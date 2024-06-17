package com.example.taskmaster.task_recycler_view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.taskmaster.AddTask;
import com.example.taskmaster.R;
import com.example.taskmaster.task_database.TaskDao;
import com.example.taskmaster.task_database.TaskDatabase;
import com.example.taskmaster.task_database.TaskModel;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Objects;

public class TaskRecyclerAdapter extends RecyclerView.Adapter<TaskRecyclerAdapter.TaskViewHolder> {

    Context context;
    ArrayList<TaskRecyclerModel> list;

    public TaskRecyclerAdapter(Context context, ArrayList<TaskRecyclerModel> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.task_view, parent, false);
        return new TaskRecyclerAdapter.TaskViewHolder(view);
    }

    @SuppressLint({"ResourceAsColor", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.task_title_view.setText(list.get(position).title);
        holder.task_time_view.setText(list.get(position).time);
        holder.task_description_view.setText(list.get(position).description);
        holder.id = list.get(position).id;
        String status = list.get(position).status;

        if (Objects.equals(status, "Completed")) {
            holder.task_status_switch_button.getThumbDrawable().setColorFilter(
                    ContextCompat.getColor(context, R.color.light_green), PorterDuff.Mode.SRC_IN);
            holder.task_status_switch_button.getTrackDrawable().setColorFilter(
                    ContextCompat.getColor(context, R.color.light_green), PorterDuff.Mode.SRC_IN);
            holder.task_status_switch_button.setChecked(true);
        } else {
            holder.task_status_switch_button.getThumbDrawable().setColorFilter(
                    ContextCompat.getColor(context, R.color.pending), PorterDuff.Mode.SRC_IN);
            holder.task_status_switch_button.getTrackDrawable().setColorFilter(
                    ContextCompat.getColor(context, R.color.pending), PorterDuff.Mode.SRC_IN);
            holder.task_status_switch_button.setChecked(false);
        }

        holder.task_card_view.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddTask.class);
            intent.putExtra("TITLE", holder.task_title_view.getText().toString());
            intent.putExtra("TIME", holder.task_time_view.getText().toString());
            intent.putExtra("DESCRIPTION", holder.task_description_view.getText().toString());
            intent.putExtra("ID", holder.id);
            context.startActivity(intent);
        });

        holder.task_status_switch_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String status;
                if (isChecked) {
                    status = "Completed";
                    holder.task_status_switch_button.getThumbDrawable().setColorFilter(
                            ContextCompat.getColor(context, R.color.light_green), PorterDuff.Mode.SRC_IN);
                    holder.task_status_switch_button.getTrackDrawable().setColorFilter(
                            ContextCompat.getColor(context, R.color.light_green), PorterDuff.Mode.SRC_IN);
                } else {
                    holder.task_status_switch_button.getThumbDrawable().setColorFilter(
                            ContextCompat.getColor(context, R.color.pending), PorterDuff.Mode.SRC_IN);
                    holder.task_status_switch_button.getTrackDrawable().setColorFilter(
                            ContextCompat.getColor(context, R.color.pending), PorterDuff.Mode.SRC_IN);
                    status = "Pending";
                }

                new DBThread(status, holder.id).start();
            }
        });

        holder.delete_task.setOnClickListener(v -> {
            TaskModel taskModel = new TaskModel(list.get(position).id, list.get(position).title, list.get(position).time, list.get(position).description, list.get(position).status);
            new DeleteTaskThread(taskModel).start();
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size());
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView task_title_view, task_time_view, task_description_view;
        CardView task_card_view;

        ImageView delete_task;


        SwitchMaterial task_status_switch_button;
        String id;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            task_title_view = itemView.findViewById(R.id.task_title_view);
            task_time_view = itemView.findViewById(R.id.task_time_view);
            task_description_view = itemView.findViewById(R.id.task_description_view);
            task_card_view = itemView.findViewById(R.id.task_card_view);
            task_status_switch_button = itemView.findViewById(R.id.task_status_switch_button);
            delete_task = itemView.findViewById(R.id.delete_task);
        }
    }

    class DBThread extends Thread {

        String status, id;

        DBThread(String status, String id) {
            this.status = status;
            this.id = id;
        }

        public void run() {
            super.run();

            TaskDatabase db = Room.databaseBuilder(context,
                            TaskDatabase.class,
                            "TaskDB"
                    )
                    .fallbackToDestructiveMigration()
                    .build();

            TaskDao dao = db.taskDao();

            dao.updateStatus(this.status, this.id);
        }
    }

    class DeleteTaskThread extends Thread {

        TaskModel taskModel;

        DeleteTaskThread(TaskModel taskModel) {
            this.taskModel = taskModel;
        }

        public void run() {
            super.run();

            TaskDatabase db = Room.databaseBuilder(context,
                            TaskDatabase.class,
                            "TaskDB"
                    )
                    .fallbackToDestructiveMigration()
                    .build();

            TaskDao dao = db.taskDao();

            dao.deleteTask(taskModel);
        }
    }
}
