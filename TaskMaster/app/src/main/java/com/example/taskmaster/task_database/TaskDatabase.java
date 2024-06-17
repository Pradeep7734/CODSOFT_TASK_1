package com.example.taskmaster.task_database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {TaskModel.class}, version = 4)
public abstract class TaskDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();
}
