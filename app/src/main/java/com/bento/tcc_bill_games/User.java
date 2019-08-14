package com.bento.tcc_bill_games;

import java.io.Serializable;

public class User implements Serializable {
    private String uuid;
    private String username;
    private String profile_url;
    private String name;

    public User(){

    }

    public User(String uuid, String username, String profile_url, String name) {
        this.uuid = uuid;
        this.username = username;
        this.profile_url = profile_url;
        this.name = name;
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
