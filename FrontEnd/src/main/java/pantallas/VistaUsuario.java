package pantallas;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import objetos.Usuario;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class VistaUsuario {
    public Grid usuarioGrid;
    public FormLayout usuarioForm;
    Gson gson = new Gson();
    Usuario aux = null;

    public VistaUsuario() {

        //GRID
        Grid<Usuario> grid = new Grid<>();
        Grid.Column<Usuario> idColumn = grid.addColumn(Usuario::getId).setHeader("Id");
        Grid.Column<Usuario> nombreColumn = grid.addColumn(Usuario::getNombre).setHeader("Nombre");
        Grid.Column<Usuario> departamentoColumn = grid.addColumn(Usuario::getDepartamento).setHeader("Departamento");
        Grid.Column<Usuario> telefonoColumn = grid.addColumn(Usuario::getTelefono).setHeader("Teléfono");
        Grid.Column<Usuario> correoColumn = grid.addColumn(Usuario::getCorreo).setHeader("Correo");
        Grid.Column<Usuario> ubicacionColumn = grid.addColumn(Usuario::getUbicacion).setHeader("Ubicación");

        //EDITAR
        //one click
        grid.setSelectionMode(Grid.SelectionMode.NONE);


        FormLayout layoutUsuario = new FormLayout();

        IntegerField idField = new IntegerField();
        TextField nombreField = new TextField();
        TextField departamentoField = new TextField();
        TextField telefonoField = new TextField();
        TextField correoField = new TextField();
        TextField ubicacionField = new TextField();
        idField.setLabel("Id");
        nombreField.setLabel("Nombre");
        departamentoField.setLabel("Departamento");
        telefonoField.setLabel("Teléfono");
        correoField.setLabel("Correo");
        ubicacionField.setLabel("Ubicación");

        layoutUsuario.add(idField, nombreField, departamentoField,
                telefonoField, correoField, ubicacionField);
        //botones
        Button modificar = new Button("Modificar");
        Button add = new Button("Añadir");
        Button eliminar = new Button("Eliminar");
        //MODIFICAR

        idField.setEnabled(false);

        modificar.addClickListener(event -> {
            modificar(idField.getValue(), nombreField.getValue(), departamentoField.getValue()
                    , telefonoField.getValue(), correoField.getValue(), ubicacionField.getValue(), aux);
            idField.setEnabled(false);
            idField.clear();
            nombreField.clear();
            departamentoField.clear();
            telefonoField.clear();
            correoField.clear();
            ubicacionField.clear();
            grid.setItems(getAll());
            add.setEnabled(true);
            eliminar.setEnabled(false);
            modificar.setEnabled(false);
        });
        modificar.setEnabled(false);
        modificar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        //AÑADIR
        add.addClickListener(event -> {
            anadir(nombreField.getValue(), departamentoField.getValue()
                    , telefonoField.getValue(), correoField.getValue(), ubicacionField.getValue());
            idField.clear();
            nombreField.clear();
            departamentoField.clear();
            telefonoField.clear();
            correoField.clear();
            ubicacionField.clear();
            grid.setItems(getAll());
            eliminar.setEnabled(false);
            modificar.setEnabled(false);
        });

        add.setEnabled(true);

        //ELIMINAR
        eliminar.addClickListener(event -> {
                    eliminar(idField.getValue(), nombreField.getValue(), departamentoField.getValue()
                            , telefonoField.getValue(), correoField.getValue(), ubicacionField.getValue());
                    idField.setEnabled(false);
                    idField.clear();
                    nombreField.clear();
                    departamentoField.clear();
                    telefonoField.clear();
                    correoField.clear();
                    ubicacionField.clear();
                    grid.setItems(getAll());
                    add.setEnabled(true);
                    eliminar.setEnabled(false);
                    modificar.setEnabled(false);
                }
        );
        eliminar.setEnabled(false);
        eliminar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);


        //GRID LISTENES
        grid.addItemClickListener(
                event -> {
                    idField.setValue(event.getItem().getId());
                    nombreField.setValue(event.getItem().getNombre());
                    departamentoField.setValue(event.getItem().getDepartamento());
                    telefonoField.setValue(event.getItem().getTelefono());
                    correoField.setValue(event.getItem().getCorreo());
                    ubicacionField.setValue(event.getItem().getUbicacion());
                    idField.setEnabled(false);
                    add.setEnabled(false);
                    modificar.setEnabled(true);
                    eliminar.setEnabled(true);
                    aux = new Usuario(idField.getValue(), nombreField.getValue(), departamentoField.getValue(), telefonoField.getValue(), correoField.getValue(), ubicacionField.getValue());
                });


        layoutUsuario.add(modificar, eliminar, add);
        grid.setItems(getAll());
        usuarioForm = layoutUsuario;
        usuarioGrid = grid;

    }

    public ArrayList<Usuario> getAll() {
        ArrayList<Usuario> todos = new ArrayList<>();
        try {
            //LLAMADA A LA API
            URL url = new URL("http://localhost:8080/usuarios");
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
                todos = gson.fromJson(respuesta.toString(), new TypeToken<List<Usuario>>() {
                }.getType());
            }

        } catch (Exception e) {

        }

        return todos;
    }

    public void modificar(int id, String nombre, String departamento, String telefono, String correo, String ubicacion,
                          Usuario aux) {
        try {
            URL url = new URL("http://localhost:8080/modificarUsuario");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            ArrayList<Usuario> usuarios = new ArrayList<>();
            Usuario nuevo = new Usuario(id, nombre, departamento, telefono, correo, ubicacion);
            usuarios.add(aux);
            usuarios.add(nuevo);
            String jsonUsuarios = gson.toJson(usuarios);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonUsuarios.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response);
            }

        } catch (Exception e) {

        }
    }

    /*
    public void modificar(Usuario mod){
        try{
            URL url = new URL("http://localhost:8080/modificarUsuario");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            ArrayList<Usuario> list = new ArrayList<>();
            int id = mod.getId();
            for (Usuario user : list) {
                if(user.getId()==id){
                    user.modificarUsuario(mod);
                }
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            list.add(aux);
            list.add(mod);
            String jsonUsuarios = gson.toJson(mod);

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonUsuarios.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }

        }catch(Exception e){

        }
    }
    */


    public void eliminar(int id, String nombre, String departamento, String telefono, String correo, String ubicacion) {
        try {
            URL url = new URL("http://localhost:8080/eliminarUsuario");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            Usuario usuario = new Usuario(id, nombre, departamento, telefono, correo, ubicacion);
            String jsonUsuario = gson.toJson(usuario);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonUsuario.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response);
            }

        } catch (Exception e) {

        }

    }

    public void anadir(String nombre, String departamento, String telefono, String correo, String ubicacion) {
        try {
            URL url = new URL("http://localhost:8080/usuarios");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            Usuario usuario = new Usuario(1, nombre, departamento, telefono, correo, ubicacion);
            String jsonUsuario = gson.toJson(usuario);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonUsuario.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response);
            }

        } catch (Exception e) {

        }
    }
}
