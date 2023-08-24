package com.project.resume.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Project implements Comparable<Project> {
    public Project() {
        //default
        setMain(true);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private boolean main;

    private String title;

    private String description;

    private String img;

    private String file_html;


    @Override
    public int compareTo(Project o) {
        return this.id - o.id;
    }
}
