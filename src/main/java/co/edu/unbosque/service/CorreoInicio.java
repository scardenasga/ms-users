package co.edu.unbosque.service;

import java.nio.file.Files;
import java.nio.file.Paths;

import jakarta.mail.Session;
import jakarta.mail.Message;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
public class CorreoInicio implements StrategyEmail {

    private final String destinatario;
    private final String codigo;
    

    public CorreoInicio (String destinatario, String codigo){
        this.destinatario= destinatario;
        this.codigo = codigo;
    }
    @Override
    public void enviarCorreo() {
       try {
            Session sesion = EmailService.getSession();
            MimeMessage mensaje = new MimeMessage(sesion);
            mensaje.setFrom(new InternetAddress(EmailService.getRemitente()));
            mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
            mensaje.setSubject("Código de verificación");

            String contenido = new String(Files.readAllBytes(Paths.get("src/main/webapp/index.xhtml")));
            contenido = contenido.replace("${codigo}", codigo).replace("${aux}", destinatario);

            mensaje.setContent(contenido, "text/html");
            Transport.send(mensaje);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
