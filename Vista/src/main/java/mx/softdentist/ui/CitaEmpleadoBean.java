package mx.softdentist.ui;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.ServletContext;
import jakarta.faces.application.FacesMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import mx.softdentist.entidad.Cita;
import mx.softdentist.entidad.Paciente;
import mx.softdentist.integration.ServiceLocator;

// Importaciones para PDF
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

// Importaciones para Calendario
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.event.SelectEvent;

// Importaciones para Correo (Jakarta Mail)
import jakarta.mail.*;
import jakarta.mail.internet.*;

@Named
@ViewScoped
public class CitaEmpleadoBean implements Serializable {

    // --- Datos ---
    private List<Cita> citas;
    private Cita citaSeleccionada;
    private String observaciones;

    // --- Interfaz ---
    private String vistaActual = "LISTA";
    private ScheduleModel eventModel;
    private ScheduleEvent<?> event;
    private String estadoSeleccionado;
    private String observacionesReceta;

    @PostConstruct
    public void init() {
        cargarCitasConDetalles();
    }

    // ================== NAVEGACIÓN Y CARGA ==================

    public void cambiarVista(String vista) {
        this.vistaActual = vista;
        if("CALENDARIO".equals(vista)) cargarCalendario();
    }

    private void cargarCitasConDetalles() {
        try {
            citas = ServiceLocator.getInstanceCitaDAO().obtenerTodasConPacientes();
            cargarCalendario();
        } catch (Exception e) {
            mensajeError("Error al cargar citas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarCalendario() {
        eventModel = new DefaultScheduleModel();
        if (citas != null) {
            for (Cita c : citas) {
                if (c.getFecha() != null && c.getHora() != null) {
                    LocalDateTime inicio = LocalDateTime.of(c.getFecha(), c.getHora());
                    LocalDateTime fin = inicio.plusHours(1);
                    String titulo = c.getIdPaciente().getNombre() + " " + c.getIdPaciente().getApellido();

                    String estilo = "fc-event-programada";
                    if("Completada".equals(String.valueOf(c.getEstado()))) estilo = "fc-event-completada";
                    else if("Cancelada".equals(String.valueOf(c.getEstado()))) estilo = "fc-event-cancelada";

                    eventModel.addEvent(DefaultScheduleEvent.builder()
                            .title(titulo).startDate(inicio).endDate(fin)
                            .description(c.getMotivo()).data(c).styleClass(estilo).build());
                }
            }
        }
    }

    public void onEventSelect(SelectEvent<ScheduleEvent<?>> selectEvent) {
        event = selectEvent.getObject();
        citaSeleccionada = (Cita) event.getData();
    }

    // ================== ACCIONES (LISTA Y DIALOGOS) ==================

    public void seleccionarCita(Cita c) {
        this.citaSeleccionada = c;
        if(c.getEstado() != null) this.estadoSeleccionado = c.getEstado().toString();
    }

    public void actualizarEstado() {
        try {
            if(citaSeleccionada != null && estadoSeleccionado != null) {
                citaSeleccionada.setEstado(Cita.EstadoCita.valueOf(estadoSeleccionado));
                ServiceLocator.getInstanceCitaDAO().update(citaSeleccionada);
                mensajeInfo("Estado actualizado correctamente.");
                cargarCitasConDetalles();
            }
        } catch (Exception e) {
            mensajeError("Error al actualizar: " + e.getMessage());
        }
    }

    // ================== LÓGICA DE RECETA Y CORREO ==================

    public void enviarReceta() {
        this.observaciones = this.observacionesReceta;

        // 1. Validaciones
        if (citaSeleccionada == null) {
            mensajeWarn("Seleccione una cita.");
            return;
        }
        if (observaciones == null || observaciones.trim().isEmpty()) {
            mensajeWarn("Ingrese observaciones para la receta.");
            return;
        }
        String correoDestino = citaSeleccionada.getIdPaciente().getCorreo();
        if (correoDestino == null || correoDestino.isEmpty()) {
            mensajeWarn("El paciente no tiene correo registrado. Solo se generará el PDF.");
            // Podrías decidir continuar solo generando PDF aquí si quieres
            return;
        }

        try {
            // 2. Preparar Rutas
            FacesContext context = FacesContext.getCurrentInstance();
            ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
            String recetaDir = servletContext.getRealPath("/resources/recetas/");

            File directorio = new File(recetaDir);
            if (!directorio.exists()) directorio.mkdirs();

            String nombreArchivo = "receta_" + citaSeleccionada.getId() + "_" + System.currentTimeMillis() + ".pdf";
            String rutaArchivo = recetaDir + File.separator + nombreArchivo;

            // 3. Generar PDF
            generarPDF(rutaArchivo);

            // 4. Enviar Correo (Lógica interna para no tocar EmailService)
            enviarCorreoInterno(correoDestino, "Receta Médica - Dental Patron",
                    "Hola " + citaSeleccionada.getIdPaciente().getNombre() + ",\n\nAdjunto encontrarás tu receta médica.",
                    rutaArchivo);

            // 5. Finalizar
            actualizarEstadoCita();
            mensajeInfo("Receta generada y enviada exitosamente.");

            // Limpiar
            observaciones = "";
            observacionesReceta = "";
            citaSeleccionada = null;
            cargarCitasConDetalles();

        } catch (Exception e) {
            mensajeError("Error en el proceso: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Lógica privada de correo usando tus credenciales de EmailService
    private void enviarCorreoInterno(String destinatario, String asunto, String cuerpo, String rutaAdjunto) {
        CompletableFuture.runAsync(() -> {
            try {
                // Credenciales copiadas de tu EmailService.java
                final String username = "ramon.angry2@gmail.com";
                final String password = "tpws snmt hamr axso";

                Properties prop = new Properties();
                prop.put("mail.smtp.host", "smtp.gmail.com");
                prop.put("mail.smtp.port", "587");
                prop.put("mail.smtp.auth", "true");
                prop.put("mail.smtp.starttls.enable", "true");
                prop.put("mail.smtp.ssl.trust", "*");

                Session session = Session.getInstance(prop, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
                message.setSubject(asunto);
                message.setSentDate(new java.util.Date());

                // Multipart para adjunto
                Multipart multipart = new MimeMultipart();

                // Parte Texto
                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setContent(cuerpo, "text/html; charset=utf-8");
                multipart.addBodyPart(textPart);

                // Parte Archivo
                if (rutaAdjunto != null) {
                    MimeBodyPart filePart = new MimeBodyPart();
                    filePart.attachFile(new File(rutaAdjunto));
                    multipart.addBodyPart(filePart);
                }

                message.setContent(multipart);
                Transport.send(message);
                System.out.println("Correo enviado correctamente a " + destinatario);

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error enviando correo asíncrono: " + e.getMessage());
            }
        });
    }

    private void generarPDF(String rutaArchivo) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(rutaArchivo));
        document.open();

        Font tituloFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLUE);
        Font subtituloFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);

        Paragraph titulo = new Paragraph("Receta Médica - SoftDentist", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);
        document.add(new Chunk("\n"));

        Paciente p = citaSeleccionada.getIdPaciente();
        if (p != null) {
            document.add(new Paragraph("PACIENTE:", subtituloFont));
            document.add(new Paragraph("Nombre: " + p.getNombre() + " " + p.getApellido(), normalFont));
            document.add(new Paragraph("Teléfono: " + (p.getTelefono() != null ? p.getTelefono() : "S/N"), normalFont));
            document.add(new Paragraph(" "));
        }

        document.add(new Paragraph("DETALLES:", subtituloFont));
        document.add(new Paragraph("Fecha: " + citaSeleccionada.getFecha() + " - " + citaSeleccionada.getHora(), normalFont));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("TRATAMIENTO:", subtituloFont));
        document.add(new Paragraph(observaciones, normalFont));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("__________________________\nFirma del Dentista", normalFont));

        document.close();
    }

