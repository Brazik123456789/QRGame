package com.kolomin.balansir.Repositoeirs;

import com.kolomin.balansir.Entities.QR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QRRepository extends JpaRepository<QR, Long> {

    @Query(value = "SELECT id FROM qr_table WHERE qr_suffix = ?1", nativeQuery = true)
    Long existsByQRSuffix(String qr_suffix);
}