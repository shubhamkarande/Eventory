package com.eventory.dto;

import jakarta.validation.constraints.NotBlank;

public class RSVPRequest {
    
    @NotBlank
    private String userId;
    
    private ReminderSettings reminderSettings;
    
    public RSVPRequest() {}
    
    public RSVPRequest(String userId, ReminderSettings reminderSettings) {
        this.userId = userId;
        this.reminderSettings = reminderSettings;
    }
    
    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public ReminderSettings getReminderSettings() { return reminderSettings; }
    public void setReminderSettings(ReminderSettings reminderSettings) { this.reminderSettings = reminderSettings; }
    
    public static class ReminderSettings {
        private Boolean enabled = true;
        private Integer minutesBefore = 60;
        
        public ReminderSettings() {}
        
        public ReminderSettings(Boolean enabled, Integer minutesBefore) {
            this.enabled = enabled;
            this.minutesBefore = minutesBefore;
        }
        
        public Boolean getEnabled() { return enabled; }
        public void setEnabled(Boolean enabled) { this.enabled = enabled; }
        
        public Integer getMinutesBefore() { return minutesBefore; }
        public void setMinutesBefore(Integer minutesBefore) { this.minutesBefore = minutesBefore; }
    }
}