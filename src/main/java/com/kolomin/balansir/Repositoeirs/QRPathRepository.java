package com.kolomin.balansir.Repositoeirs;

import com.kolomin.balansir.Entities.QRPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QRPathRepository extends JpaRepository<QRPath, Long> {
}