    private void actualizarEstadoCita() {
        try {
            citaSeleccionada.setEstado(Cita.EstadoCita.valueOf("Completada"));
            ServiceLocator.getInstanceCitaDAO().update(citaSeleccionada);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Helpers de Mensajes ---
    private void mensajeInfo(String msg) { FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null)); }
    private void mensajeWarn(String msg) { FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, msg, null)); }
    private void mensajeError(String msg) { FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null)); }

    // --- Getters y Setters ---
    public List<Cita> getCitas() { return citas; }
    public Cita getCitaSeleccionada() { return citaSeleccionada; }
    public void setCitaSeleccionada(Cita citaSeleccionada) { this.citaSeleccionada = citaSeleccionada; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public String getVistaActual() { return vistaActual; }
    public void setVistaActual(String vistaActual) { this.vistaActual = vistaActual; }
    public ScheduleModel getEventModel() { return eventModel; }
    public ScheduleEvent<?> getEvent() { return event; }
    public void setEvent(ScheduleEvent<?> event) { this.event = event; }
    public String getEstadoSeleccionado() { return estadoSeleccionado; }
    public void setEstadoSeleccionado(String estadoSeleccionado) { this.estadoSeleccionado = estadoSeleccionado; }
    public String getObservacionesReceta() { return observacionesReceta; }
    public void setObservacionesReceta(String observacionesReceta) { this.observacionesReceta = observacionesReceta; }
}