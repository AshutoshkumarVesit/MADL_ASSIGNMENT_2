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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private EditText etTitle;
    private EditText etDescription;
    private Spinner spReminderFlag;
    private ImageView ivSelected;
    private NotesDbHelper notesDbHelper;

    private Uri cameraImageUri;
    private String selectedImagePath;
    private String pendingCameraImagePath;

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
        spReminderFlag = findViewById(R.id.spReminderFlag);
        ivSelected = findViewById(R.id.ivSelected);
        Button btnMedia = findViewById(R.id.btnMedia);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnView = findViewById(R.id.btnView);

        setupReminderFlagSpinner();
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

    private void setupReminderFlagSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.reminder_flag_options,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spReminderFlag.setAdapter(adapter);
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
                    if (isSuccess && !TextUtils.isEmpty(pendingCameraImagePath)) {
                        Uri localFileUri = Uri.fromFile(new File(pendingCameraImagePath));
                        selectedImagePath = localFileUri.toString();
                        ivSelected.setImageURI(null);
                        ivSelected.setImageURI(localFileUri);
                    } else {
                        pendingCameraImagePath = null;
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

                        String copiedImagePath = copySelectedImageToAppStorage(uri);
                        if (!TextUtils.isEmpty(copiedImagePath)) {
                            Uri localFileUri = Uri.fromFile(new File(copiedImagePath));
                            selectedImagePath = localFileUri.toString();
                            ivSelected.setImageURI(null);
                            ivSelected.setImageURI(localFileUri);
                        } else {
                            Toast.makeText(this, "Unable to load selected image", Toast.LENGTH_SHORT).show();
                        }
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
            pendingCameraImagePath = imageFile.getAbsolutePath();
            cameraImageUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    imageFile);
            takePictureLauncher.launch(cameraImageUri);
        } catch (IOException exception) {
            Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show();
        }
    }

    private String copySelectedImageToAppStorage(Uri sourceUri) {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir == null) {
            return null;
        }

        File destinationFile = new File(storageDir, "selected_" + System.currentTimeMillis() + ".jpg");
        try (InputStream inputStream = getContentResolver().openInputStream(sourceUri);
                OutputStream outputStream = new FileOutputStream(destinationFile)) {
            if (inputStream == null) {
                return null;
            }

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            return destinationFile.getAbsolutePath();
        } catch (IOException exception) {
            return null;
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
        String reminderFlag = String.valueOf(spReminderFlag.getSelectedItem());
        String date = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date());

        Note note = new Note();
        note.setTitle(title);
        note.setDescription(description);
        note.setImagePath(selectedImagePath);
        note.setDate(date);
        note.setReminderFlag(reminderFlag);

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
        spReminderFlag.setSelection(0);
        selectedImagePath = null;
        cameraImageUri = null;
        pendingCameraImagePath = null;
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