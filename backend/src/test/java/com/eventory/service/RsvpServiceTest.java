package com.eventory.service;

import com.eventory.dto.RsvpResponse;
import com.eventory.model.Event;
import com.eventory.model.Rsvp;
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
class RsvpServiceTest {

    @Mock
    private RsvpRepository rsvpRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RsvpService rsvpService;

    private User testUser;
    private User testOrganizer;
    private Event testEvent;
    private Rsvp testRsvp;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("user@example.com")
                .role(User.Role.ATTENDEE)
                .build();

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
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(3))
                .category("music")
                .isFree(true)
                .maxAttendees(100)
                .build();

        testRsvp = Rsvp.builder()
                .id(UUID.randomUUID())
                .event(testEvent)
                .user(testUser)
                .qrCode("EVENTORY-TEST-QR")
                .checkedIn(false)
                .build();
    }

    @Test
    void createRsvp_WithValidData_ShouldReturnRsvpResponse() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(any(UUID.class))).thenReturn(Optional.of(testEvent));
        when(rsvpRepository.existsByEventIdAndUserId(any(UUID.class), any(UUID.class))).thenReturn(false);
        when(rsvpRepository.countByEventId(any(UUID.class))).thenReturn(10L);
        when(rsvpRepository.save(any(Rsvp.class))).thenReturn(testRsvp);

        // Act
        RsvpResponse result = rsvpService.createRsvp(testEvent.getId(), "user@example.com");

        // Assert
        assertNotNull(result);
        assertNotNull(result.getQrCode());
        verify(rsvpRepository).save(any(Rsvp.class));
    }

    @Test
    void createRsvp_WhenAlreadyRsvped_ShouldThrowException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(any(UUID.class))).thenReturn(Optional.of(testEvent));
        when(rsvpRepository.existsByEventIdAndUserId(any(UUID.class), any(UUID.class))).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> rsvpService.createRsvp(testEvent.getId(), "user@example.com"));
        verify(rsvpRepository, never()).save(any(Rsvp.class));
    }

    @Test
    void createRsvp_WhenEventFull_ShouldThrowException() {
        // Arrange
        testEvent.setMaxAttendees(10);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(any(UUID.class))).thenReturn(Optional.of(testEvent));
        when(rsvpRepository.existsByEventIdAndUserId(any(UUID.class), any(UUID.class))).thenReturn(false);
        when(rsvpRepository.countByEventId(any(UUID.class))).thenReturn(10L);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> rsvpService.createRsvp(testEvent.getId(), "user@example.com"));
    }

    @Test
    void checkIn_WithValidQr_ShouldSucceed() {
        // Arrange
        when(rsvpRepository.findByQrCode(anyString())).thenReturn(Optional.of(testRsvp));
        when(rsvpRepository.save(any(Rsvp.class))).thenReturn(testRsvp);

        // Act
        RsvpResponse result = rsvpService.checkIn("EVENTORY-TEST-QR", "organizer@example.com");

        // Assert
        assertNotNull(result);
        verify(rsvpRepository).save(any(Rsvp.class));
    }

    @Test
    void checkIn_ByNonOrganizer_ShouldThrowException() {
        // Arrange
        when(rsvpRepository.findByQrCode(anyString())).thenReturn(Optional.of(testRsvp));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> rsvpService.checkIn("EVENTORY-TEST-QR", "other@example.com"));
    }

    @Test
    void checkIn_WhenAlreadyCheckedIn_ShouldThrowException() {
        // Arrange
        testRsvp.setCheckedIn(true);
        when(rsvpRepository.findByQrCode(anyString())).thenReturn(Optional.of(testRsvp));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> rsvpService.checkIn("EVENTORY-TEST-QR", "organizer@example.com"));
    }

    @Test
    void getUserRsvps_ShouldReturnRsvpList() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(rsvpRepository.findByUserId(any(UUID.class))).thenReturn(Arrays.asList(testRsvp));

        // Act
        List<RsvpResponse> result = rsvpService.getUserRsvps("user@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void cancelRsvp_BeforeCheckIn_ShouldSucceed() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(rsvpRepository.findByEventIdAndUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(testRsvp));
        doNothing().when(rsvpRepository).delete(any(Rsvp.class));

        // Act
        rsvpService.cancelRsvp(testEvent.getId(), "user@example.com");

        // Assert
        verify(rsvpRepository).delete(testRsvp);
    }

    @Test
    void cancelRsvp_AfterCheckIn_ShouldThrowException() {
        // Arrange
        testRsvp.setCheckedIn(true);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(rsvpRepository.findByEventIdAndUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(testRsvp));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> rsvpService.cancelRsvp(testEvent.getId(), "user@example.com"));
        verify(rsvpRepository, never()).delete(any(Rsvp.class));
    }
}
