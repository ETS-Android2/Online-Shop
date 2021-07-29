package com.example.demo.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.awt.*;
import java.util.*;

@Setter
@Getter
@ToString
@Entity
@Table(name = "users")
public class User {

    @Setter(value = AccessLevel.NONE)
    @Id
    private Long id;

    @Setter(value = AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
    private UserType type = UserType.USER;

    @Column(nullable = false)
    private String phone_number;
    @Email(message = "Email is not valid!")
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private Set<Poster> posters = new HashSet<>();

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "bookmarks_id")
    private Set<Poster> bookmarks = new HashSet<>();

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Log> logs = new HashSet<>();

    private String name;
    private String family;

    private int posterscnt;

    @Lob
    @Column(columnDefinition = "LONGBLOB", length = 51200000)
    @Setter(value = AccessLevel.NONE) private byte[] avatar;

    private Boolean isConfirmed = false; // flag to keep whether email is confirmed or not
    private Date signup_date;

    public User() {
        this.id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        this.posterscnt = 0;
        this.signup_date = new Date();
    }

    public User(String name, String family, String phone_number, String email, String password) {
        this();
        this.name = name;
        this.family = family;
        this.phone_number = phone_number;
        this.email = email;
        this.password = password;
    }

    public User updateUser(User user) {
        if( user != null ) {
            this.name = user.getName();
            this.family = user.getFamily();
            this.phone_number = user.getPhone_number();
            this.avatar = user.getAvatar();
        }
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if( obj == null || !(obj instanceof User) )
            return false;
        return this.id.equals(((User)obj).getId());
    }

    @Override
    public int hashCode() {
        final int prime = 37633;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.email == null) ? 0 : this.email.hashCode());
        return result;
    }

    public void toggleBookmark(Poster poster) {
        if( this.bookmarks.contains(poster) )
            this.bookmarks.remove(poster);
        else
            this.bookmarks.add(poster);
    }

    public void addLog(Log log) {
        this.logs.add(log);
    }

    public void addPoster(Poster poster) {
        posters.add(poster);
    }

    public Boolean isAdmin() {
        return this.type == UserType.ADMIN;
    }

    public Boolean hasPoster(Poster poster) {
        return this.posters.contains(poster);
    }

    public void Activate() { this.isConfirmed = true; }
}
