package com.example.demo.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.id.UUIDGenerator;
import org.hibernate.annotations.GenericGenerator;


@Getter
@Setter
@ToString
@Entity
@Table(name = "tokens")
public class ConfirmationToken {

    @Setter(value = AccessLevel.NONE)
    @Id
    private String id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime confirmedAt = null;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User owner;

    public ConfirmationToken() {
        this.id = UUID.randomUUID().toString();
        this.token = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusMinutes(15);
    }

    public ConfirmationToken(User user) {
        this();
        this.owner = user;
    }

    public void setConfirmed() {
        if (this.confirmedAt == null) {
            this.confirmedAt = LocalDateTime.now();
            this.owner.Activate();
        }
    }

    public Boolean isConfirmed() {
        return this.confirmedAt != null;
    }
}
