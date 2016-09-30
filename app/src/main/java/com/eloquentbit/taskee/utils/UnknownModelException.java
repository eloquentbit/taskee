package com.eloquentbit.taskee.utils;

public class UnknownModelException extends RuntimeException {

    public UnknownModelException() {

    }

    public UnknownModelException(String message) {
        super(message);
    }
}
