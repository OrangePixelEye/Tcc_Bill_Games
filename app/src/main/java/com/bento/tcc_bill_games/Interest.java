package com.bento.tcc_bill_games;

public class Interest {
    String uuid;
    String name;
    String proj_id;
    String proj_name;

    public Interest(){}
    public Interest(String uid, String name,String p_id, String p_name){
        this.uuid = uid;
        this.name = name;
        this.proj_id = p_id;
        this.proj_name = p_name;
    }
    public String getUuid() {
        return uuid;
    }

    public String getProj_id() {
        return proj_id;
    }

    public String getProj_name() {
        return proj_name;
    }

    public String getName() {
        return name;
    }
}
