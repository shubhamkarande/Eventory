package com.eventory.controller;

import com.eventory.dto.RsvpResponse;
import com.eventory.service.RsvpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RsvpController {

    private final RsvpService rsvpService;

    @PostMapping("/events/{eventId}/rsvp")
    public ResponseEntity<RsvpResponse> rsvpToEvent(
            @PathVariable UUID eventId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(rsvpService.createRsvp(eventId, userDetails.getUsername()));
    }

    @GetMapping("/events/{eventId}/rsvp")
    public ResponseEntity<RsvpResponse> getUserRsvpForEvent(
            @PathVariable UUID eventId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(rsvpService.getUserRsvpForEvent(eventId, userDetails.getUsername()));
    }

    @DeleteMapping("/events/{eventId}/rsvp")
    public ResponseEntity<Void> cancelRsvp(
            @PathVariable UUID eventId,
            @AuthenticationPrincipal UserDetails userDetails) {
        rsvpService.cancelRsvp(eventId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rsvps")
    public ResponseEntity<List<RsvpResponse>> getUserRsvps(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(rsvpService.getUserRsvps(userDetails.getUsername()));
    }

    @GetMapping("/events/{eventId}/attendees")
    public ResponseEntity<List<RsvpResponse>> getEventAttendees(
            @PathVariable UUID eventId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(rsvpService.getEventRsvps(eventId, userDetails.getUsername()));
    }

    @PostMapping("/rsvps/checkin")
    public ResponseEntity<RsvpResponse> checkIn(
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String qrCode = request.get("qrCode");
        return ResponseEntity.ok(rsvpService.checkIn(qrCode, userDetails.getUsername()));
    }

    @GetMapping("/events/{eventId}/stats")
    public ResponseEntity<Map<String, Long>> getEventStats(
            @PathVariable UUID eventId) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalRsvps", rsvpService.getEventAttendeeCount(eventId));
        stats.put("checkedIn", rsvpService.getEventCheckedInCount(eventId));
        return ResponseEntity.ok(stats);
    }
}
