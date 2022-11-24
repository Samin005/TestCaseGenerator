package com.example.testcasegenerator.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
public class Title {
    @Id
    private Integer id;

    @Column(unique = true, nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Author author;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
