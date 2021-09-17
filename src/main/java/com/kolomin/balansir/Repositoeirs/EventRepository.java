package com.kolomin.balansir.Repositoeirs;

import com.kolomin.balansir.Entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    @Query(value = "SELECT * FROM event_table WHERE deleted = false;", nativeQuery = true)
    Iterable<? extends Event> findAllNotDeleted();

    @Query(value = "SELECT * FROM event_table WHERE deleted = true;", nativeQuery = true)
    Iterable<? extends Event> findAllDeleted();

    @Query(value = "SELECT id FROM event_table WHERE name = ?1", nativeQuery = true)
    Long existsByEventName(String eventName);

    @Query(value = "SELECT * FROM event_table WHERE name LIKE CONCAT('%', ?1, '%') AND city LIKE CONCAT('%', ?2, '%') AND area LIKE CONCAT('%', ?3, '%') AND date LIKE CONCAT('%', ?4, '%') AND deleted = true", nativeQuery = true)
    Iterable<? extends Event> findAllByQueryDeletedTrue(String name, String city, String area, String date);

    @Query(value = "SELECT * FROM event_table WHERE name LIKE CONCAT('%', ?1, '%') AND city LIKE CONCAT('%', ?2, '%') AND area LIKE CONCAT('%', ?3, '%') AND date LIKE CONCAT('%', ?4, '%') AND deleted = false", nativeQuery = true)
    Iterable<? extends Event> findAllByQueryDeletedFalse(String name, String city, String area, String date);

//    Iterable<? extends Event> findBy
}
