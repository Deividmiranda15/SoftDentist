package mx.softdentist.ui;

import com.itextpdf.text.pdf.PdfWriter;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.ServletContext;
import jakarta.faces.application.FacesMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import mx.softdentist.entidad.Cita;
import mx.softdentist.entidad.Paciente;
import mx.softdentist.integration.ServiceLocator;

// Importaciones para PDF
import com.itextpdf.text.*;

// Importaciones para Calendario
import org.primefaces.PrimeFaces;
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

    private String vistaActual = "LISTA";
    private ScheduleModel eventModel;
    private ScheduleEvent<?> event;
    private String estadoSeleccionado;
    private String observacionesReceta;
    private Cita citaAReagendar;
    private LocalDate fechaReagendar;
    private String horaReagendar;
    private String motivoReagendar;
    private boolean ordenAscendente = true;
    private List<String> horasDisponiblesReagendar = new ArrayList<>();

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    @PostConstruct
    public void init() {
        cargarCitasConDetalles();
    }


    public void cambiarVista(String vista) {
        this.vistaActual = vista;
        if("CALENDARIO".equals(vista)) cargarCalendario();
    }

    private void cargarCitasConDetalles() {
        try {
            List<Cita> todas = ServiceLocator.getInstanceCitaDAO().obtenerTodasConPacientes();
            citas = todas.stream()
                    .filter(c -> c.getEstado() != Cita.EstadoCita.Cancelada)
                    .collect(Collectors.toList());
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

            generarPDF(rutaArchivo);

            enviarCorreoInterno(correoDestino, "Receta Médica - Dental Patron",
                    "Hola " + citaSeleccionada.getIdPaciente().getNombre() + ",\n\nAdjunto encontrarás tu receta médica.",
                    rutaArchivo);

            actualizarEstadoCita();
            mensajeInfo("Receta generada y enviada exitosamente.");

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

    public void prepararReagendamiento(Cita cita) {
        System.out.println("CLICK REAGENDAR");
        this.citaAReagendar = cita;
        this.fechaReagendar = null;
        this.horaReagendar = null;
        this.horasDisponiblesReagendar = new ArrayList<>();
    }

    public void onFechaReagendarSelect(SelectEvent<LocalDate> event) {
        this.fechaReagendar = event.getObject();
        cargarHorasDisponiblesReagendar();
    }

    private void cargarHorasDisponiblesReagendar() {
        if (fechaReagendar == null) {
            horasDisponiblesReagendar = new ArrayList<>();
            return;
        }

        if (fechaReagendar.isBefore(LocalDate.now())) {
            horasDisponiblesReagendar = new ArrayList<>();
            mensajeError("No puede reagendar a fechas pasadas.");
            return;
        }

        if (fechaReagendar.getDayOfWeek() == DayOfWeek.SUNDAY) {
            horasDisponiblesReagendar = new ArrayList<>();
            mensajeWarn("Los domingos no se atienden citas.");
            return;
        }

        List<LocalTime> generadas = generarHoras(fechaReagendar);

        if (fechaReagendar.equals(LocalDate.now())) {
            LocalTime ahora = LocalTime.now();
            generadas = generadas.stream()
                    .filter(h -> h.isAfter(ahora.plusMinutes(15))) // Margen de 15 min para cortesía
                    .collect(Collectors.toList());
        }

        List<LocalTime> ocupadas;
        if (citaAReagendar != null && citaAReagendar.getId() != null) {
            ocupadas = ServiceLocator.getInstanceCitaDAO()
                    .obtenerHorasOcupadasExcluyendo(fechaReagendar, citaAReagendar.getId());
        } else {
            ocupadas = ServiceLocator.getInstanceCitaDAO()
                    .obtenerHorasOcupadas(fechaReagendar);
        }

         horasDisponiblesReagendar = generadas.stream()
                .filter(h -> ocupadas.stream().noneMatch(o -> o.getHour() == h.getHour() && o.getMinute() == h.getMinute()))
                .map(h -> h.format(TIME_FORMATTER))
                .collect(Collectors.toList());

        if (horasDisponiblesReagendar.isEmpty()) {
            mensajeInfo("No hay horarios disponibles para el momento seleccionado.");
        }
    }

    private List<LocalTime> generarHoras(LocalDate fecha) {
        List<LocalTime> horas = new ArrayList<>();
        LocalTime inicio = LocalTime.of(10, 0);
        LocalTime fin = fecha.getDayOfWeek() == DayOfWeek.SATURDAY
                ? LocalTime.of(14, 0) : LocalTime.of(20, 0);

        LocalTime actual = inicio;
        while (!actual.isAfter(fin.minusMinutes(30))) {
            horas.add(actual);
            actual = actual.plusMinutes(30);
        }
        return horas;
    }

    public void confirmarReagendamiento() {
        try {
            if (citaAReagendar == null) {
                mensajeWarn("No hay cita seleccionada.");
                return;
            }
            if (fechaReagendar == null) {
                mensajeError("Seleccione una nueva fecha.");
                return;
            }
            if (horaReagendar == null || horaReagendar.isEmpty()) {
                mensajeError("Seleccione una nueva hora.");
                return;
            }

            LocalTime nuevaHora = LocalTime.parse(horaReagendar, TIME_FORMATTER);
            boolean exito = ServiceLocator.getInstanceCitaDAO()
                    .reagendarCita(citaAReagendar.getId(), fechaReagendar, nuevaHora, motivoReagendar);

            if (exito) {
                mensajeInfo("Cita reagendada al " + fechaReagendar + " a las " + horaReagendar + ".");
                cargarCitasConDetalles();
                PrimeFaces.current().executeScript("PF('dlgReagendar').hide()");
                limpiarReagendamiento();
            } else {
                mensajeError("La hora seleccionada ya no está disponible. Elija otra.");
                cargarHorasDisponiblesReagendar();
            }
        } catch (Exception e) {
            mensajeError("Error al reagendar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limpiarReagendamiento() {
        citaAReagendar = null;
        fechaReagendar = null;
        horaReagendar = null;
        motivoReagendar = null;
        horasDisponiblesReagendar = new ArrayList<>();
    }

    public List<Integer> getDiasDeshabilitados() { return List.of(0); }

    public Date getManana() {
        return Date.from(LocalDate.now().plusDays(1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public void toggleOrden() {
        ordenAscendente = !ordenAscendente;
        if (ordenAscendente) {
            citas.sort(Comparator.comparing(Cita::getFecha).thenComparing(Cita::getHora));
        } else {
            citas.sort(Comparator.comparing(Cita::getFecha).reversed().thenComparing(Comparator.comparing(Cita::getHora).reversed()));
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
    public Cita getCitaAReagendar() { return citaAReagendar; }
    public void setCitaAReagendar(Cita c) { this.citaAReagendar = c; }
    public LocalDate getFechaReagendar() { return fechaReagendar; }
    public Date getHoy() {
        return Date.from(LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    public void setFechaReagendar(LocalDate f) {
        this.fechaReagendar = f;
        if (f != null) cargarHorasDisponiblesReagendar();
    }
    public String getHoraReagendar() { return horaReagendar; }
    public void setHoraReagendar(String h) { this.horaReagendar = h; }
    public List<String> getHorasDisponiblesReagendar() { return horasDisponiblesReagendar; }
    public String getMotivoReagendar() { return motivoReagendar; }
    public void setMotivoReagendar(String m) { this.motivoReagendar = m; }
    public boolean isOrdenAscendente() { return ordenAscendente; }
}