package com.eventory.repository;

import com.eventory.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {
    
    @Query("SELECT e FROM Event e JOIN e.venue v " +
           "WHERE (:startDate IS NULL OR e.startDateTime >= :startDate) " +
           "AND (:endDate IS NULL OR e.endDateTime <= :endDate) " +
           "AND (6371 * acos(cos(radians(:latitude)) * cos(radians(v.latitude)) * " +
           "cos(radians(v.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(v.latitude)))) <= :radius " +
           "ORDER BY e.startDateTime ASC")
    Page<Event> findEventsByLocation(
        @Param("latitude") double latitude,
        @Param("longitude") double longitude,
        @Param("radius") int radius,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
    
    @Query("SELECT e FROM Event e JOIN e.venue v JOIN e.category c " +
           "WHERE c.id = :categoryId " +
           "AND (:startDate IS NULL OR e.startDateTime >= :startDate) " +
           "AND (:endDate IS NULL OR e.endDateTime <= :endDate) " +
           "AND (6371 * acos(cos(radians(:latitude)) * cos(radians(v.latitude)) * " +
           "cos(radians(v.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(v.latitude)))) <= :radius " +
           "ORDER BY e.startDateTime ASC")
    Page<Event> findEventsByLocationAndCategory(
        @Param("latitude") double latitude,
        @Param("longitude") double longitude,
        @Param("radius") int radius,
        @Param("categoryId") String categoryId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
}