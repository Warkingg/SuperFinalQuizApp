package com.example.superquizapp.repository;

import com.example.superquizapp.model.ImageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageModelRepository extends JpaRepository<ImageModel,Long> {
    Optional<ImageModel> findByName(String name);
}
