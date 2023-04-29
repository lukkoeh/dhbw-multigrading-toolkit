package org.grp8.dhbwmultigradingtoolkit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Controller {
    //Declaration of Button

    @FXML
    private Button instruction;
    @FXML
    private Button previewMatrikel;
    @FXML
    private Button previewExam;
    @FXML
    private Button exit;
    @FXML
    private Button infoMatrikel;
    @FXML
    private Button infoExam;

    //linking Instruction-Icon to browser-pdf-document view
    @FXML
    private void openPdf(ActionEvent event) throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://acrobat.adobe.com/link/review?uri=urn:aaid:scds:US:f8ff6011-19f7-45aa-bdbe-ba5d5b228860"));
    }

    //linking eye-button to previews

    //linking x-buttons on Previews to Home-Page
}