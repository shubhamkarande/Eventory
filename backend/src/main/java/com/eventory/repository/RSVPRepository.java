package com.eventory.repository;

import com.eventory.model.Rsvp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RsvpRepository extends JpaRepository<Rsvp, UUID> {

    Optional<Rsvp> findByQrCode(String qrCode);

    Optional<Rsvp> findByEventIdAndUserId(UUID eventId, UUID userId);

    List<Rsvp> findByUserId(UUID userId);

    List<Rsvp> findByEventId(UUID eventId);

    boolean existsByEventIdAndUserId(UUID eventId, UUID userId);

    @Query("SELECT COUNT(r) FROM Rsvp r WHERE r.event.id = :eventId")
    long countByEventId(@Param("eventId") UUID eventId);

    @Query("SELECT COUNT(r) FROM Rsvp r WHERE r.event.id = :eventId AND r.checkedIn = true")
    long countCheckedInByEventId(@Param("eventId") UUID eventId);
}
