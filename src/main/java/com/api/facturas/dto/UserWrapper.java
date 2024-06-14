package com.api.facturas.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWrapper {
    private String id;
    private String nombre;
    private String email;
    private String numeroContacto;
    private String status;
}
