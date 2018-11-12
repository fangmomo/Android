package com.github.ikidou.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable
public class User {

    @DatabaseField
    public Date date = new Date();

    @DatabaseField(canBeNull = false)
    public String username;

    @DatabaseField(canBeNull = false)
    public String psd;

    @DatabaseField(canBeNull = false)
    public String email;

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", psd='" + psd + '\'' +
                ", email='" + email + '\'' +
                '}';
    }


}
