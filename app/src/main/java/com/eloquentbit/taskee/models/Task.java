package com.eloquentbit.taskee.models;

import com.eloquentbit.taskee.utils.RealmAutoIncrement;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Task extends RealmObject {

    public static final String ID = "_id";
    public static final String TITLE = "title";
    public static final String PRIORITY = "priority";

    @Required
    @PrimaryKey
    private Integer _id;

    @Required
    private String title;
    private String description;
    private boolean isCompleted;
    private String dueDate;
    private int priority;

    public Task() {}

    public Task(String title, String description, String dueDate, int priority) {
        this.title = title;
        this.description = description;
        this.isCompleted = false;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public Integer getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
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

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public static Integer getNextId() {
        return RealmAutoIncrement.getInstance().getNextIdFromModel(Task.class);
    }

    public String toString() {
        return (this.getId() + " - " + this.getTitle() + " - " + this.isCompleted() + " - "
                + " - " + this.getPriority() + " - " + this.getDueDate());
    }
}
