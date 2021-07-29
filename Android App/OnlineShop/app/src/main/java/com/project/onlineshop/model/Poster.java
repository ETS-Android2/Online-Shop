package com.project.onlineshop.model;

import android.media.Image;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import io.objectbox.annotation.*;
import io.objectbox.converter.PropertyConverter;
import io.objectbox.relation.RelationInfo;
import io.objectbox.relation.ToOne;

@Entity
public class Poster {

    @Id(assignable = true)
    public Long id;

    public String name;

    public ToOne<User> owner;
    public String owner_phone_number = null;

    public byte[] poster_image;

    public double price;

    public Date submissionDate;

    // PosterGroup Enum
    @Convert(converter = GroupConverter.class, dbType = Integer.class)
    public PosterGroup posterGroup;
    public enum PosterGroup {
        OTHER(0),
        INDUSTRIAL(1),
        FURNITURE(2),
        ELECTRONIC(3),
        CULTURAL(4),
        ENTERTAINMENT(5);

        final int id;

        PosterGroup(int id) {
            this.id = id;
        }
    }
    public static class GroupConverter implements PropertyConverter<PosterGroup, Integer> {
        @Override
        public PosterGroup convertToEntityProperty(Integer databaseValue) {
            if (databaseValue == null) {
                return null;
            }
            for (PosterGroup group : PosterGroup.values()) {
                if (group.id == databaseValue) {
                    return group;
                }
            }
            return PosterGroup.OTHER;
        }

        @Override
        public Integer convertToDatabaseValue(PosterGroup entityProperty) {
            return entityProperty == null ? null : entityProperty.id;
        }
    }

    public String description;
    public int priority = 0;

    public Poster() {
        this.id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        this.submissionDate = new Date();
        this.owner = new ToOne<User>(this, Poster_.owner);
        this.priority = 0;
    }

    public Poster(User owner, String name, String description, byte[] poster_image, PosterGroup posterGroup, double price) {
        this();
        this.setOwner(owner);
        this.name = name;
        this.description = description;
        this.poster_image = poster_image;
        this.posterGroup = posterGroup;
        this.price = price;
    }

    public Poster(User owner, String name, double price) {
        this(owner, name, null, null, PosterGroup.OTHER, price);
    }

    public Poster(User owner, String name) {
        this(owner, name, 0);
    }

    @Override
    public boolean equals(Object obj) {
        if( obj == null || !(obj instanceof Poster) )
            return false;
        return this.id.equals(((Poster) obj).id);
    }

    @Override
    public int hashCode() {
        return this.id.intValue();
    }

    public void setOwner(User user) {
        this.owner = new ToOne<User>(this, Poster_.owner);
        this.owner.setTarget(user);
        this.owner_phone_number = user.phone_number;
    }

}
