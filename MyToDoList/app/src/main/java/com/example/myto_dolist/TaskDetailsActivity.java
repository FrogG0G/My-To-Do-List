package com.example.myto_dolist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.List;

public class TaskDetailsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int taskId;
    private Task currentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        dbHelper = new DatabaseHelper(this);
        taskId = getIntent().getIntExtra("task_id", -1);

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        loadTaskDetails();

        Button deleteButton = findViewById(R.id.btn_delete);
        deleteButton.setOnClickListener(v -> {
            dbHelper.deleteTask(taskId);
            Toast.makeText(this, "Задача удалена", Toast.LENGTH_SHORT).show();
            finish();
        });

        Button editButton = findViewById(R.id.btn_edit);
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(TaskDetailsActivity.this, EditTaskActivity.class);
            intent.putExtra("task_id", taskId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTaskDetails();
    }

    private void loadTaskDetails() {
        List<Task> tasks = dbHelper.getAllTasks();
        currentTask = null;
        for (Task t : tasks) {
            if (t.getId() == taskId) {
                currentTask = t;
                break;
            }
        }

        if (currentTask != null) {
            ((TextView) findViewById(R.id.text_name)).setText(currentTask.getName());
            ((TextView) findViewById(R.id.text_description)).setText(currentTask.getDescription());
            ((TextView) findViewById(R.id.text_date)).setText(currentTask.getDate());
            
            TextView priorityText = findViewById(R.id.text_priority);
            priorityText.setText(currentTask.getPriority());
            ImageView priorityIcon = findViewById(R.id.priority_icon);

            // Set priority color
            int colorRes;
            if (currentTask.getPriority().equals(getString(R.string.priority_low))) {
                colorRes = R.color.low_priority_text;
            } else if (currentTask.getPriority().equals(getString(R.string.priority_medium))) {
                colorRes = R.color.medium_priority_text;
            } else {
                colorRes = R.color.high_priority_text;
            }
            int color = ContextCompat.getColor(this, colorRes);
            priorityText.setTextColor(color);
            priorityIcon.setColorFilter(color);
            
            TextView statusBadge = findViewById(R.id.status_badge);
            statusBadge.setText(currentTask.isCompleted() ? getString(R.string.status_completed) : getString(R.string.status_active));
        }
    }
}