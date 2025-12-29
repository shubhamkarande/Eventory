package com.eventory.service;

import com.eventory.dto.CreateEventRequest;
import com.eventory.dto.EventResponse;
import com.eventory.model.Event;
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
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RsvpRepository rsvpRepository;

    public List<EventResponse> getUpcomingEvents() {
        return eventRepository.findUpcomingEvents(LocalDateTime.now()).stream()
                .map(event -> EventResponse.fromEvent(event, rsvpRepository.countByEventId(event.getId())))
                .collect(Collectors.toList());
    }

    public List<EventResponse> getEventsByCategory(String category) {
        return eventRepository.findUpcomingEventsByCategory(category, LocalDateTime.now()).stream()
                .map(event -> EventResponse.fromEvent(event, rsvpRepository.countByEventId(event.getId())))
                .collect(Collectors.toList());
    }

    public List<EventResponse> getEventsNearby(Double lat, Double lng, Double radiusKm, String category) {
        List<Event> events;
        if (category != null && !category.isEmpty()) {
            events = eventRepository.findEventsWithinRadiusByCategory(lat, lng, radiusKm, category,
                    LocalDateTime.now());
        } else {
            events = eventRepository.findEventsWithinRadius(lat, lng, radiusKm, LocalDateTime.now());
        }
        return events.stream()
                .map(event -> EventResponse.fromEvent(event, rsvpRepository.countByEventId(event.getId())))
                .collect(Collectors.toList());
    }

    public EventResponse getEventById(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return EventResponse.fromEvent(event, rsvpRepository.countByEventId(event.getId()));
    }

    @Transactional
    public EventResponse createEvent(CreateEventRequest request, String organizerEmail) {
        User organizer = userRepository.findByEmail(organizerEmail)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        Event event = Event.builder()
                .organizer(organizer)
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(request.getAddress())
                .venueName(request.getVenueName())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .category(request.getCategory())
                .isFree(request.getIsFree() != null ? request.getIsFree() : true)
                .price(request.getPrice())
                .maxAttendees(request.getMaxAttendees())
                .build();

        event = eventRepository.save(event);
        return EventResponse.fromEvent(event, 0L);
    }

    @Transactional
    public EventResponse updateEvent(UUID eventId, CreateEventRequest request, String organizerEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getOrganizer().getEmail().equals(organizerEmail)) {
            throw new RuntimeException("You can only update your own events");
        }

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setImageUrl(request.getImageUrl());
        event.setLatitude(request.getLatitude());
        event.setLongitude(request.getLongitude());
        event.setAddress(request.getAddress());
        event.setVenueName(request.getVenueName());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setCategory(request.getCategory());
        event.setIsFree(request.getIsFree());
        event.setPrice(request.getPrice());
        event.setMaxAttendees(request.getMaxAttendees());

        event = eventRepository.save(event);
        return EventResponse.fromEvent(event, rsvpRepository.countByEventId(event.getId()));
    }

    @Transactional
    public void deleteEvent(UUID eventId, String organizerEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getOrganizer().getEmail().equals(organizerEmail)) {
            throw new RuntimeException("You can only delete your own events");
        }

        eventRepository.delete(event);
    }

    public List<EventResponse> getOrganizerEvents(String organizerEmail) {
        User organizer = userRepository.findByEmail(organizerEmail)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        return eventRepository.findByOrganizerId(organizer.getId()).stream()
                .map(event -> EventResponse.fromEvent(event, rsvpRepository.countByEventId(event.getId())))
                .collect(Collectors.toList());
    }
}
