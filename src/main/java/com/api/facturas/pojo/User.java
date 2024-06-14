package com.api.facturas.pojo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Random;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@DynamicUpdate
@DynamicInsert
@Entity
public class User {
    @Id
    private String id = UUID.randomUUID().toString();
    @Column(length = 50,nullable = false)
    private String nombre;
    @Column(name="contacto",length = 20,nullable = false)
    private String numeroContacto;
    @Column(length = 100,nullable = false,unique = true)
    private String email;
    @Column(nullable = false,unique = true,length = 35)
    private String password;
    @Column
    private String status;

    private String rol;

    @Override
    public String toString() {
        return "Nombre: "+this.nombre+"\nContacto: "+this.numeroContacto+
                "\nEmail: "+this.email+"\nPassword: "+this.password+
                "\nStatus: "+this.status+"\nRol: "+this.rol;
    }
    @PrePersist
    private void setStatus() {
        Random random = new Random();
        int randomNum = random.nextInt(2)+1;
        this.status = randomNum == 1?"true":"false";
        this.rol = randomNum == 1?"admin":"user";
    }
}
