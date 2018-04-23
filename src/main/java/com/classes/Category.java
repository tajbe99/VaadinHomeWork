package com.classes;

import java.io.Serializable;

public class Category implements Serializable, Cloneable{

    private String name;

    private Long id;

    public boolean isPersisted() {
        return id != null;
    }

    @Override
    protected Category clone() throws CloneNotSupportedException {
        return (Category) super.clone();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    public Category() { }

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) { this.id = id; }

    public Long getId() { return id; }


}
