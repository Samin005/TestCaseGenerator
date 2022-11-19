package com.example.testcasegenerator.model;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.util.Calendar;
import java.util.Date;

@Entity
@Check(constraints = "renewal <= 2 AND due = created + interval 20 day")
public class Loan {
    @Id
    private Integer id;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Item item;

    private int renewal;

    @Temporal(TemporalType.DATE)
    @Column(updatable = false)
    private final Date created =  new Date();

    @Temporal(TemporalType.DATE)
    private Date due;

    public Loan() {
        int daysToDue = 20;
        this.due = initializeDueDate(daysToDue);
    }

    public Loan(int daysToDue) {
        this.due = initializeDueDate(daysToDue);
    }

    private Date initializeDueDate(int daysToDue) {
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, daysToDue);
        return calendar.getTime();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getRenewal() {
        return renewal;
    }

    public void setRenewal(int renewal) {
        this.renewal = renewal;
    }

    public Date getCreated() {
        return created;
    }

    public Date getDue() {
        return due;
    }

    public void setDue(Date due) {
        this.due = due;
    }
}
