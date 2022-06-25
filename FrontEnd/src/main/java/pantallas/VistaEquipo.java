package pantallas;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import objetos.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VistaEquipo {

    public Grid equipoGrid;
    public FormLayout equipoForm;
    Gson gson = new Gson();
    Equipo aux = null;

    public VistaEquipo() {
        //GRID
        Grid<Equipo> grid = new Grid<>();
        Grid.Column<Equipo> idColumn = grid.addColumn(Equipo::getId).setHeader("Id");
        Grid.Column<Equipo> usuarioColumn = grid.addColumn(Equipo::getTipo).setHeader("Tipo");
        Grid.Column<Equipo> equipoColumn = grid.addColumn(Equipo::getNombreSO).setHeader("Sistema Operativo");
        Grid.Column<Equipo> fechaInicioColumn = grid.addColumn(Equipo::getVersionSO).setHeader("Versión SO");
        Grid.Column<Equipo> fechaFinalColumn = grid.addColumn(Equipo::getTipoDisc).setHeader("Tipo de disco");
        Grid.Column<Equipo> fechaRealColumn = grid.addColumn(Equipo::getCapacidadDisc).setHeader("Capacidad");
        Grid.Column<Equipo> comentarioColumn = grid.addColumn(Equipo::getsNombre).setHeader("Software");

        //EDITAR
        //one click
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        FormLayout layoutEquipo = new FormLayout();

        IntegerField idField = new IntegerField();
        idField.setLabel("Id");
        TextField tipoField = new TextField();
        tipoField.setLabel("Tipo");
        TextField soField = new TextField();
        soField.setLabel("Sistema Operativo");
        TextField vsoField = new TextField();
        vsoField.setLabel("Version SO");
        TextField tipoDField = new TextField();
        tipoDField.setLabel("Tipo de disco");
        TextField capField = new TextField();
        capField.setLabel("Capacidad");
        TextField softwareField = new TextField();
        softwareField.setLabel("Software");

        layoutEquipo.add(idField, tipoField, soField,
                vsoField, tipoDField, capField, softwareField);

        //botones
        Button modificar = new Button("Modificar");
        Button add = new Button("Añadir");
        Button eliminar = new Button("Eliminar");

        idField.setEnabled(false);


        //añadir
        add.addClickListener(event -> {
            anadir(tipoField.getValue(), soField.getValue(), vsoField.getValue()
                    , tipoDField.getValue(), capField.getValue(), softwareField.getValue());
            idField.clear();
            tipoField.clear();
            soField.clear();
            vsoField.clear();
            tipoDField.clear();
            capField.clear();
            softwareField.clear();
            grid.setItems(getAll());
            eliminar.setEnabled(false);
            modificar.setEnabled(false);
        });
        //modificar
        modificar.addClickListener(event -> {
            modificar(idField.getValue(), tipoField.getValue(), soField.getValue(), vsoField.getValue(),
                    tipoDField.getValue(), capField.getValue(), softwareField.getValue(), aux);
            idField.setEnabled(false);
            idField.clear();
            tipoField.clear();
            soField.clear();
            vsoField.clear();
            tipoDField.clear();
            softwareField.clear();
            capField.clear();
            grid.setItems(getAll());
            add.setEnabled(true);
            eliminar.setEnabled(false);
            modificar.setEnabled(false);

        });

        modificar.setEnabled(false);
        modificar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        add.setEnabled(true);


        eliminar.addClickListener(event -> {
                    eliminar(idField.getValue(), tipoDField.getValue(), soField.getValue(), vsoField.getValue()
                            , tipoDField.getValue(), capField.getValue(), softwareField.getValue());
                    idField.setEnabled(false);
                    idField.clear();
                    soField.clear();
                    vsoField.clear();
                    tipoDField.clear();
                    capField.clear();
                    softwareField.clear();
                    grid.setItems(getAll());
                    add.setEnabled(true);
                    eliminar.setEnabled(false);
                    modificar.setEnabled(false);
                }
        );
        eliminar.setEnabled(false);
        eliminar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);


        grid.addItemClickListener(
                event -> {
                    idField.setValue(event.getItem().getId());
                    tipoField.setValue(event.getItem().getTipo());
                    soField.setValue(event.getItem().getNombreSO());
                    vsoField.setValue(event.getItem().getVersionSO());
                    tipoDField.setValue(event.getItem().getTipoDisc());
                    capField.setValue(event.getItem().getCapacidadDisc());
                    softwareField.setValue(event.getItem().getsNombre());
                    idField.setEnabled(false);
                    add.setEnabled(false);
                    modificar.setEnabled(true);
                    eliminar.setEnabled(true);
                    aux = new Equipo(event.getItem().getId(), event.getItem().getTipo(), event.getItem().getNombreSO(),
                            event.getItem().getVersionSO(), event.getItem().getTipoDisc(),
                            event.getItem().getCapacidadDisc(), event.getItem().getsNombre());

                });

        layoutEquipo.add(modificar, eliminar, add);

        grid.setItems(getAll());
        equipoForm = layoutEquipo;
        equipoGrid = grid;
    }

    public ArrayList<Equipo> getAll() {
        ArrayList<Equipo> todos = new ArrayList<>();
        try {
            //LLAMADA A LA API
            URL url = new URL("http://localhost:8080/equipos");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int codigoRespuesta = con.getResponseCode();
            //SI HA IDO BIEN LECTURA DE JASON
            if (codigoRespuesta == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String linea;
                StringBuilder respuesta = new StringBuilder();
                while ((linea = bufferedReader.readLine()) != null) {
                    respuesta.append(linea);
                }
                bufferedReader.close();
                todos = gson.fromJson(respuesta.toString(), new TypeToken<List<Equipo>>() {
                }.getType());
                System.out.println(respuesta.toString());
            }

        } catch (Exception e) {

        }

        return todos;
    }


    public void anadir(String tipo, String SO, String v_SO, String disc, String cap_Disc, String sNombre) {
        try {
            URL url = new URL("http://localhost:8080/equipos");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            Equipo equipo = new Equipo(1, tipo, SO, v_SO, disc, cap_Disc, sNombre);
            String jsonEquipo = gson.toJson(equipo);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonEquipo.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }

        } catch (Exception e) {

        }
    }

    public void modificar(int id, String tipo, String SO, String v_SO, String disc, String cap_Disc, String sNombre,
                          Equipo aux) {
        try {
            URL url = new URL("http://localhost:8080/modificarEquipo");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            ArrayList<Equipo> equipos = new ArrayList<>();
            Equipo nuevo = new Equipo(id, tipo, SO, v_SO, disc, cap_Disc, sNombre);
            equipos.add(aux);
            equipos.add(nuevo);
            String jsonEquipos = gson.toJson(equipos);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonEquipos.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }

        } catch (Exception e) {

        }
    }

    public void eliminar(int id, String tipo, String SO, String v_SO, String disc, String cap_Disc, String sNombre) {
        try {
            URL url = new URL("http://localhost:8080/eliminarEquipo");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            Equipo equipo = new Equipo(id, tipo, SO, v_SO, disc, cap_Disc, sNombre);
            String jsonEquipo = gson.toJson(equipo);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonEquipo.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }

        } catch (Exception e) {

        }
    }
}
