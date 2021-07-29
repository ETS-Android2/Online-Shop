package com.project.onlineshop.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;

import java.nio.ByteBuffer;
import java.util.*;

import io.objectbox.annotation.*;
import io.objectbox.converter.PropertyConverter;
import io.objectbox.relation.ToMany;

@Entity
public class User {

    @Id(assignable = true)
    public Long id;

    // UserType Enum
    @Convert(converter = TypeConverter.class, dbType = Integer.class)
    public UserType type;
    public enum UserType {
        USER(0),
        ADMIN(1);

        final int id;

        UserType(int id) {
            this.id = id;
        }
    }
    public static class TypeConverter implements PropertyConverter<UserType, Integer> {
        @Override
        public UserType convertToEntityProperty(Integer databaseValue) {
            if (databaseValue == null) {
                return null;
            }
            for (UserType type : UserType.values()) {
                if (type.id == databaseValue) {
                    return type;
                }
            }
            return UserType.USER;
        }

        @Override
        public Integer convertToDatabaseValue(UserType entityProperty) {
            return entityProperty == null ? null : entityProperty.id;
        }
    }

    public String phone_number;
    public String email;
    public String password;

    @Backlink(to = "owner")
    public List<Poster> posters;

    public List<Poster> bookmarks;

    @Transient
    public List<Log> logs;

    public String name;
    public String family;

    public byte[] avatar;

    public int posterscnt;

    public Boolean isConfirmed = false; // flag to keep whether email is confirmed or not
    public Date signup_date;

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
        this.name = user.name;
        this.family = user.family;
        this.phone_number = user.phone_number;
        this.avatar = user.avatar;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if( obj == null || !(obj instanceof User) )
            return false;
        return this.id.equals(((User)obj).id);
    }

    @Override
    public int hashCode() {
        final int prime = 37633;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.email == null) ? 0 : this.email.hashCode());
        return result;
    }

    public UserType getType() {
        return type;
    }

    public Boolean isBookmarked(Poster poster) {
        return this.bookmarks.contains(poster);
    }

    public Boolean hasPoster(Poster poster) {
        return this.posters.contains(poster);
    }

    public User handleNulls() {
        if(this.posters == null)
            this.posters = new ArrayList<>();

        if(this.bookmarks == null)
            this.bookmarks = new ArrayList<>();

        return this;
    }

    public Boolean isAdmin() {
        return this.type == UserType.ADMIN;
    }
}
