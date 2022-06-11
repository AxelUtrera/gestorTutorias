/*
 *Autor: Daniel Eduardo Anota Paxtian
 *Fecha de creacion: 10/06/2022
 *Ultima modificacion: 10/06/2022
 *Nombre modificador: Daniel Eduardo Anota Paxtian
 */

package tutSys.vistas;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class FXMLMenuTutSysControlador implements Initializable {
    @FXML
    public Label labelNombreUsuario;
    @FXML
    public Label labelFecha;
    public String nombreUsuario;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colocarFechaActual();
    }

    @FXML
    public void invocarLlenarReporteTutoria(ActionEvent actionEvent) {
        try{
            Stage escenarioPrincipal = (Stage) labelNombreUsuario.getScene().getWindow();
            Scene llenarReporteTutoria = new Scene(FXMLLoader.load(getClass().getResource("RegistroReporteTutoriaVista.fxml")));
            escenarioPrincipal.setScene(llenarReporteTutoria);
            escenarioPrincipal.setTitle("TutSys - Llenar Reporte de Tutoría Académica");
            escenarioPrincipal.show();
        } catch (IOException excepcion){
            excepcion.printStackTrace();
        }
    }

    @FXML
    public void invocarRegistroProblematicaAcademica(ActionEvent actionEvent) {
        try{
            Stage escenarioPrincipal = (Stage) labelNombreUsuario.getScene().getWindow();
            Scene registroProblematicaAcademica = new Scene(FXMLLoader.load(getClass().getResource("RegistroProblematicaAcademicaVista.fxml")));
            escenarioPrincipal.setScene(registroProblematicaAcademica);
            escenarioPrincipal.setTitle("TutSys - Registrar Problematica Académica");
            escenarioPrincipal.show();
        } catch (IOException excepcion){
            excepcion.printStackTrace();
        }
    }

    @FXML
    public void invocarConsultarProblematicasAcademicas(ActionEvent actionEvent) {
        try{
            Stage escenarioPrincipal = (Stage) labelNombreUsuario.getScene().getWindow();
            Scene consultaProblematicaAcademica = new Scene(FXMLLoader.load(getClass().getResource("ProblematicaAcademicaVista.fxml")));
            escenarioPrincipal.setScene(consultaProblematicaAcademica);
            escenarioPrincipal.setTitle("TutSys - Consultar Problematica Académica");
            escenarioPrincipal.show();
        } catch (IOException excepcion){
            excepcion.printStackTrace();
        }
    }

    private void colocarFechaActual(){
        Date fechaActual = new Date();
        labelFecha.setText(new SimpleDateFormat("dd/MM/yyyy").format(fechaActual));
    }

}
