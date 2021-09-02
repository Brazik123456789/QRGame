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
    private Long people_count;
    @Column
    private boolean deleted;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QR> qrs;

    @Override
    public String toString() {
        return "{\n" +
                "\t\"id\": \"" + id + "\",\n" +
                "\t\"name\": \"" + name + "\",\n" +
                "\t\"city\": \"" + city + "\",\n" +
                "\t\"date\": \"" + date + "\",\n" +
                "\t\"area\": \"" + area + "\",\n" +
                "\t\"people_count\": \"" + people_count + "\",\n" +
                "\t\"deleted\": \"" + deleted + "\",\n" +
                "\t\"qrs\": " + qrs + "\n" +
                '}';
    }
}
