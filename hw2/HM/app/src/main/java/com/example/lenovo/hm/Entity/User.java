package com.example.lenovo.hm.Entity;
import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class User {

    @SerializedName("username")
    public String username;

    @SerializedName("psd")
    public String psd;

    @SerializedName("email")
    public String email;

    @Override
    public String toString() {
        return "User{" +
                " username='" + username + '\'' +
                ", psd='" + psd + '\'' +
                ", email='" + email + '\'' +
                '}';
    }


}
