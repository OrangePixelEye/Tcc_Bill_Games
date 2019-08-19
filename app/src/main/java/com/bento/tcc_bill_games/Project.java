package com.bento.tcc_bill_games;

import java.io.Serializable;

public class Project implements Serializable {
    private String project_id;
    private String uuid;
    private String name;
    private String gd;
    private String description;

    public Project() {
    }

    public Project(String GameCategory, String project_id, String uuid, String name, String description) {
        this.gd = GameCategory;
        this.project_id = project_id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
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
        return gd;
    }
}
