package com.api.facturas.repository;

import com.api.facturas.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    @Query(value = "SELECT u FROM User u WHERE u.email = :email")
    User findUserByEmail(@Param("email")String email);
    @Query(value = "SELECT u FROM User u WHERE u.rol = 'user'")
    List<User> getOnlyUsers();
    @Query(value = "SELECT u.email FROM User u WHERE u.rol = 'admin'")
    List<String> getAllAdmins();
}
