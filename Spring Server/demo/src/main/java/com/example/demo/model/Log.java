package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Date;
import java.util.UUID;

@Getter
@Entity
@Table(name = "logs")
public class Log {

    @Id
    private Long id;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User owner;

    private String report;
    private String endpoint;

    private Date submissionDate;

    public Log() {
        this.id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        this.submissionDate = new Date();
    }

    public Log(User owner, String report, String endpoint) {
        this();
        this.owner = owner;
        this.report = report;
        this.endpoint = endpoint;
        owner.addLog(this);
    }
}
