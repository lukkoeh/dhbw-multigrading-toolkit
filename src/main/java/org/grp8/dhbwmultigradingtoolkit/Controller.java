package org.grp8.dhbwmultigradingtoolkit;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.awt.*;
import java.io.IOException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Controller implements Initializable {
    //Declaration of Button
    @FXML
    private Button previewMatrikel;
    @FXML
    private Button previewExam;
    @FXML
    private Button moodleUploadButton;
    @FXML
    private Label matrikelTabelleOutput;
    @FXML
    private Label notenTabelleOutput;

    @FXML
    private Button cancelButton;

    private File selectedMatrikelFile;
    private File selectedGradeFile;

    @FXML
    private TextField inputuser;
    @FXML
    private PasswordField inputpassword;
    @FXML
    private Button btnrelogin;

    private static final String[] creds = new String[2];

    private SheetManager s;
    private Stage mainpageStage;

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
        MatriculationIndex m = MatriculationIndex.getInstance(selectedMatrikelFile);
        ArrayList<Student> data = m.getStudents();
        ObservableList<Student> odatatmp = FXCollections.observableArrayList();
        odatatmp.addAll(data);
        PreviewMatriculationController.odata = odatatmp;
        // Load preview-matrikeltabelle.fxml and create a new stage
        Parent root = FXMLLoader.load(getClass().getResource("preview-matrikeltabelle.fxml"));
        previewMatrikelStage = new Stage();
        previewMatrikelStage.initModality(Modality.APPLICATION_MODAL);
        previewMatrikelStage.setScene(new Scene(root));

        // Show the preview window
        previewMatrikelStage.showAndWait();
    }

    //2. Preview of Notentabelle
    private Stage previewExamStage;

    @FXML
    private void showPreviewExam(ActionEvent event) throws IOException {
        ArrayList<ArrayList<String>> data = s.getData();
        ObservableList<PreviewGrade> odatatmp = FXCollections.observableArrayList();
        for (ArrayList<String> datum : data) {
            PreviewGrade p = new PreviewGrade(datum.get(0), datum.get(1), datum.get(2), datum.get(3), datum.get(4), datum.get(5));
            odatatmp.add(p);
        }
        PreviewController.odata = odatatmp;
        // Load preview-notentabelle.fxml and create a new stage
        Parent root = FXMLLoader.load(getClass().getResource("preview-notentabelle.fxml"));
        previewExamStage = new Stage();
        previewExamStage.initModality(Modality.APPLICATION_MODAL);
        previewExamStage.setScene(new Scene(root));
        previewExamStage.showAndWait();
        // Show the preview window
    }


    /**
     * Event handler for the file upload action of the Matrikeltabelle file.
     * Prompts the user to select a Matrikel file using a file chooser dialog,
     * updates the file output label, enables the Moodle upload button if ready,
     * and updates the visibility of the preview button based on whether a file is selected.
     */
    @FXML
    public void handleFileUploadMatrikel() {
        selectedMatrikelFile = chooseFile("Matrikeltabelle auswählen", "Files", "*.xlsx", "*.csv", "*.ods");
        if (selectedGradeFile == null) {
            moodleUploadButton.setDisable(true);
            updateFileOutputLabel(matrikelTabelleOutput, selectedMatrikelFile);
            updatePreviewButtonVisibility(previewMatrikel, selectedMatrikelFile != null);
            return;
        }
        s = new SheetManager(selectedGradeFile);
        if (selectedMatrikelFile != null) {
            if(s.mergeNeeded()){
                s.mergeData(selectedMatrikelFile);
                moodleUploadButton.setDisable(false);
                updateFileOutputLabel(matrikelTabelleOutput, selectedMatrikelFile);
                updatePreviewButtonVisibility(previewMatrikel, selectedMatrikelFile != null);
            }
        }
        else {
            if(s.mergeNeeded()){
                moodleUploadButton.setDisable(true);
                showFileError(matrikelTabelleOutput);
            }
        }
    }

    private void showFileError(Label label) {
        label.setText("Bitte wählen Sie eine Datei aus!");
        label.getStyleClass().add("error");
    }


    /**
     * Event handler for the file upload action of the Notentabelle file.
     * Prompts the user to select a grade file using a file chooser dialog,
     * updates the file output label, enables the Moodle upload button if ready,
     * and updates the visibility of the preview button based on whether a file is selected.
     */
    @FXML
    public void handleFileUploadGrade() {
        selectedGradeFile = chooseFile("Notentabelle auswählen", "Files", "*.xlsx", "*.csv", "*.ods");
        if (selectedGradeFile != null) {
            s = new SheetManager(selectedGradeFile);
            if(s.mergeNeeded()){
                s.mergeData(selectedMatrikelFile);
            }
            updateFileOutputLabel(notenTabelleOutput, selectedGradeFile);
            updatePreviewButtonVisibility(previewExam, selectedGradeFile != null);
            moodleUploadButton.setDisable(s.mergeNeeded() && selectedMatrikelFile == null);
        }
        else {
            moodleUploadButton.setDisable(true);
            notenTabelleOutput.setText("Bitte wählen Sie eine Datei aus!");
            notenTabelleOutput.getStyleClass().add("error");
        }
    }


    /**
     * Opens a file chooser dialog for the user to select a file.
     *
     * @param title       The title of the file chooser dialog.
     * @param description The description of the file types to be displayed in the dialog.
     * @param extensions  The file extensions allowed to be selected.
     * @return The selected file, or null if no file was chosen.
     */
    private File chooseFile(String title, String description, String... extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new ExtensionFilter(description, extensions));
        return fileChooser.showOpenDialog(null);
    }


    /**
     * Updates the label to display the name of the selected file, or a prompt to choose a file if no file is selected.
     * Also updates the style class of the label to indicate an error if no file is selected.
     *
     * @param label The label to be updated.
     * @param file  The selected file.
     */
    private void updateFileOutputLabel(Label label, File file) {
        if (file != null) {
            String fileName = file.getName();
            int maxLength = 25; // Set the maximum length of the displayed file name
            if (fileName.length() > maxLength) {
                String truncatedName = fileName.substring(0, maxLength - 3) + "...";
                label.setText(truncatedName);
            } else {
                label.setText(fileName);
            }
            label.getStyleClass().removeAll("error");
        }
    }


    /**
     * Sets the visibility of the preview button based on whether the file is uploaded or not.
     *
     * @param previewButton The preview button to be updated.
     * @param isVisible     Indicating whether the preview button should be visible.
     */
    private void updatePreviewButtonVisibility(Button previewButton, boolean isVisible) {
        previewButton.setVisible(isVisible);
    }


    /**
     * Enables or disables the moodleUploadButton based on the availability of both the selectedMatrikelFile and selectedGradeFile.
     * The moodleUploadButton is disabled if either of the files is null.
     */


    @FXML
    private void showMainPage(ActionEvent event) throws IOException {
        creds[0] = inputuser.getText();
        creds[1] = inputpassword.getText();
        if (Objects.equals(creds[0], "") || Objects.equals(creds[1], "")) {
            /*Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Bitte geben Sie Zugangsdaten ein.");
            a.showAndWait();
            return;*/
        }
        this.closeLoginPage(event);
        if (mainpageStage == null) {
            // connection between MainPage and LoginPage
            Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
            mainpageStage = new Stage();
            mainpageStage.initModality(Modality.APPLICATION_MODAL);
            mainpageStage.setScene(new Scene(root));
            mainpageStage.showAndWait();
        }

        // Show the preview window
    }
    @FXML
    public void openLoginPage() throws IOException {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        Parent root = FXMLLoader.load(getClass().getResource("relogin.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    private void updateLoginData(ActionEvent event) throws IOException {
        creds[0] = inputuser.getText();
        creds[1] = inputpassword.getText();
        if (Objects.equals(creds[0], "") || Objects.equals(creds[1], "")) {
            /*Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Bitte geben Sie Zugangsdaten ein.");
            a.showAndWait();
            return;*/
        }
        Stage stage = (Stage) btnrelogin.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void closeLoginPage(ActionEvent event) throws IOException {

        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void startProcess() {
        Bot b = new Bot(creds, s);
        b.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}