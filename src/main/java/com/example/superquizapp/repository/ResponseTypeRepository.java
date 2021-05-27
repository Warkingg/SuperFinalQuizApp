package com.example.superquizapp.repository;

import com.example.superquizapp.domain.ResponseType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseTypeRepository extends CrudRepository<ResponseType,Long> {
}
