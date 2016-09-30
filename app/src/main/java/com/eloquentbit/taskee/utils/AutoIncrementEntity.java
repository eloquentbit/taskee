package com.eloquentbit.taskee.utils;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class AutoIncrementEntity extends RealmObject {

    @Required
    @PrimaryKey
    private Integer _id = 1;

    private Integer task;

    public Integer getId() {
        return _id;
    }

    public Integer getTask() {
        return task;
    }

    public void setTask(Integer task) {
        this.task = task;
    }

    public void incrementByClassName(String className) {
        switch (className) {
            case KnownClasses.TASK:
                task = task == null ? 1 : ++task;
                break;
            default:
                throw new UnknownModelException("Class name: " + className);

        }
    }

    public Integer findByClassName(String className) {
        switch (className) {
            case KnownClasses.TASK:
                return  this.task;
            default:
                throw new UnknownModelException("Class name: " + className);
        }
    }
}
