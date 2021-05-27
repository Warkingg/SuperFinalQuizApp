package com.example.superquizapp.repository;

import com.example.superquizapp.domain.Response;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseRepository extends CrudRepository<Response,Long> {
}
