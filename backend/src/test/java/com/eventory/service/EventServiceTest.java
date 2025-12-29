package com.eventory.service;

import com.eventory.dto.CreateEventRequest;
import com.eventory.dto.EventResponse;
import com.eventory.model.Event;
import com.eventory.model.User;
import com.eventory.repository.EventRepository;
import com.eventory.repository.RsvpRepository;
import com.eventory.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RsvpRepository rsvpRepository;

    @InjectMocks
    private EventService eventService;

    private User testOrganizer;
    private Event testEvent;
    private CreateEventRequest createEventRequest;

    @BeforeEach
    void setUp() {
        testOrganizer = User.builder()
                .id(UUID.randomUUID())
                .name("Test Organizer")
                .email("organizer@example.com")
                .role(User.Role.ORGANIZER)
                .build();

        testEvent = Event.builder()
                .id(UUID.randomUUID())
                .organizer(testOrganizer)
                .title("Test Event")
                .description("Test Description")
                .latitude(40.7128)
                .longitude(-74.0060)
                .address("New York, NY")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(3))
                .category("music")
                .isFree(true)
                .build();

        createEventRequest = new CreateEventRequest(
                "New Event",
                "Event Description",
                null,
                40.7128,
                -74.0060,
                "New York, NY",
                "Test Venue",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(3),
                "tech",
                true,
                BigDecimal.ZERO,
                100);
    }

    @Test
    void getUpcomingEvents_ShouldReturnEventList() {
        // Arrange
        when(eventRepository.findUpcomingEvents(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(testEvent));
        when(rsvpRepository.countByEventId(any(UUID.class))).thenReturn(10L);

        // Act
        List<EventResponse> result = eventService.getUpcomingEvents();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Event", result.get(0).getTitle());
        assertEquals(10L, result.get(0).getAttendeeCount());
    }

    @Test
    void getEventById_WithValidId_ShouldReturnEvent() {
        // Arrange
        UUID eventId = testEvent.getId();
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));
        when(rsvpRepository.countByEventId(eventId)).thenReturn(5L);

        // Act
        EventResponse result = eventService.getEventById(eventId);

        // Assert
        assertNotNull(result);
        assertEquals("Test Event", result.getTitle());
        assertEquals(5L, result.getAttendeeCount());
    }

    @Test
    void getEventById_WithInvalidId_ShouldThrowException() {
        // Arrange
        UUID invalidId = UUID.randomUUID();
        when(eventRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> eventService.getEventById(invalidId));
    }

    @Test
    void createEvent_WithValidData_ShouldReturnEventResponse() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testOrganizer));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // Act
        EventResponse result = eventService.createEvent(createEventRequest, "organizer@example.com");

        // Assert
        assertNotNull(result);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void deleteEvent_ByOwner_ShouldSucceed() {
        // Arrange
        UUID eventId = testEvent.getId();
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));
        doNothing().when(eventRepository).delete(any(Event.class));

        // Act
        eventService.deleteEvent(eventId, "organizer@example.com");

        // Assert
        verify(eventRepository).delete(testEvent);
    }

    @Test
    void deleteEvent_ByNonOwner_ShouldThrowException() {
        // Arrange
        UUID eventId = testEvent.getId();
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> eventService.deleteEvent(eventId, "other@example.com"));
        verify(eventRepository, never()).delete(any(Event.class));
    }

    @Test
    void getEventsNearby_WithLocation_ShouldReturnEvents() {
        // Arrange
        when(eventRepository.findEventsWithinRadius(
                eq(40.7128), eq(-74.0060), eq(50.0), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(testEvent));
        when(rsvpRepository.countByEventId(any(UUID.class))).thenReturn(0L);

        // Act
        List<EventResponse> result = eventService.getEventsNearby(40.7128, -74.0060, 50.0, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
