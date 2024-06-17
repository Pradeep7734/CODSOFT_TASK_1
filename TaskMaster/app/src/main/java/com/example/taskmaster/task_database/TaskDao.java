package com.example.taskmaster.task_database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insertTask(TaskModel taskModel);


    @Query("SELECT * FROM TaskModel")
    List<TaskModel> getTask();

    @Query("UPDATE TaskModel SET title=:title, time=:time, description=:description WHERE id=:id")
    void updateTask(String title, String time, String description, String id);


    @Query("UPDATE TaskModel SET status=:status WHERE id=:id")
    void updateStatus(String status, String id);

    @Delete
    void deleteTask(TaskModel taskModel);
}
