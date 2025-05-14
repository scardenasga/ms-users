package co.edu.unbosque.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;

import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class CorreoSuscripcion implements StrategyEmail{

    private final String destinatario;
    private final String nombre;
    private final String tipo;
    private final BigDecimal precio;
    private final String fecha;

    public CorreoSuscripcion(String destinatario, String nombre, String tipo, BigDecimal precio, String fecha) {
        this.destinatario = destinatario;
        this.nombre = nombre;
        this.tipo = tipo;
        this.precio = precio;
        this.fecha = fecha;
    }
    @Override
    public void enviarCorreo() {
       
       try {
            Session sesion = EmailService.getSession();
            MimeMessage mensaje = new MimeMessage(sesion);
            mensaje.setFrom(new InternetAddress(EmailService.getRemitente()));
            mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
            mensaje.setSubject("¡Gracias por tu suscripción!");
            String contenido = new String(Files.readAllBytes(Paths.get("src/main/webapp/pagos.xhtml")));

            contenido = contenido
                    .replace("${nombre}", nombre)
                    .replace("${tipo}", tipo)
                    .replace("${precio}", precio.toString())
                    .replace("${fecha}", fecha);

            mensaje.setContent(contenido, "text/html; charset=utf-8");
            Transport.send(mensaje);

        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }
    

}
