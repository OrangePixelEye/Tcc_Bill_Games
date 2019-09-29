package com.bento.tcc_bill_games;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String uuid,
            username,
            profile_url,
            name,
            email,
            phone;
    private List<String> areaM,lineM;

    public User(){}

    public User(String uuid, String username, String profile_url, String name,String phone, String email,List<String> areaM,List<String> lineM) {
        this.uuid = uuid;
        this.username = username;
        this.profile_url = profile_url;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.areaM = areaM;
        this.lineM = lineM;
    }

    public String getEmail() {
        return email;
    }


    public List<String> getAreaM() {
        return areaM;
    }

    public List<String> getLineM() {
        return lineM;
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