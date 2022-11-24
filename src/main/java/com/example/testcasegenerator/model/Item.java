package com.example.testcasegenerator.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Item {
    @Id
    private Integer id;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Title title;

    @Column(nullable = false)
    private String location;

    @Temporal(TemporalType.DATE)
    @Column(updatable = false)
    private final Date created = new Date();

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getCreated() {
        return created;
    }
}
