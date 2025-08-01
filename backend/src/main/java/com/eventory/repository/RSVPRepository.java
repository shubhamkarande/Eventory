package com.eventory.repository;

import com.eventory.model.RSVP;
import com.eventory.model.RSVPStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RSVPRepository extends JpaRepository<RSVP, String> {
    
    boolean existsByEventIdAndUserId(String eventId, String userId);
    
    Optional<RSVP> findByEventIdAndUserId(String eventId, String userId);
    
    List<RSVP> findByUserIdAndStatus(String userId, RSVPStatus status);
    
    Optional<RSVP> findByQrCode(String qrCode);
    
    List<RSVP> findByEventId(String eventId);
}