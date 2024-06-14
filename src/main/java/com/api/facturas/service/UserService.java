package com.api.facturas.service;

import com.api.facturas.constants.FacturaConstantes;
import com.api.facturas.dto.UserCredentialDTO;
import com.api.facturas.dto.UserWrapper;
import com.api.facturas.mappers.UserMapper;
import com.api.facturas.pojo.User;
import com.api.facturas.repository.UserRepository;
import com.api.facturas.security.CustomerDetailService;
import com.api.facturas.security.jwt.JwtFilter;
import com.api.facturas.security.jwt.JwtUtil;
import com.api.facturas.util.FacturaUtils;
import com.api.facturas.util.email.EmailUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("ALL")
@Service
@Slf4j
public class UserService {
    @Autowired
            private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomerDetailService customerDetailService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    private EmailUtils emailUtils;

    //Funcion del service para registrarse
    public ResponseEntity<?> signUp(UserCredentialDTO userCredentials) {
        try {
            if(validateUser(userCredentials)) {
                User repeatUser = userRepository.findUserByEmail(userCredentials.getEmail());
                if(repeatUser == null) {
                    userRepository.save(UserMapper.credentials_to_Entity(userCredentials));
                    return FacturaUtils.getResponseEntity("Usuario registrado!",
                            HttpStatus.CREATED);
                } else {
                    //Tambien puede ser remplazado por una excepcion
                    return FacturaUtils.getResponseEntity("El correo ingresado ya se encuentra registrado",
                            HttpStatus.NOT_ACCEPTABLE);
                }
            } else {
                return FacturaUtils.getResponseEntity(FacturaConstantes.INVALID_DATA,HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ResponseEntity<String> login(UserCredentialDTO userCredentialDTO) {
        log.info("Dentro de login");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userCredentialDTO.getEmail(),userCredentialDTO.getPassword())
            );
            if(authentication.isAuthenticated()) {
                if(customerDetailService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<>(
                            "{\"token\":\"" + jwtUtil.createToken(userCredentialDTO,
                                    customerDetailService.getUserDetail().getRol()) + "\"}", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Espere la aprobacion del administrador",HttpStatus.OK);
                }
            }
        }catch (Exception ex) {
            log.error(String.valueOf(ex));
        }
        return new ResponseEntity<>("Credenciales incorrectas",HttpStatus.BAD_REQUEST);
    }
    public ResponseEntity<List<UserWrapper>> getAllUsers() {
        try {
            if(jwtFilter.isAdmin()) {
                List<UserWrapper> usersWrapper = userRepository.getOnlyUsers().stream()
                        .map(UserMapper::entity_to_Wrapper) // Cambia `forEach` por `map`
                        .toList();
                return new ResponseEntity<>(usersWrapper,HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
    public ResponseEntity<String> updateUserStatus(String status,String id) {
        try {
            System.out.println("Se ejecuta esta funcion");
            if(jwtFilter.isAdmin()) {
                Optional<User> userBBDD = userRepository.findById(id);
                if(userBBDD.isPresent()) {
                    User userSave = userBBDD.get();
                    userSave.setStatus(status);
                    userRepository.save(userSave);
                    sendEmailToAdmins(userSave.getStatus(),userSave.getEmail(),userRepository.getAllAdmins());
                    return ResponseEntity.ok("Status del usuario actualizado correctamente");
                } else {
                    return FacturaUtils.getResponseEntity("Este usuario no existe",HttpStatus.NOT_FOUND);
                }
            } else {
                return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    public ResponseEntity<String> changePassword(String newPassword) {
        try {
            User userBBDD = userRepository.findUserByEmail(jwtFilter.getCurrentUser());
            if(userBBDD != null && !userBBDD.getPassword().equals(newPassword)) {
                userBBDD.setPassword(newPassword);
                userRepository.save(userBBDD);
                emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Contraseña Cambiada",
                        "Hola! "+jwtFilter.getCurrentUser()+" tu contraseña ha sido cambiada correctamente\nSi este"+
                        " cambio de contraseña no lo realizaste tu te sugerimos que te pongas en contacto con nosotros.",new ArrayList<>());

                return ResponseEntity.ok("Su contraseña ha sido modificada correctamente");
            }
            return new ResponseEntity<>("Error al tratar de cambiar contraseña. Intentelo mas tarde",HttpStatus.BAD_REQUEST);
        }catch (Exception ex) {
            ex.printStackTrace();
            return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ResponseEntity<String> forgotPassword(String userMail) {
        User userBBDD = userRepository.findUserByEmail(userMail);
        if(userBBDD != null && !Strings.isNullOrEmpty(userBBDD.getEmail())) {
            emailUtils.sendCredentials(userMail,"Recuperacion de Credenciales",userBBDD.getPassword());
            return ResponseEntity.ok("Sus credenciales han sido enviadas a su correo '"+userMail+"'");
        }
        return new ResponseEntity<>("No se pudo encontrar el usuario "+userMail+". Verifique sus datos.",HttpStatus.BAD_REQUEST);
    }

    private boolean validateUser(UserCredentialDTO userCredentials) {
        return !userCredentials.getPassword().isEmpty() && !userCredentials.getEmail().isEmpty()
                && !userCredentials.getEmail().isBlank() && !userCredentials.getNombre().isEmpty()
                && userCredentials.getNumeroContacto().length() == 10;
    }
    private void sendEmailToAdmins(String status,String user,List<String> allAdmins) {
        if(status != null && status.equalsIgnoreCase("true")) {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Cuenta aprobada",
                    "Usuario: "+user+"\nfue aprobado por Admin: "+jwtFilter.getCurrentUser(),
                    allAdmins);
        } else {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Cuenta desaprobada",
                    "Usuario: "+user+"\nfue desaprobado por Admin: "+jwtFilter.getCurrentUser(),
                    allAdmins);
        }
    }
}