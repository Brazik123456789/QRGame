package com.kolomin.balansir.Entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "users_table")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String login;
    @Column
    private String password;
    @Column
    private String role;

    public User() {
    }

    public User(String login, String password, String role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }

    @Override
    public String toString() {
        return "\n{\n" +
                "\t\"id\": \"" + id + "\",\n" +
                "\t\"login\": \"" + login + "\"\n" +
                "}";
    }
}
