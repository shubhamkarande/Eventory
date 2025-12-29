package com.eventory.dto;

import com.eventory.model.Rsvp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsvpResponse {
    private String id;
    private String eventId;
    private String eventTitle;
    private String userId;
    private String userName;
    private String qrCode;
    private Boolean checkedIn;
    private LocalDateTime checkedInAt;
    private LocalDateTime createdAt;

    public static RsvpResponse fromRsvp(Rsvp rsvp) {
        return RsvpResponse.builder()
                .id(rsvp.getId().toString())
                .eventId(rsvp.getEvent().getId().toString())
                .eventTitle(rsvp.getEvent().getTitle())
                .userId(rsvp.getUser().getId().toString())
                .userName(rsvp.getUser().getName())
                .qrCode(rsvp.getQrCode())
                .checkedIn(rsvp.getCheckedIn())
                .checkedInAt(rsvp.getCheckedInAt())
                .createdAt(rsvp.getCreatedAt())
                .build();
    }
}
