package com.eventory.service;

import com.eventory.dto.EventsResponse;
import com.eventory.dto.RSVPRequest;
import com.eventory.model.Event;
import com.eventory.model.EventCategory;
import com.eventory.model.RSVP;
import com.eventory.model.RSVPStatus;
import com.eventory.repository.EventCategoryRepository;
import com.eventory.repository.EventRepository;
import com.eventory.repository.RSVPRepository;
import com.eventory.util.QRCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EventService {
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private RSVPRepository rsvpRepository;
    
    @Autowired
    private EventCategoryRepository categoryRepository;
    
    @Autowired
    private QRCodeGenerator qrCodeGenerator;
    
    public EventsResponse getEvents(double latitude, double longitude, int radius,
                                  String category, String startDate, String endDate,
                                  Pageable pageable) {
        
        LocalDateTime start = null;
        LocalDateTime end = null;
        
        if (startDate != null) {
            start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        if (endDate != null) {
            end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        
        Page<Event> eventPage;
        
        if (category != null) {
            eventPage = eventRepository.findEventsByLocationAndCategory(
                latitude, longitude, radius, category, start, end, pageable);
        } else {
            eventPage = eventRepository.findEventsByLocation(
                latitude, longitude, radius, start, end, pageable);
        }
        
        return new EventsResponse(
            eventPage.getContent(),
            eventPage.getTotalElements(),
            eventPage.getTotalPages(),
            eventPage.getNumber(),
            eventPage.hasNext()
        );
    }
    
    public Event getEventById(String eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));
    }
    
    public RSVP rsvpToEvent(String eventId, RSVPRequest request) {
        // Check if event exists
        Event event = getEventById(eventId);
        
        // Check if user already has RSVP
        if (rsvpRepository.existsByEventIdAndUserId(eventId, request.getUserId())) {
            throw new RuntimeException("User already has RSVP for this event");
        }
        
        // Check if event is full
        if (event.getMaxAttendees() != null && 
            event.getCurrentAttendees() >= event.getMaxAttendees()) {
            throw new RuntimeException("Event is full");
        }
        
        // Generate unique QR code
        String qrCodeData = generateQRCodeData(eventId, request.getUserId());
        
        // Create RSVP
        RSVP rsvp = new RSVP(eventId, request.getUserId(), qrCodeData);
        if (request.getReminderSettings() != null) {
            rsvp.setReminderEnabled(request.getReminderSettings().getEnabled());
            rsvp.setReminderMinutesBefore(request.getReminderSettings().getMinutesBefore());
        }
        
        rsvp = rsvpRepository.save(rsvp);
        
        // Update event attendee count
        event.setCurrentAttendees(event.getCurrentAttendees() + 1);
        eventRepository.save(event);
        
        return rsvp;
    }
    
    public void cancelRSVP(String eventId, String userId) {
        RSVP rsvp = rsvpRepository.findByEventIdAndUserId(eventId, userId)
            .orElseThrow(() -> new RuntimeException("RSVP not found"));
        
        rsvp.setStatus(RSVPStatus.CANCELLED);
        rsvpRepository.save(rsvp);
        
        // Update event attendee count
        Event event = getEventById(eventId);
        event.setCurrentAttendees(Math.max(0, event.getCurrentAttendees() - 1));
        eventRepository.save(event);
    }
    
    public List<RSVP> getUserRSVPs(String userId) {
        return rsvpRepository.findByUserIdAndStatus(userId, RSVPStatus.CONFIRMED);
    }
    
    public List<EventCategory> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    private String generateQRCodeData(String eventId, String userId) {
        return String.format("%s:%s:%s", eventId, userId, UUID.randomUUID().toString());
    }
}