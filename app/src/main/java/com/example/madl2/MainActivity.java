package com.example.madl2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private EditText etTitle;
    private EditText etDescription;
    private Spinner spPriority;
    private ImageView ivSelected;
    private NotesDbHelper notesDbHelper;

    private Uri cameraImageUri;
    private String selectedImagePath;

    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<String> requestNotificationPermissionLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String[]> openDocumentLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notesDbHelper = new NotesDbHelper(this);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        spPriority = findViewById(R.id.spPriority);
        ivSelected = findViewById(R.id.ivSelected);
        Button btnMedia = findViewById(R.id.btnMedia);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnView = findViewById(R.id.btnView);

        setupPrioritySpinner();
        setupActivityResultLaunchers();
        requestNotificationPermissionIfRequired();
        enqueuePeriodicReminder();

        btnMedia.setOnClickListener(view -> showMediaChoiceDialog());
        btnSave.setOnClickListener(view -> saveNote());
        btnView.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NotesListActivity.class);
            startActivity(intent);
        });
    }

    private void setupPrioritySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.priority_options,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPriority.setAdapter(adapter);
    }

    private void setupActivityResultLaunchers() {
        requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        launchCameraCapture();
                    } else {
                        Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                    }
                });

        requestNotificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    // No-op: notifications will run when permission is granted.
                });

        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                isSuccess -> {
                    if (isSuccess && cameraImageUri != null) {
                        selectedImagePath = cameraImageUri.toString();
                        ivSelected.setImageURI(cameraImageUri);
                    }
                });

        openDocumentLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        try {
                            getContentResolver().takePersistableUriPermission(
                                    uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (SecurityException ignored) {
                            // Some providers do not support persistent grants.
                        }
                        selectedImagePath = uri.toString();
                        ivSelected.setImageURI(uri);
                    }
                });
    }

    private void showMediaChoiceDialog() {
        String[] choices = { "Capture Image", "Select Image" };
        new AlertDialog.Builder(this)
                .setTitle("Add Image")
                .setItems(choices, (dialogInterface, which) -> {
                    if (which == 0) {
                        if (ContextCompat.checkSelfPermission(this,
                                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            launchCameraCapture();
                        } else {
                            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                        }
                    } else {
                        openDocumentLauncher.launch(new String[] { "image/*" });
                    }
                })
                .show();
    }

    private void launchCameraCapture() {
        try {
            File imageFile = createImageFile();
            cameraImageUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    imageFile);
            takePictureLauncher.launch(cameraImageUri);
        } catch (IOException exception) {
            Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir == null) {
            throw new IOException("Storage unavailable");
        }
        String timestamp = String.valueOf(System.currentTimeMillis());
        return File.createTempFile("note_" + timestamp, ".jpg", storageDir);
    }

    private void saveNote() {
        String title = etTitle.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Title is required");
            return;
        }

        String description = etDescription.getText().toString().trim();
        String priority = String.valueOf(spPriority.getSelectedItem());
        String date = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date());

        Note note = new Note();
        note.setTitle(title);
        note.setDescription(description);
        note.setImagePath(selectedImagePath);
        note.setDate(date);
        note.setPriority(priority);

        long rowId = notesDbHelper.insertNote(note);
        if (rowId > 0) {
            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
            clearForm();
        } else {
            Toast.makeText(this, "Failed to save note", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        etTitle.setText("");
        etDescription.setText("");
        spPriority.setSelection(0);
        selectedImagePath = null;
        cameraImageUri = null;
        ivSelected.setImageDrawable(null);
    }

    private void requestNotificationPermissionIfRequired() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this,
                        Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void enqueuePeriodicReminder() {
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                NotificationWorker.class,
                1,
                TimeUnit.MINUTES).build();

        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork(
                AppConstants.WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest);
    }
}