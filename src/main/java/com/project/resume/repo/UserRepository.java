package com.project.resume.repo;

import com.project.resume.model.Admin;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<Admin, Long> {
}