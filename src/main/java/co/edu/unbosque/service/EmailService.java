package co.edu.unbosque.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Random;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final String remitente = "grupoaccioneselbosque@gmail.com";
    private static final String password = "mrktnwdshnmkukhc";
    private static Session sesion;

    private static Session getSession() {
        if (sesion == null) {
        	Properties prop = new Properties();
        	prop.put("mail.smtp.host", "smtp.gmail.com");  // El host del servidor SMTP
        	prop.put("mail.smtp.port", "587"); // El puerto SMTP con STARTTLS
        	prop.put("mail.smtp.auth", "true"); // Habilitar autenticación SMTP
        	prop.put("mail.smtp.starttls.enable", "true"); // Habilitar STARTTLS
        	prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");

            sesion = Session.getInstance(prop, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(remitente, password);
                }
            });
        }
        return sesion;
    }

    public static void enviarCorreo(String codigo, String destinatario, String identification) {
        System.out.println("INICIO DE ENVÍO DE CORREO");
        System.out.println("Código: " + codigo);
        System.out.println("Destinatario: " + destinatario);

        try {
            System.out.println("Obteniendo sesión...");
            Session sesion = getSession();
            System.out.println("Sesión obtenida");

            System.out.println("Creando mensaje...");
            MimeMessage mensaje = new MimeMessage(sesion);
            mensaje.setFrom(new InternetAddress(remitente));
            mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
            mensaje.setSubject("Código de seguridad de la cuenta (" + codigo + ")");

            System.out.println("Leyendo plantilla...");
            String contenido = new String(Files.readAllBytes(Paths.get("src/main/webapp/index.xhtml")));
            contenido = contenido.replace("${codigo}", codigo);
            contenido = contenido.replace("${aux}", destinatario);

            mensaje.setContent(contenido, "text/html");
            System.out.println("Mensaje listo. Enviando...");

            Transport.send(mensaje);
            System.out.println("Correo enviado correctamente");

        } catch (Exception e) {
            System.out.println("ERROR durante el envío del correo");
            e.printStackTrace();
        }

    }

    public String generarCodigo() {
        Random random = new Random();
        int codigo = 100000 + random.nextInt(900000);
        return String.valueOf(codigo);
    }
}
