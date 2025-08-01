package com.eventory.repository;

import com.eventory.model.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventCategoryRepository extends JpaRepository<EventCategory, String> {
    
    Optional<EventCategory> findByName(String name);
}