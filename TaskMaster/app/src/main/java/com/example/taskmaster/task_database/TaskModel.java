package com.example.taskmaster.task_database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TaskModel {
    public TaskModel(@NonNull String id, String title, String time, String description, String status) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.description = description;
        this.status = status;
    }

    @NonNull
    @PrimaryKey
    public String id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "time")
    public String time;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "status")
    public String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
