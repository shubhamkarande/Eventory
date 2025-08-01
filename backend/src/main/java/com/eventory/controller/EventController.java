package com.eventory.controller;

import com.eventory.dto.EventsResponse;
import com.eventory.dto.RSVPRequest;
import com.eventory.model.Event;
import com.eventory.model.EventCategory;
import com.eventory.model.RSVP;
import com.eventory.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class EventController {
    
    @Autowired
    private EventService eventService;
    
    @GetMapping("/events")
    public ResponseEntity<EventsResponse> getEvents(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "50") int radius,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        EventsResponse response = eventService.getEvents(
            latitude, longitude, radius, category, startDate, endDate, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/events/{eventId}")
    public ResponseEntity<Event> getEventDetails(@PathVariable String eventId) {
        Event event = eventService.getEventById(eventId);
        return ResponseEntity.ok(event);
    }
    
    @PostMapping("/events/{eventId}/rsvp")
    public ResponseEntity<RSVP> rsvpToEvent(
            @PathVariable String eventId,
            @Valid @RequestBody RSVPRequest rsvpRequest) {
        RSVP rsvp = eventService.rsvpToEvent(eventId, rsvpRequest);
        return ResponseEntity.ok(rsvp);
    }
    
    @DeleteMapping("/events/{eventId}/rsvp")
    public ResponseEntity<Void> cancelRSVP(
            @PathVariable String eventId,
            @RequestParam String userId) {
        eventService.cancelRSVP(eventId, userId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/user/rsvps")
    public ResponseEntity<List<RSVP>> getUserRSVPs(@RequestParam String userId) {
        List<RSVP> rsvps = eventService.getUserRSVPs(userId);
        return ResponseEntity.ok(rsvps);
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<EventCategory>> getEventCategories() {
        List<EventCategory> categories = eventService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
}