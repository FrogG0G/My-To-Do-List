package com.example.myto_dolist;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditTaskActivity extends AppCompatActivity {

    private EditText editName, editDescription, editDate;
    private Spinner spinnerPriority;
    private DatabaseHelper dbHelper;
    private final Calendar calendar = Calendar.getInstance();
    private int taskId;
    private Task currentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        dbHelper = new DatabaseHelper(this);
        taskId = getIntent().getIntExtra("task_id", -1);

        editName = findViewById(R.id.edit_task_name);
        editDescription = findViewById(R.id.edit_task_description);
        editDate = findViewById(R.id.edit_task_date);
        spinnerPriority = findViewById(R.id.spinner_priority);
        Button saveButton = findViewById(R.id.save_task_button);
        ImageView backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> finish());

        loadTaskData();

        editDate.setOnClickListener(v -> {
            new DatePickerDialog(EditTaskActivity.this, (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        saveButton.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String description = editDescription.getText().toString().trim();
            String date = editDate.getText().toString().trim();
            String priority = spinnerPriority.getSelectedItem().toString();

            if (name.isEmpty()) {
                Toast.makeText(this, "Название не может быть пустым", Toast.LENGTH_SHORT).show();
                return;
            }

            currentTask.setName(name);
            currentTask.setDescription(description);
            currentTask.setDate(date);
            currentTask.setPriority(priority);

            dbHelper.updateTask(currentTask);
            Toast.makeText(this, "Задача обновлена", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void loadTaskData() {
        List<Task> tasks = dbHelper.getAllTasks();
        for (Task t : tasks) {
            if (t.getId() == taskId) {
                currentTask = t;
                break;
            }
        }

        if (currentTask != null) {
            editName.setText(currentTask.getName());
            editDescription.setText(currentTask.getDescription());
            editDate.setText(currentTask.getDate());

            String[] priorities = {getString(R.string.priority_low), getString(R.string.priority_medium), getString(R.string.priority_high)};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorities);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPriority.setAdapter(adapter);

            for (int i = 0; i < priorities.length; i++) {
                if (priorities[i].equals(currentTask.getPriority())) {
                    spinnerPriority.setSelection(i);
                    break;
                }
            }
        }
    }

    private void updateLabel() {
        String myFormat = "dd.MM.yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        editDate.setText(sdf.format(calendar.getTime()));
    }
}