package org.grp8.dhbwmultigradingtoolkit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Controller {
    //all button functions

    @FXML
    private Button instruction;
    private Button previewMatrikel;
    private Button previewExam;
    private Button exit;
    private Button infoMatrikel;
    private Button infoExam;

    //linking "Anleitung"-Button t0 browser-pdf-document view
    @FXML
    private void openPdf(ActionEvent event) throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://acrobat.adobe.com/link/review?uri=urn:aaid:scds:US:f8ff6011-19f7-45aa-bdbe-ba5d5b228860"));
    }

    //linking eye-button to previews

    //linking x-buttons on Previews to Home-Page
}