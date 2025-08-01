package com.eventory.dto;

import com.eventory.model.Event;
import java.util.List;

public class EventsResponse {
    private List<Event> events;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private boolean hasNext;
    
    public EventsResponse() {}
    
    public EventsResponse(List<Event> events, long totalElements, int totalPages, 
                         int currentPage, boolean hasNext) {
        this.events = events;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.hasNext = hasNext;
    }
    
    // Getters and Setters
    public List<Event> getEvents() { return events; }
    public void setEvents(List<Event> events) { this.events = events; }
    
    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    
    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }
    
    public boolean isHasNext() { return hasNext; }
    public void setHasNext(boolean hasNext) { this.hasNext = hasNext; }
}