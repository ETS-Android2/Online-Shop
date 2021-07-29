package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.awt.*;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Table(name = "posters")
public class Poster {

    @Setter(value = AccessLevel.NONE)
    @Id
    private Long id;
    //@Column(nullable = false)
    private String name;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private User owner;
    private String owner_phone_number = null;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] poster_image = null;

    @Enumerated(EnumType.STRING)
    private PosterGroup posterGroup;
    private double price;

    //@Column(length = 512)
    private String description;
    private int priority = 0;

    private Date submissionDate;

    public Poster() {
        this.id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        this.submissionDate = new Date();
        this.priority = 0;
        this.price = 0;
    }

    public Poster(String name, User owner, PosterGroup posterGroup) {
        this();
        this.name = name;
        this.owner = owner;
        this.priority = 0;
        this.posterGroup = posterGroup;
    }

    public Poster(User owner, String name, byte[] poster_image, String description, PosterGroup posterGroup, int price) {
        this();
        this.owner = owner;
        this.name = name;
        this.poster_image = poster_image;
        this.description = description;
        this.posterGroup = posterGroup;
        this.price = price;
    }

    public Poster updatePoster(Poster poster) {
        this.name = poster.getName();
        this.poster_image = poster.getPoster_image();
        this.description = poster.getDescription();
        this.posterGroup = poster.getPosterGroup();
        this.price = poster.getPrice();
        this.priority = poster.getPriority();
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if( obj == null || !(obj instanceof Poster) )
            return false;
        return this.id.equals(((Poster)obj).getId());
    }

    @Override
    public int hashCode() {
        final int prime = 337091;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }

    public void setOwner(User user) {
        this.owner = user;
        this.owner_phone_number = user.getPhone_number();
    }

}
