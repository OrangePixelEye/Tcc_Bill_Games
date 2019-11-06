package com.bento.tcc_bill_games;

import java.io.Serializable;
import java.util.List;

public class Project implements Serializable {
    private String project_id;
    private String uuid;
    private String name;
    private String gd;
    private String description;
    private String project_url;
    private String date_added;
    private List<String> areaN,lineN;

    public Project() {
    }

    public Project(String GameCategory, String project_id, String uuid, String name, String description,String project_url,String date,List<String> areaN, List<String> lineN) {
        this.gd = GameCategory;
        this.project_id = project_id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.project_url = project_url;
        this.date_added = date;
        this.areaN = areaN;
        this.lineN = lineN;
    }
    public String getProject_id() {
        return this.project_id;
    }

    public String getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getGd() {
        return this.gd;
    }

    public String getDate_added(){return this.date_added;}

    public String getProject_url() {
        return this.project_url;
    }

    public List<String> getAreaN() {
        return areaN;
    }

    public List<String> getLineN() {
        return lineN;
    }

}