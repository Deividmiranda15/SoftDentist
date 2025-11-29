package mx.softdentist.integration;

import mx.softdentist.entidad.Cita;
import mx.softdentist.facade.FacadeAdministrador;
import mx.softdentist.facade.FacadeCita;
import mx.softdentist.facade.FacadeEmpleado;
import mx.softdentist.facade.FacadePaciente;
import mx.softdentist.facade.FacadeMensaje;

public class ServiceFacadeLocator {
    private static FacadeEmpleado facadeEmpleado;
    private static FacadeAdministrador facadeAdministrador;
    private static FacadePaciente facadePaciente;
    private static FacadeCita facadeCita;
    private static FacadeMensaje facadeMensaje;

    public static FacadeEmpleado getInstanceFacadeEmpleado() {
        if (facadeEmpleado == null) {
            facadeEmpleado = new FacadeEmpleado();
            return facadeEmpleado;
        } else {
            return facadeEmpleado;
        }
    }

    public static FacadeAdministrador getInstanceFacadeUsuario() {
        if (facadeAdministrador == null) {
            facadeAdministrador = new FacadeAdministrador();
            return facadeAdministrador;
        } else {
            return facadeAdministrador;
        }
    }

    public static FacadePaciente getInstanceFacadePaciente() {
        if (facadePaciente == null) {
            facadePaciente = new FacadePaciente();
            return facadePaciente;
        } else {
            return facadePaciente;
        }
    }

    public static FacadeCita getInstanceFacadeCita() {
        if (facadeCita == null) {
            facadeCita = new FacadeCita();
            return facadeCita;
        } else {
            return facadeCita;
        }
    }


    public static FacadeMensaje getInstanceFacadeMensaje() {
        if (facadeMensaje == null) {
            facadeMensaje = new FacadeMensaje();
            return facadeMensaje;
        } else {
            return facadeMensaje;
        }
    }


}

