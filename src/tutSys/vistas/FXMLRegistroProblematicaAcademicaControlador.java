package tutSys.vistas;

/**
 *Autor: Axel Utrera
 *Fecha de creacion: 09/06/2022
 *Ultima modificacion: 13/06/2022
 *Nombre modificador: Daniel Eduardo Anota Paxtian
 */

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tutSys.modelo.dao.ExperienciaEducativaDAO;
import tutSys.modelo.dao.ProblematicaAcademicaDAO;
import tutSys.modelo.dao.ProfesorDAO;
import tutSys.modelo.dao.ReporteTutoriaAcademicaDAO;
import tutSys.modelo.pojo.*;
import tutSys.utilidades.CuadroDialogo;

import javax.swing.text.html.Option;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FXMLRegistroProblematicaAcademicaControlador implements Initializable {

    public static TutorAcademico tutorAcademico;
    public static ReporteTutoriaAcademica reporteExtendido;

    @FXML
    private ComboBox<String> comboBoxExperienciaEducativa;

    @FXML
    private ComboBox<String> comboBoxProfesor;

    @FXML
    private ComboBox<String> comboBoxTutorias;

    @FXML
    private Label labelFechaHoy;

    @FXML
    private TextArea textAreaDescripcion;

    @FXML
    private TextField textFieldNumeroReportes;

    @FXML
    private Label labelErrorTutoria;

    @FXML
    private Label labelErrorDescripcion;

    @FXML
    private Label labelErrorNumeroReportes;

    @FXML
    private Label labelErrorExperienciaEducativa;

    @FXML
    private Label labelErrorProfesor;


    @FXML
    void clicCancelar(ActionEvent event) {
        Optional<ButtonType> confirmarRegistro = CuadroDialogo.crearCuadroDialogoConfirmacion("Cancelar", "¿Desea cancelar el registro?");
        if (confirmarRegistro.get() == ButtonType.OK) {
            cerrarVentana();
        }
    }

    @FXML
    void clicRegistrar(ActionEvent event) {

        if(validarCampos()){
            //Recuperacion de profesor para obtener su id y asi recuperar el id de la Experiencia Educativa relacionada.
            Profesor profesorRecuperado = ProfesorDAO.recuperarProfesor(comboBoxProfesor.getValue());
            ExperienciaEducativa experienciaEducativaRecuperada = ExperienciaEducativaDAO.recuperarExperienciaEducativa(
                    profesorRecuperado.getIdProfesor(), comboBoxExperienciaEducativa.getValue());

            ProblematicaAcademica problematicaAcademica = new ProblematicaAcademica();
            problematicaAcademica.setNumeroResportes(Integer.valueOf(textFieldNumeroReportes.getText()));
            problematicaAcademica.setDescripcion(textAreaDescripcion.getText());
            problematicaAcademica.setIdReporteTutoria(obtenerIdReporteTutoria(comboBoxTutorias.getValue()));
            problematicaAcademica.setIdExperienciaEducativa(experienciaEducativaRecuperada.getIdExperienciaEducativa());
            Optional<ButtonType> confirmarRegistro = CuadroDialogo.crearCuadroDialogoConfirmacion("Confirmar", "¿Desea registrar problematica academica?");
            if (confirmarRegistro.get() == ButtonType.OK) {
                ProblematicaAcademicaDAO.agregarProblematicaAcademica(problematicaAcademica);
                CuadroDialogo.crearCuadroDialogoInformacion("Guardado exitoso!", "¡Problematica academica registrada con éxito!");
                cerrarVentana();
            }


        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        llenarComboboxSesionTutorias();
        llenarComboboxExperienciasEducativas();
        obsevarCambiosComboboxExperienciaEducativa();
        colocarFechaActual();
    }

    public void llenarComboboxSesionTutorias() {
        ObservableList<String> sesionesTutoria = FXCollections.observableArrayList();
        if(reporteExtendido != null){
            sesionesTutoria.add("ID: "+String.valueOf(reporteExtendido.getIdReporteTutoriaAcademica()) +
                    " Sesion "+String.valueOf(reporteExtendido.getNumeroSesionTutoria())
                    + "  " + reporteExtendido.getIdPeriodoEscolar());
            comboBoxTutorias.setItems(sesionesTutoria);
            comboBoxTutorias.getSelectionModel().select(0);

        } else if (tutorAcademico != null){
            ArrayList<ReporteTutoriaAcademica> sesionesRecuperadas = ReporteTutoriaAcademicaDAO.consultarReportesTutoriaPorTutor(tutorAcademico);

            for(ReporteTutoriaAcademica sesion : sesionesRecuperadas){
                sesionesTutoria.add("ID: "+String.valueOf(sesion.getIdReporteTutoriaAcademica()) +
                        " Sesion "+String.valueOf(sesion.getNumeroSesionTutoria())
                        + "  " + sesion.getIdPeriodoEscolar());
            }
            comboBoxTutorias.setItems(sesionesTutoria);
        }

    }

    public void llenarComboboxExperienciasEducativas() {
        ObservableList<String> experienciasEducativas = FXCollections.observableArrayList();
        ArrayList<ExperienciaEducativa> experienciasRecuperadas = ExperienciaEducativaDAO.recuperarTodasExperienciasEducativas();
        for (ExperienciaEducativa ee : experienciasRecuperadas) {
            experienciasEducativas.add(ee.getNombre());
        }
        comboBoxExperienciaEducativa.setItems(limpiarExperienciasEducativasRepetidas(experienciasEducativas));
    }

    public ObservableList<String> limpiarExperienciasEducativasRepetidas(ObservableList<String> experienciasEducativas) {
        ObservableList<String> listaSinRepetidos = FXCollections.observableArrayList();

        for (String e : experienciasEducativas) {
            if (!listaSinRepetidos.contains(e)) {
                listaSinRepetidos.add(e);
            }
        }
        return listaSinRepetidos;
    }

    public void obsevarCambiosComboboxExperienciaEducativa() {
        comboBoxExperienciaEducativa.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                llenarComboboxProfesor(comboBoxExperienciaEducativa.getValue());
            }
        });
    }

    public void llenarComboboxProfesor(String experienciaEducatia) {
        ObservableList<String> profesores = FXCollections.observableArrayList();
        ArrayList<Profesor> profesoresRecuperados = ProfesorDAO.recuperarProfesoresPorExperienciaEducativa(experienciaEducatia);

        for (Profesor profesor : profesoresRecuperados) {
            profesores.add(profesor.getNombre());
        }
        comboBoxProfesor.setItems(profesores);
    }

    public Label colocarFechaActual() {
        Date fechaHoy = new Date();
        labelFechaHoy.setText("Fecha de hoy: " + new SimpleDateFormat("dd/MM/yyyy").format(fechaHoy));
        return labelFechaHoy;
    }

    public int obtenerIdReporteTutoria(String cadenaTexto){
        String subcadena = cadenaTexto.substring(4,6);
        int idReporte = Integer.valueOf(subcadena.replaceAll("\\s+",""));
        return idReporte;
    }

    public boolean validarCampos() {

        boolean esValido = true;

        labelErrorTutoria.setVisible(false);
        labelErrorDescripcion.setVisible(false);
        labelErrorNumeroReportes.setVisible(false);
        labelErrorExperienciaEducativa.setVisible(false);
        labelErrorProfesor.setVisible(false);

        String sesionTutoria = comboBoxTutorias.getValue();
        String textoDescripcion = textAreaDescripcion.getText();
        String textoNumeroReportes = textFieldNumeroReportes.getText();
        String experienciaEducativaSeleccionada = comboBoxExperienciaEducativa.getValue();
        String nombreProfesorSeleccionado = comboBoxProfesor.getValue();

        Pattern expresionRegular = Pattern.compile("^[0-9]*$");
        Matcher validador = expresionRegular.matcher(textFieldNumeroReportes.getText());
        if (!(validador.matches()) || textoNumeroReportes.isEmpty()) {
            labelErrorNumeroReportes.setVisible(true);
            esValido = false;
        }

        if (sesionTutoria == null) {
            labelErrorTutoria.setVisible(true);
            esValido = false;
        }
        if (textoDescripcion.isEmpty()) {
            labelErrorDescripcion.setVisible(true);
            esValido = false;
        }

        if (experienciaEducativaSeleccionada == null) {
            labelErrorExperienciaEducativa.setVisible(true);
            esValido = false;
        }

        if (nombreProfesorSeleccionado == null) {
            labelErrorProfesor.setVisible(true);
            esValido = false;
        }

        return esValido;
    }

    private void cerrarVentana(){
        reporteExtendido = null;
        Stage escenario = (Stage) comboBoxProfesor.getScene().getWindow();
        escenario.close();
    }


}

