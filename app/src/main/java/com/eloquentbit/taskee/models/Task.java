package com.eloquentbit.taskee.models;

import com.eloquentbit.taskee.utils.RealmAutoIncrement;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Task extends RealmObject {

    public static final String ID = "_id";
    public static final String TITLE = "title";

    @Required
    @PrimaryKey
    //private Integer _id = RealmAutoIncrement.getInstance().getNextIdFromModel(Task.class);
    private Integer _id;

    @Required
    private String title;
    private String description;
    private boolean isCompleted;
    private Date dueDate;
    private String priority;

    public Task() {}

    public Task(String title, String description, Date dueDate, String priority) {
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

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public static Integer getNextId() {
        return RealmAutoIncrement.getInstance().getNextIdFromModel(Task.class);
    }

    public String toString() {
        return (this.getId() + " - " + this.getTitle() + " - " + this.isCompleted());
    }
}
