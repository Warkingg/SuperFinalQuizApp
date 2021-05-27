package com.example.superquizapp.repository;

import com.example.superquizapp.domain.FeedBack;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedBackRepository extends CrudRepository<FeedBack, Long> {
}
