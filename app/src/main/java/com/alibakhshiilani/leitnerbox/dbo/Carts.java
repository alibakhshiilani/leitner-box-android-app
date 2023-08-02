package com.alibakhshiilani.leitnerbox.dbo;

/**
 * Created by Ali on 5/23/2017.
 */
public class Carts {
    private long id;
    private String name;
    private String value;
    private String description = null;
    private long cat_id;
    private long level;
    private String created_at;
    private String readed_at;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReaded_at() {
        return readed_at;
    }

    public void setReaded_at(String readed_at) {
        this.readed_at = readed_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public long getLevel() {
        return level;
    }

    public void setLevel(long level) {
        this.level = level;
    }

    public long getCat_id() {
        return cat_id;
    }

    public void setCat_id(long cat_id) {
        this.cat_id = cat_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
