package com.kolomin.balansir.Entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resouces_table")
@Data
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "qr_id")
    private QR qr;
    @Column
    private String qr_suffix;
    @Column
    private boolean infinity;
    @Column
    private String url;
    @Column
    private Long people_count;
    @Column
    private Long came_people_count;
    @Column
    private boolean deleted;

    @Override
    public String toString() {
        return "\n\t\t\t\t{\n" +
                "\t\t\t\t\t\"id\": \"" + id + "\",\n" +
                "\t\t\t\t\t\"qr_id\": \"" + qr.getId() + "\",\n" +
                "\t\t\t\t\t\"qr_suffix\": \"" + qr_suffix + "\",\n" +
                "\t\t\t\t\t\"infinity\": \"" + infinity + "\",\n" +
                "\t\t\t\t\t\"url\": \"" + url + "\",\n" +
                "\t\t\t\t\t\"people_count\": \"" + people_count + "\",\n" +
                "\t\t\t\t\t\"came_people_count\": \"" + came_people_count + "\",\n" +
                "\t\t\t\t\t\"deleted\": \"" + deleted + "\"\n" +
                "\t\t\t\t}";
    }
}
