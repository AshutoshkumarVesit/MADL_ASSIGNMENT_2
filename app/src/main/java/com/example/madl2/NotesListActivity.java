package com.example.madl2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotesListActivity extends AppCompatActivity implements SensorEventListener {
    private NotesDbHelper dbHelper;
    private NotesAdapter notesAdapter;
    private TextView tvEmpty;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastShakeTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        dbHelper = new NotesDbHelper(this);
        tvEmpty = findViewById(R.id.tvEmpty);

        RecyclerView rvNotes = findViewById(R.id.rvNotes);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        notesAdapter = new NotesAdapter();
        rvNotes.setAdapter(notesAdapter);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        loadNotes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    private void loadNotes() {
        List<Note> notes = dbHelper.getAllNotes();
        notesAdapter.submitList(notes);
        tvEmpty.setVisibility(notes.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        double acceleration = Math.sqrt((x * x) + (y * y) + (z * z)) - SensorManager.GRAVITY_EARTH;

        long now = System.currentTimeMillis();
        if (Math.abs(acceleration) > 12 && now - lastShakeTime > 1000) {
            lastShakeTime = now;
            Toast.makeText(this, R.string.motion_detected, Toast.LENGTH_SHORT).show();
            loadNotes();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No-op
    }
}
