package org.grp8.dhbwmultigradingtoolkit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Controller {
    //Declaration of Button
    @FXML
    private Button instruction;
    @FXML
    private Button previewMatrikel;
    @FXML
    private Button previewExam;




    //linking Instruction-Icon to browser-pdf-document view
    @FXML
    private void openPdf(ActionEvent event) throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://acrobat.adobe.com/link/review?uri=urn:aaid:scds:US:f8ff6011-19f7-45aa-bdbe-ba5d5b228860"));
    }




    //linking eye-button to previews

    //1. Preview of Matrikeltabelle
    private Stage previewMatrikelStage;
    @FXML
    private void showPreviewMatrikel(ActionEvent event) throws IOException {
        if (previewMatrikelStage == null) {
            // Load preview-matrikeltabelle.fxml and create a new stage
            Parent root = FXMLLoader.load(getClass().getResource("preview-matrikeltabelle.fxml"));
            previewMatrikelStage = new Stage();
            previewMatrikelStage.initModality(Modality.APPLICATION_MODAL);
            previewMatrikelStage.setScene(new Scene(root));
        }

        // Show the preview window
        previewMatrikelStage.showAndWait();
    }

    //2. Preview of Notentabelle
    private Stage previewExamStage;
    @FXML
    private void showPreviewExam(ActionEvent event) throws IOException {
        if (previewExamStage == null) {
            // Load preview-notentabelle.fxml and create a new stage
            Parent root = FXMLLoader.load(getClass().getResource("preview-notentabelle.fxml"));
            previewExamStage = new Stage();
            previewExamStage.initModality(Modality.APPLICATION_MODAL);
            previewExamStage.setScene(new Scene(root));
        }

        // Show the preview window
        previewExamStage.showAndWait();
    }
}