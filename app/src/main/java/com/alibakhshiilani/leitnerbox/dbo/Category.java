package com.alibakhshiilani.leitnerbox.dbo;

import java.io.Serializable;

/**
 * Created by Ali on 5/24/2017.
 */
public class Category  implements Serializable {
    private long id;
    private String name;
    private String image;
    private long parent_id;
    private int type;
    private String created_at;
    private int count;


    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getParent_id() {
        return parent_id;
    }

    public void setParent_id(long parent_id) {
        this.parent_id = parent_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int isType() {
        return type;
    }

    public void setType( int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
