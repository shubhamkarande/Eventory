package com.eventory.config;

import com.eventory.model.Event;
import com.eventory.model.EventCategory;
import com.eventory.model.Venue;
import com.eventory.repository.EventCategoryRepository;
import com.eventory.repository.EventRepository;
import com.eventory.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private EventCategoryRepository categoryRepository;
    
    @Autowired
    private VenueRepository venueRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            initializeCategories();
        }
        
        if (venueRepository.count() == 0) {
            initializeVenues();
        }
        
        if (eventRepository.count() == 0) {
            initializeEvents();
        }
    }
    
    private void initializeCategories() {
        List<EventCategory> categories = Arrays.asList(
            new EventCategory("Technology", "#2196F3", "tech"),
            new EventCategory("Music", "#E91E63", "music"),
            new EventCategory("Food & Drink", "#FF9800", "food"),
            new EventCategory("Sports", "#4CAF50", "sports"),
            new EventCategory("Art & Culture", "#9C27B0", "art"),
            new EventCategory("Business", "#607D8B", "business"),
            new EventCategory("Health & Wellness", "#00BCD4", "health"),
            new EventCategory("Education", "#795548", "education")
        );
        
        categoryRepository.saveAll(categories);
    }
    
    private void initializeVenues() {
        List<Venue> venues = Arrays.asList(
            new Venue("Moscone Center", "747 Howard St", 37.7840, -122.4014, "San Francisco", "CA", "94103"),
            new Venue("Golden Gate Park", "Golden Gate Park", 37.7694, -122.4862, "San Francisco", "CA", "94117"),
            new Venue("AT&T Park", "24 Willie Mays Plaza", 37.7786, -122.3893, "San Francisco", "CA", "94107"),
            new Venue("The Fillmore", "1805 Geary Blvd", 37.7849, -122.4329, "San Francisco", "CA", "94115"),
            new Venue("Pier 39", "Pier 39", 37.8087, -122.4098, "San Francisco", "CA", "94133")
        );
        
        venueRepository.saveAll(venues);
    }
    
    private void initializeEvents() {
        List<EventCategory> categories = categoryRepository.findAll();
        List<Venue> venues = venueRepository.findAll();
        
        if (categories.isEmpty() || venues.isEmpty()) {
            return;
        }
        
        List<Event> events = Arrays.asList(
            createEvent("Tech Conference 2024", "Join us for the biggest tech conference of the year!", 
                       LocalDateTime.now().plusDays(7), LocalDateTime.now().plusDays(7).plusHours(8),
                       venues.get(0), categories.get(0), "tech-org", "Tech Organizers", 500, 0.0,
                       "https://example.com/tech-conference.jpg"),
            
            createEvent("Summer Music Festival", "Three days of amazing music in the park!", 
                       LocalDateTime.now().plusDays(14), LocalDateTime.now().plusDays(16),
                       venues.get(1), categories.get(1), "music-org", "Music Events Inc", 50.0, 0.0,
                       "https://example.com/music-festival.jpg"),
            
            createEvent("Food Truck Rally", "Taste the best food trucks in the city!", 
                       LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(3).plusHours(6),
                       venues.get(4), categories.get(2), "food-org", "Foodie Events", null, 0.0,
                       "https://example.com/food-truck.jpg"),
            
            createEvent("Giants vs Dodgers", "Baseball game at AT&T Park!", 
                       LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(10).plusHours(3),
                       venues.get(2), categories.get(3), "giants", "SF Giants", 75.0, 0.0,
                       "https://example.com/baseball.jpg"),
            
            createEvent("Art Gallery Opening", "Contemporary art exhibition opening night!", 
                       LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(5).plusHours(4),
                       venues.get(3), categories.get(4), "gallery", "Modern Art Gallery", 25.0, 0.0,
                       "https://example.com/art-gallery.jpg")
        );
        
        eventRepository.saveAll(events);
    }
    
    private Event createEvent(String title, String description, LocalDateTime start, LocalDateTime end,
                             Venue venue, EventCategory category, String organizerId, String organizerName,
                             Double price, Double currentAttendees, String imageUrl) {
        Event event = new Event(title, description, start, end, venue, category, organizerId, organizerName);
        event.setPrice(price);
        event.setCurrentAttendees(currentAttendees.intValue());
        event.setImageUrl(imageUrl);
        event.setTags(Arrays.asList("popular", "featured"));
        return event;
    }
}