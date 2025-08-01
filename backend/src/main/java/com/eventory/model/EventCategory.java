package com.eventory.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "event_categories")
public class EventCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false)
    private String color = "#5C6BC0";
    
    private String icon;
    
    // Constructors
    public EventCategory() {}
    
    public EventCategory(String name, String color, String icon) {
        this.name = name;
        this.color = color;
        this.icon = icon;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}