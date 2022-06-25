package pantallas;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import objetos.Prestamo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VistaPrestamo {
    public Grid prestamoGrid;
    public FormLayout prestamoForm;
    Gson gson = new Gson();
    Prestamo aux = null;

    public VistaPrestamo(){
        //GRID
        Grid<Prestamo> grid = new Grid<>();
        Grid.Column<Prestamo> idColumn = grid.addColumn(Prestamo::getId).setHeader("Id");
        Grid.Column<Prestamo> usuarioColumn = grid.addColumn(Prestamo::getuId).setHeader("Id Usuario");
        Grid.Column<Prestamo> equipoColumn = grid.addColumn(Prestamo::geteId).setHeader("Id Equipo");
        Grid.Column<Prestamo> fechaInicioColumn = grid.addColumn(Prestamo::getfIni).setHeader("Fecha Inicio");
        Grid.Column<Prestamo> fechaFinalColumn = grid.addColumn(Prestamo::getfFin).setHeader("Fecha Final");

        //EDITAR
        //one click
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        FormLayout layoutPrestamo = new FormLayout();

        IntegerField idField = new IntegerField();
        idField.setLabel("Id");
        IntegerField usuarioField = new IntegerField();
        usuarioField.setLabel("Usuario");
        IntegerField equipoField = new IntegerField();
        equipoField.setLabel("Equipo");
        TextField fechaInicioField = new TextField();
        fechaInicioField.setLabel("Fecha Inicio");
        TextField fechaFinalField = new TextField();
        fechaFinalField.setLabel("Fecha Final");

        layoutPrestamo.add(idField, usuarioField, equipoField,
                fechaInicioField, fechaFinalField);

        //botones
        Button modificar = new Button("Modificar");
        Button eliminar = new Button("Eliminar");
        //AÑADIR

        idField.setEnabled(false);


        //MODIFICAR
        modificar.addClickListener(event -> {
            try{
                modificar(idField.getValue(),usuarioField.getValue(),equipoField.getValue(),fechaInicioField.getValue(),
                        fechaFinalField.getValue(), aux);

            }catch (Exception e){}
            idField.setEnabled(true);
            modificar.setEnabled(false);
            idField.clear();
            usuarioField.clear();
            equipoField.clear();
            fechaInicioField.clear();
            fechaFinalField.clear();
            grid.setItems(getAll());

        });


        modificar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);



        grid.addItemClickListener(
                event -> {
                    idField.setValue(event.getItem().getId());
                    usuarioField.setValue(event.getItem().getuId());
                    equipoField.setValue(event.getItem().geteId());
                    fechaInicioField.setValue(event.getItem().getfIni());
                    fechaFinalField.setValue(event.getItem().getfFin());
                    //add.setEnabled(false);
                    modificar.setEnabled(true);
                    idField.setEnabled(false);
                    aux = new Prestamo(event.getItem().getId(),event.getItem().getuId(),event.getItem().geteId(),
                            event.getItem().getfIni(),event.getItem().getfFin());

                });

        layoutPrestamo.add(modificar);

        grid.setItems(getAll());
        prestamoForm = layoutPrestamo;
        prestamoGrid = grid;


    }

    public ArrayList<Prestamo> getAll() {
        ArrayList<Prestamo> todos = new ArrayList<>();
        try {
            //LLAMADA A LA API
            URL url = new URL("http://localhost:8080/prestamos");
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
                todos = gson.fromJson(respuesta.toString(), new TypeToken<List<Prestamo>>() {
                }.getType());
                System.out.println(respuesta.toString());
            }

        } catch (Exception e) {

        }

        return todos;
    }

    public void modificar(int id, int idu, int ide, String fini, String ffin, Prestamo aux) {
        try {
            URL url = new URL("http://localhost:8080/modificarPrestamo");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            ArrayList<Prestamo> prestamos = new ArrayList<>();
            Prestamo nuevo = new Prestamo(id, idu, ide, fini, ffin);
            prestamos.add(aux);
            prestamos.add(nuevo);
            String jsonEquipos = gson.toJson(prestamos);

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


}