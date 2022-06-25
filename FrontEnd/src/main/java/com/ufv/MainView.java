package com.ufv;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.router.Route;


import pantallas.VistaEquipo;
import pantallas.VistaPrestamo;
import pantallas.VistaUsuario;



/**
 * The main view contains a button and a click listener.
 */
@Route
@PWA(name = "My Application", shortName = "My Application")
public class MainView extends VerticalLayout {
    //datos
    VerticalLayout datos = new VerticalLayout();


    public MainView() {


        //cabecera
        Button callUsuario = new Button("Usuarios",
                event -> cargarUsuario());
        Button callPrestamo = new Button("Prestamo",
                event -> cargarPrestamo());
        Button callEquipo = new Button("Equipo",
                event -> cargarEquipo());
        HorizontalLayout cabecera = new HorizontalLayout();
        cabecera.add(callUsuario,callPrestamo,callEquipo);

        add(cabecera);
        add(datos);

    }


    public void cargarUsuario(){
        datos.removeAll();
        VistaUsuario vistaUsuario = new VistaUsuario();
        datos.add( vistaUsuario.usuarioGrid, vistaUsuario.usuarioForm);
    }
    public void cargarPrestamo(){
        datos.removeAll();
        VistaPrestamo vistaPrestamo = new VistaPrestamo();
        datos.add(vistaPrestamo.prestamoGrid, vistaPrestamo.prestamoForm);
    }
    public void cargarEquipo(){
        datos.removeAll();
        VistaEquipo vistaEquipo = new VistaEquipo();
        datos.add(vistaEquipo.equipoGrid, vistaEquipo.equipoForm);

    }

}
