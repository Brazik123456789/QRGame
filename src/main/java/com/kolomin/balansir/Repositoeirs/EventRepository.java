package com.kolomin.balansir.Repositoeirs;

import com.kolomin.balansir.Entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = "SELECT * FROM event_table WHERE deleted = false;", nativeQuery = true)
    Iterable<? extends Event> findAllNotDeleted();

    @Query(value = "SELECT id FROM event_table WHERE name = ?1", nativeQuery = true)
    Long existsByEventName(String eventName);
}
