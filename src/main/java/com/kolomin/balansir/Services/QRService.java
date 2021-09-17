package com.kolomin.balansir.Services;

import com.kolomin.balansir.Entities.QR;
import com.kolomin.balansir.Repositoeirs.QRRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QRService {
    private QRRepository qrRepository;

    @Autowired
    public QRService(QRRepository qrRepository) {
        this.qrRepository = qrRepository;
    }

    public void saveOrUpdate(QR newQR) {
        qrRepository.save(newQR);
    }

    public Iterable<? extends QR> findAll() {
        return qrRepository.findAll();
    }

    public Boolean existsByQRSuffix(String suffix) {
        if (qrRepository.existsByQRSuffix(suffix) != null){
            return true;
        } else {
            return false;
        }
    }

    public QR getBySuffix(String path) {
        return qrRepository.getBySuffix(path);
    }

    public boolean getTeamByQRSuffix(String path) {
        return qrRepository.getTeamByQRSuffix(path);
    }

    public QR getById(Long id) {
        return qrRepository.getById(id);
    }

    public void delete(QR qr) {
        qrRepository.delete(qr);
    }
}
