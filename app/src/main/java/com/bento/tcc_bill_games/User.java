package com.bento.tcc_bill_games;

import java.io.Serializable;

public class User implements Serializable {
    private String uuid,
            username,
            profile_url,
            name,
            email,
            phone,
            area,
            line;

    public String getEmail() {
        return email;
    }

    public User(){}

    public User(String uuid, String username, String profile_url, String name, String area,String line,String phone, String email) {
        this.uuid = uuid;
        this.username = username;
        this.profile_url = profile_url;
        this.name = name;
        this.area = area;
        this.line = line;
        this.phone = phone;
        this.email = email;
    }

    public String getArea() {
        return area;
    }

    public String getLine() {
        return line;
    }

    public String getPhone() {
        return phone;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public String getName() {
        return name;
    }
}