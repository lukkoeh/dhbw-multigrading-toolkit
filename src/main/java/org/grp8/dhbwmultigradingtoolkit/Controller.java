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
import javafx.stage.StageStyle;

/**
 * The Controller class contains methods to interact with the Main UI.
 */

public class Controller implements Initializable {
    //Declaration of Button
    @FXML
    private Button previewMatrikel;
    @FXML
    private Button previewExam;
    @FXML
    public Button moodleUploadButton;
    @FXML
    private Label matrikelTabelleOutput;
    @FXML
    private Label notenTabelleOutput;

    @FXML
    private Button cancelButton;

    private File selectedMatrikelFile;
    public static File selectedGradeFile;

    @FXML
    private TextField inputuser;
    @FXML
    private PasswordField inputpassword;
    @FXML
    private Button btnrelogin;

    private static final String[] creds = new String[2];

    private SheetManager s;
    private Stage mainpageStage;

    /**
     * This function uses the default PDF-Viewer to open the manual.
     */
    @FXML
    private void openPdf()  {
        if(Desktop.isDesktopSupported()){
            new Thread(() -> {
                try{
                    String currentDir = new File("").getAbsolutePath();
                    String finalPath = currentDir + "/Anleitung.pdf";
                    File f = new File(finalPath);
                    Desktop.getDesktop().open(f);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }).start();
        }
    }

    private Stage previewMatrikelStage;

    /**
     * A function to show the Preview table of the Matriculation-File.
     * @param event Click Event
     * @throws IOException If FXML Loading fails.
     */
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

    /**
     * A function to show a preview of the grading table, with merged data!
     * @param event Click Event
     * @throws IOException If FXML Load error
     */
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
    private void handleFileUploadMatrikel() {
        selectedMatrikelFile = chooseFile("Matrikeltabelle auswählen", "Excel Files", "*.xlsx");
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

    public void showFileError(Label label) {
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
            if(selectedMatrikelFile != null){
                if(s.mergeNeeded()){
                    s.mergeData(selectedMatrikelFile);
                }
            }

            if (selectedGradeFile != null){ // Falls CSV-Encoding nicht stimmt, wird file = null gesetzt (sheetManager)
                updateFileOutputLabel(notenTabelleOutput, selectedGradeFile);
                updatePreviewButtonVisibility(previewExam, selectedGradeFile != null);
                moodleUploadButton.setDisable(s.mergeNeeded() && selectedMatrikelFile == null);
            }else{
                showFileError(notenTabelleOutput);
            }
        }
        else {
            moodleUploadButton.setDisable(true);
            notenTabelleOutput.setText("Bitte wählen Sie eine Datei aus!");
            notenTabelleOutput.getStyleClass().add("error");
        }
    }

    public void disableUploadButton(){
        this.moodleUploadButton.setDisable(true);
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
    public void updateFileOutputLabel(Label label, File file) {
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
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Bitte geben Sie Zugangsdaten ein.");
            a.showAndWait();
            return;
        }
        this.closeLoginPage(event);
        if (mainpageStage == null) {
            // connection between MainPage and LoginPage
            Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
            mainpageStage = new Stage();
            mainpageStage.initModality(Modality.WINDOW_MODAL);
            mainpageStage.setScene(new Scene(root));
            mainpageStage.setResizable(false);
            mainpageStage.initStyle(StageStyle.DECORATED);
            mainpageStage.showAndWait();
        }

        // Show the preview window
    }

    /**
     * A function to show the login Page in case the login fails.
     * @throws IOException FXML Load
     */
    @FXML
    public void openLoginPage() throws IOException {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        Parent root = FXMLLoader.load(getClass().getResource("relogin.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * A function to update the static credentials value to be used by the Bot class.
     * @param event Click Event
     */
    @FXML
    private void updateLoginData(ActionEvent event) {
        creds[0] = inputuser.getText();
        creds[1] = inputpassword.getText();
        if (Objects.equals(creds[0], "") || Objects.equals(creds[1], "")) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Bitte geben Sie Zugangsdaten ein.");
            a.showAndWait();
            return;
        }
        Stage stage = (Stage) btnrelogin.getScene().getWindow();
        stage.close();
    }

    /**
     * Closes the login window for the first time.
     * @param event Click Event
     */
    @FXML
    private void closeLoginPage(ActionEvent event) {

        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Neccessary for initializable
     * @param url
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resourceBundle
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    /**
     * A function to show a dialog if the moodle upload fails.
     */
    private void showErrorUploadWarning() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("dialog-window-error.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A function to show a dialog if the moodle upload is successfully completed.
     */
    private void showSuccessUploadAlert() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("dialog-window-success.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * A function to handle the Button for the moodle upload. Handles errors, opens the Bot, starts it and stops it.
     * @param event Click Event
     */
    @FXML
    private void handleMoodleUploadButton(ActionEvent event) {
        Bot b = new Bot(creds, s);
        boolean uploadSuccess = b.start();

        // Perform upload process and set uploadSuccess accordingly

        if (uploadSuccess) {
            showSuccessUploadAlert();
        } else {
            showErrorUploadWarning();
        }
        b.stop();
    }


}