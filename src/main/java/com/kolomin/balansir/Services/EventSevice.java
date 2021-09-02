package com.kolomin.balansir.Services;

import com.kolomin.balansir.Entities.Event;
import com.kolomin.balansir.Repositoeirs.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

@Service
public class EventSevice {
    private EventRepository eventRepository;

    @Autowired
    public EventSevice(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void saveOrUpdate(Event event) {
        eventRepository.save(event);
    }

    public Event getEventById(Long eventId) {
        return eventRepository.getById(eventId);
    }

    public void deleteEventById(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    public Iterable<? extends Event> findAll() {
        return eventRepository.findAll();
    }

    public void deleteAll() {
        eventRepository.deleteAll();
    }

    public Iterable<? extends Event> findAllNotDeleted() {
        return eventRepository.findAllNotDeleted();
    }

    public void deleteEvent(Event newEvent) {
        eventRepository.delete(newEvent);
    }

    public boolean findEventName(String eventName) {
        if (eventRepository.existsByEventName(eventName) != null){
            return true;
        } else {
            return false;
        }
    }
}
