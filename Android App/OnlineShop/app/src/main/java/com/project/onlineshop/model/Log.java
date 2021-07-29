package com.project.onlineshop.model;

import java.util.Date;
import java.util.UUID;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

public class Log {

    public Long id;

    public String report;
    public String endpoint;

    public Date submissionDate;

    public Log() {
        this.id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        this.submissionDate = new Date();
    }

    public Log(String report, String endpoint) {
        super();
        this.report = report;
        this.endpoint = endpoint;
    }
}
