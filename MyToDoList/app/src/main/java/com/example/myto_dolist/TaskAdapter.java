package com.example.myto_dolist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks;
    private final OnTaskClickListener listener;
    private final DatabaseHelper dbHelper;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    public TaskAdapter(List<Task> tasks, DatabaseHelper dbHelper, OnTaskClickListener listener) {
        this.tasks = tasks;
        this.dbHelper = dbHelper;
        this.listener = listener;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int view) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task, listener, dbHelper);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView name, date, priority;
        ImageView priorityIcon;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.task_checkbox);
            name = itemView.findViewById(R.id.task_name);
            date = itemView.findViewById(R.id.task_date);
            priority = itemView.findViewById(R.id.task_priority);
            priorityIcon = itemView.findViewById(R.id.priority_icon);
        }

        public void bind(Task task, OnTaskClickListener listener, DatabaseHelper dbHelper) {
            Context context = itemView.getContext();
            name.setText(task.getName());
            date.setText(task.getDate());
            priority.setText(task.getPriority());
            checkBox.setChecked(task.isCompleted());

            // Set priority color
            int colorRes;
            if (task.getPriority().equals(context.getString(R.string.priority_low))) {
                colorRes = R.color.low_priority_text;
            } else if (task.getPriority().equals(context.getString(R.string.priority_medium))) {
                colorRes = R.color.medium_priority_text;
            } else {
                colorRes = R.color.high_priority_text;
            }
            int color = ContextCompat.getColor(context, colorRes);
            priority.setTextColor(color);
            priorityIcon.setColorFilter(color);


            updateViewStatus(task.isCompleted());

            checkBox.setOnClickListener(v -> {
                boolean checked = checkBox.isChecked();
                task.setCompleted(checked);
                dbHelper.updateTask(task);
                updateViewStatus(checked);
            });

            itemView.setOnClickListener(v -> listener.onTaskClick(task));
        }

        private void updateViewStatus(boolean isCompleted) {
            if (isCompleted) {
                name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                name.setTextColor(Color.GRAY);
            } else {
                name.setPaintFlags(name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                name.setTextColor(Color.BLACK);
            }
        }
    }
}
