package com.example.myto_dolist;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText editName, editDescription, editDate;
    private Spinner spinnerPriority;
    private DatabaseHelper dbHelper;
    private final Calendar calendar = Calendar.getInstance();
    private int taskId = -1;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        dbHelper = new DatabaseHelper(this);

        editName = findViewById(R.id.edit_task_name);
        editDescription = findViewById(R.id.edit_task_description);
        editDate = findViewById(R.id.edit_task_date);
        spinnerPriority = findViewById(R.id.spinner_priority);
        Button saveButton = findViewById(R.id.save_task_button);
        ImageView backButton = findViewById(R.id.back_button);
        TextView titleView = findViewById(R.id.create_task_toolbar_title);

        backButton.setOnClickListener(v -> finish());

        // Check for edit mode
        if (getIntent().hasExtra("task")) {
            Task task = (Task) getIntent().getSerializableExtra("task");
            if (task != null) {
                isEditMode = true;
                taskId = task.getId();
                editName.setText(task.getName());
                editDescription.setText(task.getDescription());
                editDate.setText(task.getDate());
                titleView.setText(R.string.edit_task_title);
                
                // Try to restore priority from text
                // (Assuming simple matching for demo)
            }
        }

        // Date selection
        if (!isEditMode) updateLabel();
        editDate.setOnClickListener(v -> {
            new DatePickerDialog(CreateTaskActivity.this, (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Priority spinner
        String[] priorities = {getString(R.string.priority_low), getString(R.string.priority_medium), getString(R.string.priority_high)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);
        
        if (isEditMode) {
            Task task = (Task) getIntent().getSerializableExtra("task");
            for (int i = 0; i < priorities.length; i++) {
                if (priorities[i].equals(task.getPriority())) {
                    spinnerPriority.setSelection(i);
                    break;
                }
            }
        } else {
            spinnerPriority.setSelection(1);
        }

        saveButton.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String description = editDescription.getText().toString().trim();
            String date = editDate.getText().toString().trim();
            String priority = spinnerPriority.getSelectedItem().toString();

            if (name.isEmpty()) {
                Toast.makeText(this, "Введите название задачи", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEditMode) {
                Task updatedTask = new Task(taskId, name, description, date, priority, ((Task)getIntent().getSerializableExtra("task")).isCompleted());
                dbHelper.updateTask(updatedTask);
                Toast.makeText(this, "Задача обновлена", Toast.LENGTH_SHORT).show();
            } else {
                Task newTask = new Task(name, description, date, priority, false);
                dbHelper.addTask(newTask);
                Toast.makeText(this, "Задача сохранена", Toast.LENGTH_SHORT).show();
            }
            finish();
        });
    }

    private void updateLabel() {
        String myFormat = "dd.MM.yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        editDate.setText(sdf.format(calendar.getTime()));
    }
}