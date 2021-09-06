package ru.javaops.topjava2.error;

import lombok.Getter;

import java.beans.ConstructorProperties;
import java.util.Date;


@Getter
public class ErrorInfo {
    private final Date timestamp;
    private final Integer status;
    private final String error;
    private final String message;

    @ConstructorProperties({"timestamp", "status", "error","message"})
    public ErrorInfo(Date timestamp, Integer status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ErrorInfo{" +
                "timestamp=" + timestamp +
                ", status=" + status +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
