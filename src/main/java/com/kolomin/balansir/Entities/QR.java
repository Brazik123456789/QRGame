package com.kolomin.balansir.Entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

import static com.kolomin.balansir.Config.ConfigHandler.beforeQRsPath;

@Entity
@Table(name = "qr_table")
@Data
public class QR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String qr_suffix;

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Event event;

    @Column
    private String qr_url;
    @Column
    private boolean team;
    @Column
    private boolean team_for_front;
    @Column
    private boolean deleted;
    @Column
    private Long default_resource_people_count;

    @OneToMany(mappedBy = "qr", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resource> resources;

    @Override
    public String toString() {
        int people_count = 0;
        String qr_path = this.event.getQr_path() + "/" + this.qr_suffix + ".png";
        for (Resource r: this.resources) {
            people_count += r.getCame_people_count();
        }
        return "\n\t\t{\n" +
                "\t\t\t\"id\": \"" + id + "\",\n" +
                "\t\t\t\"qr_suffix\": \"" + qr_suffix + "\",\n" +
                "\t\t\t\"event_id\": \"" + event.getId() + "\",\n" +
                "\t\t\t\"people_count\": " + people_count + ",\n" +
                "\t\t\t\"qr_url\": \"" + qr_url + "\",\n" +
                "\t\t\t\"team\": " + team + ",\n" +
                "\t\t\t\"teamForFront\": " + team_for_front + ",\n" +
                "\t\t\t\"deleted\": " + deleted + ",\n" +
                "\t\t\t\"qrPath\": \"" + beforeQRsPath + qr_path + "\",\n" +
                "\t\t\t\"default_resource_people_count\": " + default_resource_people_count + ",\n" +
                "\t\t\t\"resources\": " + resources + "\n" +
                "\t\t}";
    }
}
