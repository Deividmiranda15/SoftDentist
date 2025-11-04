/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.softdentist.integration;

import jakarta.persistence.EntityManager;
import mx.softdentist.dao.*;
import mx.softdentist.persistence.HibernateUtil;


/**
 *
 * @author total
 */
public class ServiceLocator {

    private static EmpleadoDAO empleadoDAO;
    private static PacienteDAO pacienteDAO;
    private static CitaDAO citaDAO;
    private static AdministradorDAO administradorDAO;

    private static EntityManager getEntityManager(){
        return HibernateUtil.getEntityManager();
    }

    public static EmpleadoDAO getInstanceEmpleadoDAO(){
        if(empleadoDAO == null){
            empleadoDAO = new EmpleadoDAO(getEntityManager());
            return empleadoDAO;
        } else{
            return empleadoDAO;
        }
    }


    public static PacienteDAO getInstancePacienteDAO(){
        if(pacienteDAO == null){
            pacienteDAO = new PacienteDAO(getEntityManager());
            return pacienteDAO;
        } else{
            return pacienteDAO;
        }
    }

    public static CitaDAO getInstanceCitaDAO(){
        if(citaDAO == null){
            citaDAO = new CitaDAO(getEntityManager());
            return citaDAO;
        } else{
            return citaDAO;
        }
    }

    public static AdministradorDAO getInstanceAdministradorDAO(){
        if(administradorDAO == null){
            administradorDAO = new AdministradorDAO(getEntityManager());
            return administradorDAO;
        } else{
            return administradorDAO;
        }
    }


}