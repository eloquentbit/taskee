package com.eloquentbit.taskee.models;

import com.eloquentbit.taskee.utils.RealmAutoIncrement;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Task extends RealmObject {

    public static final String TITLE = "title";

    @Required
    @PrimaryKey
    private Integer _id = RealmAutoIncrement.getInstance().getNextIdFromModel(Task.class);

    @Required
    private String title;
    private String description;
    private boolean isCompleted;
    private Date dueDate;

    public Task() {}

    public Task(String title, String description, Date dueDate) {
        this.title = title;
        this.description = description;
        this.isCompleted = false;
        this.dueDate = dueDate;
    }

    public Integer getId() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
}
