package com.eventory.repository;

import com.eventory.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    List<Event> findByCategory(String category);

    List<Event> findByOrganizerId(UUID organizerId);

    @Query("SELECT e FROM Event e WHERE e.startTime >= :now ORDER BY e.startTime ASC")
    List<Event> findUpcomingEvents(@Param("now") LocalDateTime now);

    @Query("SELECT e FROM Event e WHERE e.category = :category AND e.startTime >= :now ORDER BY e.startTime ASC")
    List<Event> findUpcomingEventsByCategory(@Param("category") String category, @Param("now") LocalDateTime now);

    @Query(value = """
            SELECT * FROM events e
            WHERE e.start_time >= :now
            AND (6371 * acos(cos(radians(:lat)) * cos(radians(e.latitude))
            * cos(radians(e.longitude) - radians(:lng)) + sin(radians(:lat))
            * sin(radians(e.latitude)))) <= :radius
            ORDER BY e.start_time ASC
            """, nativeQuery = true)
    List<Event> findEventsWithinRadius(
            @Param("lat") Double latitude,
            @Param("lng") Double longitude,
            @Param("radius") Double radiusKm,
            @Param("now") LocalDateTime now);

    @Query(value = """
            SELECT * FROM events e
            WHERE e.start_time >= :now
            AND e.category = :category
            AND (6371 * acos(cos(radians(:lat)) * cos(radians(e.latitude))
            * cos(radians(e.longitude) - radians(:lng)) + sin(radians(:lat))
            * sin(radians(e.latitude)))) <= :radius
            ORDER BY e.start_time ASC
            """, nativeQuery = true)
    List<Event> findEventsWithinRadiusByCategory(
            @Param("lat") Double latitude,
            @Param("lng") Double longitude,
            @Param("radius") Double radiusKm,
            @Param("category") String category,
            @Param("now") LocalDateTime now);
}
