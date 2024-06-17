package com.example.taskmaster;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.taskmaster.task_database.TaskDao;
import com.example.taskmaster.task_database.TaskDatabase;
import com.example.taskmaster.task_database.TaskModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class AddTask extends AppCompatActivity {

    private TextView add_task_cancel, add_task_done;
    private EditText task_title, task_time, task_description;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initialize_views();
        intent = getIntent();

        if (intent.hasExtra("ID")){
            task_title.setText(intent.getStringExtra("TITLE"));
            task_time.setText(intent.getStringExtra("TIME"));
            task_description.setText(intent.getStringExtra("DESCRIPTION"));
        }

        task_time.setOnClickListener((v) -> setTaskTime());

        add_task_cancel.setOnClickListener((v) -> startActivity(new Intent(AddTask.this, MainActivity.class)));

        add_task_done.setOnClickListener(v -> {
            String title = task_title.getText().toString();
            String time = task_time.getText().toString();
            String description = task_description.getText().toString();
            String id;
            boolean isInsert;

            if (title.isEmpty()) {
                return;
            }

            if (intent.hasExtra("ID")){
                id = intent.getStringExtra("ID");
                isInsert = false;
            }
            else{
                id = generateUUID(title, time, description);
                isInsert = true;
            }
            addTask(title, time, description, id, isInsert);
        });
    }

    private void initialize_views() {
        add_task_done = findViewById(R.id.add_task_done);
        add_task_cancel = findViewById(R.id.add_task_cancel);
        task_title = findViewById(R.id.task_title);
        task_time = findViewById(R.id.task_time);
        task_description = findViewById(R.id.task_description);
    }

//    private void setTaskTime() {
//        @SuppressLint("SetTextI18n") TimePickerDialog time_picker = new TimePickerDialog(
//                this,
//                (view, hourOfDay, minute) -> task_time.setText(hourOfDay + ":" + minute),
//                12, 15, false
//        );
//
//        time_picker.show();
//    }

    private void setTaskTime() {
        @SuppressLint("SetTextI18n") TimePickerDialog time_picker = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                    String formattedTime = sdf.format(calendar.getTime());
                    task_time.setText(formattedTime);
                },
                12, 15, false // 12:15 PM as default time, 12-hour view
        );

        time_picker.show();
    }

    private void addTask(String title, String time, String description, String id, boolean isInsert) {
        addTaskToDatabase(title, time, description, id, isInsert);
        startActivity(new Intent(AddTask.this, MainActivity.class));
        finish();
    }

    private void addTaskToDatabase(String title, String time, String description, String id, boolean isInsert) {
        new DBThread(title, time, description, id, isInsert).start();
    }

    class DBThread extends Thread {

        String title, time, description, task_id;
        boolean isInsert;

        DBThread(String title, String time, String description, String task_id, boolean isInsert) {
            this.title = title;
            this.time = time;
            this.description = description;
            this.task_id = task_id;
            this.isInsert = isInsert;
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
            if (this.isInsert) {
                TaskModel taskModel = new TaskModel(task_id, title, time, description, "Pending");
                dao.insertTask(taskModel);
            }
            else {
                dao.updateTask(title, time, description, task_id);
            }
        }
    }

    private String generateUUID(String... variables) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String variable : variables) {
            stringBuilder.append(variable);
        }
        String combinedString = stringBuilder.toString();
        UUID uuid = UUID.nameUUIDFromBytes(combinedString.getBytes());
        return uuid.toString();
    }
}