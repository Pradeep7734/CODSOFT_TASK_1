package com.example.taskmaster;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.taskmaster.task_database.TaskDao;
import com.example.taskmaster.task_database.TaskDatabase;
import com.example.taskmaster.task_database.TaskModel;
import com.example.taskmaster.task_recycler_view.TaskRecyclerAdapter;
import com.example.taskmaster.task_recycler_view.TaskRecyclerModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton add_task_button;
    private RecyclerView task_rv;

    private TextView empty_task_textview;

    private ScrollView scroll_view;

    private final ArrayList<TaskRecyclerModel> task_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize_views();
        attachTaskToRV();

        add_task_button.setOnClickListener((v) -> startActivity(new Intent(MainActivity.this, AddTask.class)));
    }

    private void initialize_views() {
        add_task_button = findViewById(R.id.add_task_button);
        task_rv = findViewById(R.id.task_rv);
        empty_task_textview = findViewById(R.id.empty_task_textview);
        scroll_view = findViewById(R.id.scroll_view);
    }

    private void attachTaskToRV() {
        gatherTaskDataIntoList();
        TaskRecyclerAdapter adapter = new TaskRecyclerAdapter(this, task_list);
        task_rv.setAdapter(adapter);
    }

    private void gatherTaskDataIntoList() {
        new DBThread(
                new OnTaskDataLoadedCallback() {
                    @Override
                    public void onDataLoaded() {
                        if (task_list.isEmpty()) {
                            empty_task_textview.setVisibility(View.VISIBLE);
                            scroll_view.setVisibility(View.INVISIBLE);
                        } else {
                            empty_task_textview.setVisibility(View.INVISIBLE);
                            scroll_view.setVisibility(View.VISIBLE);
                        }
                    }
                }
        ).start();
    }

    class DBThread extends Thread {

        private final OnTaskDataLoadedCallback callback;

        DBThread(OnTaskDataLoadedCallback callback) {
            this.callback = callback;
        }

        public void run() {
            super.run();

            TaskDatabase db = Room.databaseBuilder(
                            getApplicationContext(),
                            TaskDatabase.class,
                            "TaskDB"
                    )
                    .fallbackToDestructiveMigration()
                    .build();

            TaskDao dao = db.taskDao();
            List<TaskModel> task_model_list = dao.getTask();
            task_model_list.sort(new Comparator<TaskModel>() {
                @SuppressLint("SimpleDateFormat")
                final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

                @Override
                public int compare(TaskModel t1, TaskModel t2) {
                    try {
                        Date d1 = sdf.parse(t1.time);
                        Date d2 = sdf.parse(t2.time);
                        assert d1 != null;
                        return d1.compareTo(d2);
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            });

            for (TaskModel tm : task_model_list) {
                String title = tm.getTitle();
                String time = tm.getTime();
                String description = tm.getDescription();
                String id = tm.getId();
                String status = tm.getStatus();

                task_list.add(new TaskRecyclerModel(title, time, description, id, status));
            }


            if (callback != null) {
                runOnUiThread(callback::onDataLoaded);
            }
        }
    }
}