package com.example.taskmaster.task_recycler_view;

public class TaskRecyclerModel {

    public String title, time, description, id, status;

    public TaskRecyclerModel(String title, String time, String description, String id, String status) {
        this.title = title;
        this.time = time;
        this.description = description;
        this.id = id;
        this.status = status;
    }
}