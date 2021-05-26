package com.example.superquizapp.repository;

import com.example.superquizapp.domain.Audit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AuditRepository extends CrudRepository<Audit,Long> {
    @Query("FROM Audit where user_id=?1")
    List<Audit> findByUserId(Long userId);
}
