package com.project.resume.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Objects;

@Entity
@Data
public class Admin {
    public Admin() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String login;
    private String password;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Admin admin = (Admin) object;
        return Objects.equals(login, admin.login) && Objects.equals(password, admin.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, password);
    }
}
