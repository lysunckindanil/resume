package com.project.resume.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "projects")
public class Project implements Comparable<Project> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    public Project() {
        this.order = 0;
    }

    @Column(name = "order_list")
    private Integer order;

    private Boolean main;

    private String title;

    private String description;

    private String image;

    private String page;

    @Override
    public int compareTo(Project o) {
        return this.order - o.order;
    }

}
