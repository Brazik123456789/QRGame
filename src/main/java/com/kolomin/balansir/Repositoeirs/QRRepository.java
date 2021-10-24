package com.kolomin.balansir.Repositoeirs;

import com.kolomin.balansir.Entities.QR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QRRepository extends JpaRepository<QR, Long> {

    @Query(value = "SELECT id FROM qr_table WHERE qr_suffix = ?1", nativeQuery = true)
    Long existsByQRSuffix(String qr_suffix);

    @Query(value = "SELECT * FROM qr_table WHERE qr_suffix = ?1", nativeQuery = true)
    QR getBySuffix(String path);

    @Query(value = "SELECT team FROM qr_table WHERE qr_suffix = ?1", nativeQuery = true)
    boolean getTeamByQRSuffix(String path);
}