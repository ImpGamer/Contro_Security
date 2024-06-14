package com.api.facturas.rest;

import com.api.facturas.constants.FacturaConstantes;
import com.api.facturas.dto.UserCredentialDTO;
import com.api.facturas.dto.UserWrapper;
import com.api.facturas.service.UserService;
import com.api.facturas.util.FacturaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/signUp")
    public ResponseEntity<?> registrarUsuario(@RequestBody UserCredentialDTO userCredentialDTO) {
        return userService.signUp(userCredentialDTO);
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserCredentialDTO userCredentialDTO) {
        try {
            return userService.login(userCredentialDTO);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @GetMapping("/getAll")
    public ResponseEntity<List<UserWrapper>> obtenerUsuarios() {
        try {
            return userService.getAllUsers();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @PutMapping("/edit/{id}")
    public ResponseEntity<String> actualizarUsuario(@RequestParam(name = "status")String status,@PathVariable String id) {
        return userService.updateUserStatus(status,id);
    }
    @PostMapping("/changePassword")
    public ResponseEntity<String> cambiarContraseina(@RequestParam(name = "password")String password) {
        try {
            return userService.changePassword(password);
        }catch (Exception ex) {
            ex.printStackTrace();
            return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/regainPassword")
    public ResponseEntity<String> recoverPassword(@RequestParam(name = "recuperationMail")String email) {
        try {
            return userService.forgotPassword(email);
        }catch (Exception ex) {
            ex.printStackTrace();
            return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
