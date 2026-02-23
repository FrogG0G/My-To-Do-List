package com.example.myto_dolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView lowPriorityCount, mediumPriorityCount, highPriorityCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        lowPriorityCount = findViewById(R.id.low_priority_count);
        mediumPriorityCount = findViewById(R.id.medium_priority_count);
        highPriorityCount = findViewById(R.id.high_priority_count);

        ImageView menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        Button createTaskButton = findViewById(R.id.create_task_button);
        createTaskButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateTaskActivity.class);
            startActivity(intent);
        });

        Button viewTasksButton = findViewById(R.id.view_tasks_button);
        viewTasksButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TaskListActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatistics();
    }

    private void updateStatistics() {
        lowPriorityCount.setText(String.valueOf(dbHelper.getUncompletedTaskCountByPriority(getString(R.string.priority_low))));
        mediumPriorityCount.setText(String.valueOf(dbHelper.getUncompletedTaskCountByPriority(getString(R.string.priority_medium))));
        highPriorityCount.setText(String.valueOf(dbHelper.getUncompletedTaskCountByPriority(getString(R.string.priority_high))));
    }
}