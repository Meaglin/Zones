package com.zones.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.avaje.ebean.validation.NotNull;


/*
@Entity
@Table( name = "zones_vertices", 
        uniqueConstraints=
            @UniqueConstraint(columnNames={"id", "vertexorder"})
) */
public class Vertice {
    
    @NotNull
    @ManyToOne
    @JoinColumn(name="id")
    private Zone zone;
    
    @Column(insertable = false, updatable = false)
    private int id;

    private int vertexorder;
    
    private int x;
    
    private int y;

    public Zone getZone() {
        return zone;
    }

    public int getId() {
        return id;
    }

    public int getVertexorder() {
        return vertexorder;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setVertexorder(int vertexorder) {
        this.vertexorder = vertexorder;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
