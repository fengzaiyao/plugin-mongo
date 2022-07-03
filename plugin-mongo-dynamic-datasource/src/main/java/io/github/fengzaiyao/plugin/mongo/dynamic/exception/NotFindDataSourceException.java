package io.github.fengzaiyao.plugin.mongo.dynamic.exception;

public class NotFindDataSourceException extends RuntimeException {

    public NotFindDataSourceException(String message) {
        super(message);
    }

    public NotFindDataSourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
