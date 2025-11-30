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
import java.util.List;
import mx.softdentist.entidad.Cita;
import mx.softdentist.entidad.Paciente;
import mx.softdentist.integration.ServiceLocator;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

@Named
@ViewScoped
public class CitaEmpleadoBean implements Serializable {

    private List<Cita> citas;
    private Cita citaSeleccionada;
    private String observaciones;

    @PostConstruct
    public void init() {
        cargarCitasConDetalles();
    }

    private void cargarCitasConDetalles() {
        try {
            citas = ServiceLocator.getInstanceCitaDAO().obtenerTodasConPacientes();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error al cargar citas: " + e.getMessage(), null));
            e.printStackTrace();
        }
    }

    public void generarReceta() {
        try {
            if (citaSeleccionada == null) {
                mostrarMensaje("Seleccione una cita.", FacesMessage.SEVERITY_WARN);
                return;
            }

            if (observaciones == null || observaciones.trim().isEmpty()) {
                mostrarMensaje("Ingrese observaciones para la receta.", FacesMessage.SEVERITY_WARN);
                return;
            }

            // Crear directorio si no existe
            FacesContext context = FacesContext.getCurrentInstance();
            ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
            String recetaDir = servletContext.getRealPath("/resources/recetas/");

            File directorio = new File(recetaDir);
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            String nombreArchivo = "receta_cita" + citaSeleccionada.getId() + ".pdf";
            String rutaArchivo = recetaDir + File.separator + nombreArchivo;

            // Generar PDF
            generarPDF(rutaArchivo);

            // Actualizar estado a "Completada"
            actualizarEstadoCita();

            mostrarMensaje("Receta generada exitosamente. Estado actualizado a 'Completada'.",
                    FacesMessage.SEVERITY_INFO);

            // Limpiar y recargar
            observaciones = "";
            citaSeleccionada = null;
            cargarCitasConDetalles();

        } catch (Exception e) {
            mostrarMensaje("Error al generar receta: " + e.getMessage(),
                    FacesMessage.SEVERITY_ERROR);
            e.printStackTrace();
        }
    }

    private void generarPDF(String rutaArchivo) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(rutaArchivo));
        document.open();

        // Configuración del PDF
        Font tituloFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLUE);
        Font subtituloFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);

        // Título
        Paragraph titulo = new Paragraph("Receta Médica - SoftDentist", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        // Línea separadora
        document.add(new Chunk("\n"));

        // Información del paciente
        Paciente paciente = citaSeleccionada.getIdPaciente();
        if (paciente != null) {
            document.add(new Paragraph("PACIENTE:", subtituloFont));
            document.add(new Paragraph("Nombre: " + paciente.getNombre() + " " + paciente.getApellido(), normalFont));
            document.add(new Paragraph("Teléfono: " + (paciente.getTelefono() != null ? paciente.getTelefono() : "No especificado"), normalFont));
            document.add(new Paragraph("Correo: " + (paciente.getCorreo() != null ? paciente.getCorreo() : "No especificado"), normalFont));
            document.add(new Paragraph(" "));
        }

        // Información de la cita
        document.add(new Paragraph("INFORMACIÓN DE LA CITA:", subtituloFont));
        document.add(new Paragraph("Fecha: " + citaSeleccionada.getFecha(), normalFont));
        document.add(new Paragraph("Hora: " + citaSeleccionada.getHora(), normalFont));
        document.add(new Paragraph("Motivo: " + citaSeleccionada.getMotivo(), normalFont));
        document.add(new Paragraph(" "));

        // Observaciones
        document.add(new Paragraph("OBSERVACIONES Y TRATAMIENTO:", subtituloFont));
        Paragraph obsParagraph = new Paragraph(observaciones, normalFont);
        obsParagraph.setSpacingAfter(15);
        document.add(obsParagraph);

        // Firma
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Firma del dentista: __________________________", normalFont));
        document.add(new Paragraph("Nombre: " + obtenerNombreEmpleado(), normalFont));
        document.add(new Paragraph("Fecha de emisión: " + java.time.LocalDate.now(), normalFont));

        document.close();
    }

    private void actualizarEstadoCita() {
        try {
            citaSeleccionada.setEstado(Cita.EstadoCita.valueOf("Completada"));
            ServiceLocator.getInstanceCitaDAO().update(citaSeleccionada);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar estado de la cita: " + e.getMessage(), e);
        }
    }

    private String obtenerNombreEmpleado() {
        // Aquí puedes obtener el nombre del empleado logueado
        // Por ahora retornamos un valor por defecto
        return "Dr. " + (citaSeleccionada.getIdEmpleado() != null ?
                citaSeleccionada.getIdEmpleado().getNombre() : "Dentista");
    }

    private void mostrarMensaje(String mensaje, FacesMessage.Severity severidad) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severidad, mensaje, null));
    }

    public String obtenerNombrePaciente(Paciente paciente) {
        if (paciente == null) return "(Sin paciente asignado)";
        return paciente.getNombre() + " " + paciente.getApellido();
    }

    // Getters y Setters
    public List<Cita> getCitas() { return citas; }
    public Cita getCitaSeleccionada() { return citaSeleccionada; }
    public void setCitaSeleccionada(Cita citaSeleccionada) { this.citaSeleccionada = citaSeleccionada; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}