package com.kolomin.balansir.Entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "qr_table")
@Data
public class QR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String qr_suffix;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "event_id")
    private Event event;

    @Column
    private Long people_count;
    @Column
    private String qr_url;

    @Column
    private boolean team;
    @Column
    private boolean deleted;

    @OneToOne(mappedBy = "qr", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private QRPath qrPath;

    @OneToMany(mappedBy = "qr", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resource> resources;

    @Override
    public String toString() {
        return "\n\t\t{\n" +
                "\t\t\t\"id\": \"" + id + "\",\n" +
                "\t\t\t\"qr_suffix\": \"" + qr_suffix + "\",\n" +
                "\t\t\t\"event_id\": \"" + event.getId() + "\",\n" +
                "\t\t\t\"people_count\": \"" + people_count + "\",\n" +
                "\t\t\t\"qr_url\": \"" + qr_url + "\",\n" +
                "\t\t\t\"team\": \"" + team + "\",\n" +
                "\t\t\t\"deleted\": \"" + deleted + "\",\n" +
                "\t\t\t\"qrPath\": " + qrPath + ",\n" +
                "\t\t\t\"resources\": " + resources + "\n" +
                "\t\t}";
    }
}
