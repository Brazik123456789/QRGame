package com.kolomin.balansir.Repositoeirs;

import com.kolomin.balansir.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query(value = "SELECT * FROM users_table WHERE login = ?1", nativeQuery = true)
    User getByLogin(String login);
}
