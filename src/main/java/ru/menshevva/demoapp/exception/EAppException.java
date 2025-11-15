package ru.menshevva.demoapp.exception;

public class EAppException extends RuntimeException {

    public EAppException(String errMsg) {
        super(errMsg);
    }
}
