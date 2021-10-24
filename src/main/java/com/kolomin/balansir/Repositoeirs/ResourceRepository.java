package com.kolomin.balansir.Repositoeirs;

import com.kolomin.balansir.Entities.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    @Query(value = "SELECT id FROM resouces_table WHERE url = ?1", nativeQuery = true)
    Long findUrl(String url);

    @Query(value = "SELECT * FROM resouces_table WHERE qr_suffix = ?1 AND deleted = false ORDER BY came_people_count LIMIT 1;", nativeQuery = true)
    Resource getByQRSuffixNotDeletedAndCamePeopleCountMin(String path);

    @Query(value = "SELECT * FROM resouces_table WHERE qr_suffix = ?1 AND deleted = false LIMIT 1;", nativeQuery = true)
    Resource getByQRSuffixNotDeleted(String path);
}
