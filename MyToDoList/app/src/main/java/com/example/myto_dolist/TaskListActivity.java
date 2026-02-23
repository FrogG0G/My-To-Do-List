package com.example.myto_dolist;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskListActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Task> allTasks = new ArrayList<>();
    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        dbHelper = new DatabaseHelper(this);

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        ImageView settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(TaskListActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        searchBar = findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTasks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        View filterSortButton = findViewById(R.id.filter_sort_button);
        View filterBlock = findViewById(R.id.filter_block);
        filterSortButton.setOnClickListener(v -> {
            filterBlock.setVisibility(filterBlock.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        });

        setupPriorityFilters();
        setupSortSpinner();

        recyclerView = findViewById(R.id.tasks_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        loadTasks();
    }

    private void loadTasks() {
        allTasks = dbHelper.getAllTasks();
        adapter = new TaskAdapter(allTasks, dbHelper, this);
        recyclerView.setAdapter(adapter);
    }

    private void filterTasks(String query) {
        List<Task> filteredList = new ArrayList<>();
        for (Task task : allTasks) {
            if (task.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(task);
            }
        }
        adapter.setTasks(filteredList);
    }

    private void setupPriorityFilters() {
        LinearLayout priorityFilters = findViewById(R.id.priority_filters);
        for (int i = 0; i < priorityFilters.getChildCount(); i++) {
            TextView filterView = (TextView) priorityFilters.getChildAt(i);
            filterView.setOnClickListener(v -> {
                for (int j = 0; j < priorityFilters.getChildCount(); j++) {
                    priorityFilters.getChildAt(j).setBackgroundResource(R.drawable.settings_item_background);
                }
                v.setBackgroundResource(R.drawable.settings_item_selected);
                
                String priority = ((TextView) v).getText().toString();
                if (priority.equals(getString(R.string.filter_all))) {
                    adapter.setTasks(allTasks);
                } else {
                    List<Task> filtered = new ArrayList<>();
                    for (Task t : allTasks) {
                        if (t.getPriority().equals(priority)) filtered.add(t);
                    }
                    adapter.setTasks(filtered);
                }
            });
        }
    }

    private void setupSortSpinner() {
        Spinner sortSpinner = findViewById(R.id.sort_spinner);
        String[] options = getResources().getStringArray(R.array.sort_options);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortAndDisplayTasks(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void sortAndDisplayTasks(int position) {
        List<Task> tasksToSort = dbHelper.getAllTasks();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        switch (position) {
            case 0: // Дата (Oldest First)
                Collections.sort(tasksToSort, (t1, t2) -> {
                    try { return sdf.parse(t1.getDate()).compareTo(sdf.parse(t2.getDate())); } catch (ParseException e) { return 0; }
                });
                break;
            case 1: // Дата (Newest First)
                Collections.sort(tasksToSort, (t1, t2) -> {
                    try { return sdf.parse(t2.getDate()).compareTo(sdf.parse(t1.getDate())); } catch (ParseException e) { return 0; }
                });
                break;
            case 2: // По алфавиту (A-Z)
                Collections.sort(tasksToSort, (t1, t2) -> t1.getName().compareToIgnoreCase(t2.getName()));
                break;
            case 3: // По алфавиту (Z-A)
                Collections.sort(tasksToSort, (t1, t2) -> t2.getName().compareToIgnoreCase(t1.getName()));
                break;
            case 4: // Приоритет (High to Low)
                Collections.sort(tasksToSort, (t1, t2) -> Integer.compare(getPriorityValue(t2.getPriority()), getPriorityValue(t1.getPriority())));
                break;
            case 5: // Приоритет (Low to High)
                Collections.sort(tasksToSort, (t1, t2) -> Integer.compare(getPriorityValue(t1.getPriority()), getPriorityValue(t2.getPriority())));
                break;
        }
        adapter.setTasks(tasksToSort);
    }
    
    private int getPriorityValue(String priority) {
        if (priority.equals(getString(R.string.priority_high))) return 3;
        if (priority.equals(getString(R.string.priority_medium))) return 2;
        if (priority.equals(getString(R.string.priority_low))) return 1;
        return 0;
    }

    @Override
    public void onTaskClick(Task task) {
        Intent intent = new Intent(this, TaskDetailsActivity.class);
        intent.putExtra("task_id", task.getId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }
}