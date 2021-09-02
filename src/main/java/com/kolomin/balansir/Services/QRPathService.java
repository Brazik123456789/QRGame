package com.kolomin.balansir.Services;

import com.kolomin.balansir.Entities.QRPath;
import com.kolomin.balansir.Repositoeirs.QRPathRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QRPathService {
    private QRPathRepository qrPathRepository;

    @Autowired
    public QRPathService(QRPathRepository qrPathRepository) {
        this.qrPathRepository = qrPathRepository;
    }


    public void saveOrUpdate(QRPath qrPath) {
        qrPathRepository.save(qrPath);
    }
}
