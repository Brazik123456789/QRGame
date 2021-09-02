package com.kolomin.balansir.Repositoeirs;

import com.kolomin.balansir.Entities.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    @Query(value = "SELECT id FROM resouces_table WHERE url = ?1", nativeQuery = true)
    Long findUrl(String url);
}
