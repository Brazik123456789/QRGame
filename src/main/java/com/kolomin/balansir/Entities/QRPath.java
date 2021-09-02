package com.kolomin.balansir.Entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "qrpath_table")
@Data
public class QRPath {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "qr_id", referencedColumnName = "id")
    private QR qr;

    @Column
    private String path;
    @Column
    private boolean deleted;

    @Override
    public String toString() {
        return "{\n" +
                "\t\t\t\t\"id\": \"" + id + "\",\n" +
                "\t\t\t\t\"qr_id\": \"" + qr.getId() + "\",\n" +
                "\t\t\t\t\"path\": \"" + path + "\",\n" +
                "\t\t\t\t\"deleted\": \"" + deleted + "\"\n" +
                "\t\t\t}";
    }
}
