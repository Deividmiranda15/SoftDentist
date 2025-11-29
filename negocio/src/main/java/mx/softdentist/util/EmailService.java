package mx.softdentist.util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

/**
 * Servicio de correo electrónico para enviar correos
 */
public class EmailService {

    // Configuración para GMAIL
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587"; // Puerto TLS

    // Credenciales
    private static final String EMAIL_ORIGEN = "ramon.angry2@gmail.com";
    private static final String EMAIL_PASSWORD = "tpws snmt hamr axso";

    private final Session session;
    private static EmailService instance;

    Authenticator auth;

    private EmailService() {
        Properties props = new Properties();

        // Configuración Básica de Conexión
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");

        // Configuración de Seguridad
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // Confianza en Certificados (Todos para evitar errores con anitvirus)
        props.put("mail.smtp.ssl.trust", "*");

        // Timeouts para no congelar el hilo si algo falla
        props.put("mail.smtp.connectiontimeout", "15000"); // 15 segundos
        props.put("mail.smtp.timeout", "15000");
        props.put("mail.smtp.writetimeout", "15000");

        auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_ORIGEN, EMAIL_PASSWORD);
            }
        };

        // Creación de la sesión
        session = Session.getInstance(props, auth);

    }

    public static synchronized EmailService getInstance() {
        if (instance == null) {
            instance = new EmailService();
        }
        return instance;
    }

    public void enviarCorreoIndividual(String destinatario, String asunto, String cuerpo) {
        CompletableFuture.runAsync(() -> {
            try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(EMAIL_ORIGEN));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
                message.setSubject(asunto);
                message.setSentDate(new java.util.Date());
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent(cuerpo, "text/html; charset=utf-8");
                message.setContent(cuerpo, "text/html; charset=utf-8");

                Transport.send(message);


            } catch (MessagingException e) {
                System.err.println("Mensaje: " + e.getMessage());
                // Error completo
                e.printStackTrace();
            }
        });
    }

    public void enviarAnuncioMasivo(List<String> destinatarios, String asunto, String cuerpo) {
        if (destinatarios == null || destinatarios.isEmpty()) return;

        CompletableFuture.runAsync(() -> {
            try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(EMAIL_ORIGEN));
                // Truco: El "Para" eres tú mismo, los demás van en BCC (Ocultos)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(EMAIL_ORIGEN));

                InternetAddress[] direccionesBCC = new InternetAddress[destinatarios.size()];
                for (int i = 0; i < destinatarios.size(); i++) {
                    direccionesBCC[i] = new InternetAddress(destinatarios.get(i));
                }
                message.setRecipients(Message.RecipientType.BCC, direccionesBCC);

                message.setSubject(asunto);
                message.setText(cuerpo);

                Transport.send(message);

            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
    }
}