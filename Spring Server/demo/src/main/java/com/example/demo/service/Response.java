package com.example.demo.service;

public class Response {

    private Boolean result; // 0 for FAILURE, 1 for SUCCESS
    private String message;

    public Response(String message, Boolean result) {
        this.result = result;
        this.message = message;
    }

    public Response(String message) {
        this(message, false);
    }

    public Boolean getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }
}
