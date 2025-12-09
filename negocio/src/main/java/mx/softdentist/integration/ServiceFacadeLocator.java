package mx.softdentist.integration;

import mx.softdentist.facade.*;

public class ServiceFacadeLocator {
    private static FacadeEmpleado facadeEmpleado;
    private static FacadeAdministrador facadeAdministrador;
    private static FacadePaciente facadePaciente;
    private static FacadeCita facadeCita;
    private static FacadeMensaje facadeMensaje;
    private static FacadePago facadePago;
    private static FacadeProducto facadeProducto;

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

    public static FacadePago getInstanceFacadePago() {
        if (facadePago == null) {
            facadePago = new FacadePago();
            return facadePago;
        } else {
            return facadePago;
        }
    }

    public static FacadeProducto getInstanceFacadeProducto() {
        if (facadeProducto == null) {
            facadeProducto = new FacadeProducto();
            return facadeProducto;
        } else {
            return facadeProducto;
        }
    }


}

