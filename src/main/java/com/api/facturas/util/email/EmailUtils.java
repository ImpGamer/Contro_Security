package com.api.facturas.util.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailUtils {
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String emailSender;

    public void sendSimpleMessage(String to, String subject, String text, List<String> list) {
        SimpleMailMessage message = new SimpleMailMessage();
        //Correo que enviara el mensaje
        message.setFrom(emailSender);
        //Correo al que le llegara el mensaje
        message.setTo(to);
        //Titulo del correo
        message.setSubject(subject);
        message.setText(text);

        if(list != null && !list.isEmpty()) {
            /*El atributo "cc" dentro de un mail, es una copia extra del mail que le llegara al destinatario*/
            message.setCc(getCcArray(list));
        }
        javaMailSender.send(message);
    }
    private String[] getCcArray(List<String> cclist) {
        String[] cc = new String[cclist.size()];
        for(int i=0;i<cclist.size();i++) {
            cc[i] = cclist.get(i);
        }
        return cc;
    }
    public void sendCredentials(String to, String subject,String password) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            //Creacion del helper del mensaje y parametro booleano el cual permite mucho contenido HTML y archivos
            MimeMessageHelper helper = new MimeMessageHelper(message,true);

            helper.setFrom(emailSender);
            helper.setTo(to);
            helper.setSubject(subject);

            final String HTML_MESSAGE = "<h4>Has pedido una recuperacion de contraseña</h4>" +
                    "                    <p>Tus credenciales son las siguientes:</p><br>" +
                    "                    <b>Correo Electronico: </b><br>"+to+
                    "                    <br><b>Contraseña: </b><br>"+password+
                    "<p>Ingresa estas credenciales para volver a iniciar sesion!</p>"+
                    "<a href=\"https://ko-fi.com/foundation_survive\">Inicia Sesion</a>";

            message.setContent(HTML_MESSAGE,"text/html");
            javaMailSender.send(message);

        }catch (MessagingException ex) {
            ex.printStackTrace();
        }
    }
}