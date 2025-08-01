package com.eventory.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "rsvps", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"event_id", "user_id"})
})
public class RSVP {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @NotBlank
    @Column(name = "event_id", nullable = false)
    private String eventId;
    
    @NotBlank
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @NotBlank
    @Column(name = "qr_code", nullable = false, unique = true)
    private String qrCode;
    
    @NotNull
    @Column(name = "rsvp_date_time", nullable = false)
    private LocalDateTime rsvpDateTime;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RSVPStatus status = RSVPStatus.CONFIRMED;
    
    @Column(name = "reminder_enabled")
    private Boolean reminderEnabled = true;
    
    @Column(name = "reminder_minutes_before")
    private Integer reminderMinutesBefore = 60;
    
    @Column(name = "checked_in_at")
    private LocalDateTime checkedInAt;
    
    // Constructors
    public RSVP() {}
    
    public RSVP(String eventId, String userId, String qrCode) {
        this.eventId = eventId;
        this.userId = userId;
        this.qrCode = qrCode;
        this.rsvpDateTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    
    public LocalDateTime getRsvpDateTime() { return rsvpDateTime; }
    public void setRsvpDateTime(LocalDateTime rsvpDateTime) { this.rsvpDateTime = rsvpDateTime; }
    
    public RSVPStatus getStatus() { return status; }
    public void setStatus(RSVPStatus status) { this.status = status; }
    
    public Boolean getReminderEnabled() { return reminderEnabled; }
    public void setReminderEnabled(Boolean reminderEnabled) { this.reminderEnabled = reminderEnabled; }
    
    public Integer getReminderMinutesBefore() { return reminderMinutesBefore; }
    public void setReminderMinutesBefore(Integer reminderMinutesBefore) { this.reminderMinutesBefore = reminderMinutesBefore; }
    
    public LocalDateTime getCheckedInAt() { return checkedInAt; }
    public void setCheckedInAt(LocalDateTime checkedInAt) { this.checkedInAt = checkedInAt; }
}

enum RSVPStatus {
    CONFIRMED,
    CANCELLED,
    CHECKED_IN
}