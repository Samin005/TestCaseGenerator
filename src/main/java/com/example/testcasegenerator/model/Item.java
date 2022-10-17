package com.example.testcasegenerator.model;

import javax.persistence.*;

@Entity
public class Item {
    @Id
    private Integer id;

    @ManyToOne(optional = false)
    private Title title;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }
}
