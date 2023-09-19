package com.project.resume.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "projects")
public class Project implements Comparable<Project> {
    public Project() {
        setMain(true);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private boolean main;

    private String title;

    private String description;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Image image;

    private String page;

    @Override
    public int compareTo(Project o) {
        return this.id - o.id;
    }
}
