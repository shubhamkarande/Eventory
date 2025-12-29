package com.eventory.service;

import com.eventory.dto.RsvpResponse;
import com.eventory.model.Event;
import com.eventory.model.Rsvp;
import com.eventory.model.User;
import com.eventory.repository.EventRepository;
import com.eventory.repository.RsvpRepository;
import com.eventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RsvpService {

    private final RsvpRepository rsvpRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public RsvpResponse createRsvp(UUID eventId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Check if already RSVPed
        if (rsvpRepository.existsByEventIdAndUserId(eventId, user.getId())) {
            throw new RuntimeException("You have already RSVPed to this event");
        }

        // Check capacity
        if (event.getMaxAttendees() != null) {
            long currentCount = rsvpRepository.countByEventId(eventId);
            if (currentCount >= event.getMaxAttendees()) {
                throw new RuntimeException("Event is at full capacity");
            }
        }

        // Generate unique QR code
        String qrCode = generateQrCode(eventId, user.getId());

        Rsvp rsvp = Rsvp.builder()
                .event(event)
                .user(user)
                .qrCode(qrCode)
                .checkedIn(false)
                .build();

        rsvp = rsvpRepository.save(rsvp);
        return RsvpResponse.fromRsvp(rsvp);
    }

    public RsvpResponse getRsvpByQrCode(String qrCode) {
        Rsvp rsvp = rsvpRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new RuntimeException("RSVP not found"));
        return RsvpResponse.fromRsvp(rsvp);
    }

    @Transactional
    public RsvpResponse checkIn(String qrCode, String organizerEmail) {
        Rsvp rsvp = rsvpRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new RuntimeException("Invalid QR code"));

        // Verify organizer owns this event
        if (!rsvp.getEvent().getOrganizer().getEmail().equals(organizerEmail)) {
            throw new RuntimeException("You can only check in attendees for your own events");
        }

        if (rsvp.getCheckedIn()) {
            throw new RuntimeException("Attendee already checked in");
        }

        rsvp.setCheckedIn(true);
        rsvp.setCheckedInAt(LocalDateTime.now());
        rsvp = rsvpRepository.save(rsvp);

        return RsvpResponse.fromRsvp(rsvp);
    }

    public List<RsvpResponse> getUserRsvps(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return rsvpRepository.findByUserId(user.getId()).stream()
                .map(RsvpResponse::fromRsvp)
                .collect(Collectors.toList());
    }

    public List<RsvpResponse> getEventRsvps(UUID eventId, String organizerEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getOrganizer().getEmail().equals(organizerEmail)) {
            throw new RuntimeException("You can only view attendees for your own events");
        }

        return rsvpRepository.findByEventId(eventId).stream()
                .map(RsvpResponse::fromRsvp)
                .collect(Collectors.toList());
    }

    public RsvpResponse getUserRsvpForEvent(UUID eventId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Rsvp rsvp = rsvpRepository.findByEventIdAndUserId(eventId, user.getId())
                .orElseThrow(() -> new RuntimeException("RSVP not found"));

        return RsvpResponse.fromRsvp(rsvp);
    }

    @Transactional
    public void cancelRsvp(UUID eventId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Rsvp rsvp = rsvpRepository.findByEventIdAndUserId(eventId, user.getId())
                .orElseThrow(() -> new RuntimeException("RSVP not found"));

        if (rsvp.getCheckedIn()) {
            throw new RuntimeException("Cannot cancel RSVP after check-in");
        }

        rsvpRepository.delete(rsvp);
    }

    private String generateQrCode(UUID eventId, UUID userId) {
        return String.format("EVENTORY-%s-%s-%s", eventId.toString().substring(0, 8),
                userId.toString().substring(0, 8), UUID.randomUUID().toString().substring(0, 8));
    }

    public long getEventAttendeeCount(UUID eventId) {
        return rsvpRepository.countByEventId(eventId);
    }

    public long getEventCheckedInCount(UUID eventId) {
        return rsvpRepository.countCheckedInByEventId(eventId);
    }
}
