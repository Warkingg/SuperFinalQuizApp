package com.example.superquizapp.repository;

import com.example.superquizapp.domain.AnswerType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerTypeRepository extends CrudRepository<AnswerType,Long> {
}
