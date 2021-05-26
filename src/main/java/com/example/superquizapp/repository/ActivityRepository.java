package com.example.superquizapp.repository;

import com.example.superquizapp.domain.Activity;
import org.springframework.data.repository.CrudRepository;

public interface ActivityRepository extends CrudRepository<Activity,Long> {
}
