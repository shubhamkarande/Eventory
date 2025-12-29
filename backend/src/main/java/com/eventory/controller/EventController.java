package com.eventory.controller;

import com.eventory.dto.CreateEventRequest;
import com.eventory.dto.EventResponse;
import com.eventory.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventResponse>> getEvents(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false, defaultValue = "50") Double radius,
            @RequestParam(required = false) String category) {

        if (lat != null && lng != null) {
            return ResponseEntity.ok(eventService.getEventsNearby(lat, lng, radius, category));
        } else if (category != null && !category.isEmpty()) {
            return ResponseEntity.ok(eventService.getEventsByCategory(category));
        } else {
            return ResponseEntity.ok(eventService.getUpcomingEvents());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable UUID id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody CreateEventRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(eventService.createEvent(request, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable UUID id,
            @Valid @RequestBody CreateEventRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(eventService.updateEvent(id, request, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        eventService.deleteEvent(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/organizer")
    public ResponseEntity<List<EventResponse>> getOrganizerEvents(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(eventService.getOrganizerEvents(userDetails.getUsername()));
    }
}
