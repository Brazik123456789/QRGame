package com.kolomin.balansir.Entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "event_table")
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String city;
    @Column
    private Date date;
    @Column
    private String area;
    @Column
    private boolean deleted;
    @Column
    private String qr_path;

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QR> qrs;

    @Override
    public String toString() {
        int people_count = 0;
        Long default_resource_people_count = 0L;
        for (QR qr: this.qrs) {
            default_resource_people_count += qr.getDefault_resource_people_count();
            for (Resource r: qr.getResources()) {
                people_count += r.getCame_people_count();
            }
        }

        return "{\n" +
                "\t\"id\": \"" + id + "\",\n" +
                "\t\"name\": \"" + name + "\",\n" +
                "\t\"city\": \"" + city + "\",\n" +
                "\t\"date\": \"" + date.toString().substring(8,10) + "-" + date.toString().substring(5,7) +  "-" + date.toString().substring(0,4) + "\",\n" +
                "\t\"unixtime\": " + date.getTime()/1000L + ",\n" +
                "\t\"area\": \"" + area + "\",\n" +
                "\t\"people_count\": " + people_count + default_resource_people_count + ",\n" +
                "\t\"default_resource_people_count\": " + default_resource_people_count + ",\n" +
                "\t\"qr_path\": \"" + qr_path + "\",\n" +
                "\t\"deleted\": " + deleted + ",\n" +
                "\t\"qrs\": " + qrs + "\n" +
                '}';
    }
}
