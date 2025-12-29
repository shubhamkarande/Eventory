package com.eventory.dto;

import com.eventory.model.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {
    private String id;
    private String organizerId;
    private String organizerName;
    private String title;
    private String description;
    private String imageUrl;
    private Double latitude;
    private Double longitude;
    private String address;
    private String venueName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String category;
    private Boolean isFree;
    private BigDecimal price;
    private Integer maxAttendees;
    private Long attendeeCount;
    private LocalDateTime createdAt;

    public static EventResponse fromEvent(Event event, Long attendeeCount) {
        return EventResponse.builder()
                .id(event.getId().toString())
                .organizerId(event.getOrganizer().getId().toString())
                .organizerName(event.getOrganizer().getName())
                .title(event.getTitle())
                .description(event.getDescription())
                .imageUrl(event.getImageUrl())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .address(event.getAddress())
                .venueName(event.getVenueName())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .category(event.getCategory())
                .isFree(event.getIsFree())
                .price(event.getPrice())
                .maxAttendees(event.getMaxAttendees())
                .attendeeCount(attendeeCount)
                .createdAt(event.getCreatedAt())
                .build();
    }
}
