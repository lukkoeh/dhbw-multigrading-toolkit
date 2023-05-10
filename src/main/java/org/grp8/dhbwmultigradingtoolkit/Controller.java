package org.grp8.dhbwmultigradingtoolkit;

import com.sun.javafx.scene.control.InputField;
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
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.w3c.dom.Text;


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
    private TableView<String> tablepreviewmatrikel;

    private static String[] creds = new String[2];


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
            MatriculationIndex m = new MatriculationIndex(selectedMatrikelFile);
            ArrayList<Student> data = m.getStudents();
            ObservableList<Student> odatatmp = FXCollections.observableArrayList();
            odatatmp.addAll(data);
            PreviewMatriculationController.odata = odatatmp;
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
            SheetManager s = new SheetManager(selectedGradeFile);
            ArrayList<ArrayList<String>> data = s.getData();
            ObservableList<PreviewGrade> odatatmp = FXCollections.observableArrayList();
            for (int i = 0; i < data.size(); i++) {
                PreviewGrade p = new PreviewGrade(data.get(i).get(0), data.get(i).get(1), data.get(i).get(2), data.get(i).get(3), data.get(i).get(4), data.get(i).get(5));
                odatatmp.add(p);
            }
            PreviewController.odata = odatatmp;
            // Load preview-notentabelle.fxml and create a new stage
            Parent root = FXMLLoader.load(getClass().getResource("preview-notentabelle.fxml"));
            previewExamStage = new Stage();
            previewExamStage.initModality(Modality.APPLICATION_MODAL);
            previewExamStage.setScene(new Scene(root));
            previewExamStage.showAndWait();
        }
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
        updateFileOutputLabel(matrikelTabelleOutput, selectedMatrikelFile);
        enableMoodleUploadButtonIfReady();
        updatePreviewButtonVisibility(previewMatrikel, selectedMatrikelFile != null);
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
        updateFileOutputLabel(notenTabelleOutput, selectedGradeFile);
        enableMoodleUploadButtonIfReady();
        updatePreviewButtonVisibility(previewExam, selectedGradeFile != null);
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
        } else {
            label.setText("Bitte wählen Sie eine Datei aus !");
            label.getStyleClass().add("error");
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
    private void enableMoodleUploadButtonIfReady() {
        moodleUploadButton.setDisable(selectedGradeFile == null);
    }

    private Stage mainpageStage;

    @FXML
    private void showMainPage(ActionEvent event) throws IOException {
        creds[0] = inputuser.getText();
        creds[1] = inputpassword.getText();
        if (mainpageStage == null) {
            // connection between MainPage and LoginPage
            Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
            mainpageStage = new Stage();
            mainpageStage.initModality(Modality.APPLICATION_MODAL);
            mainpageStage.setScene(new Scene(root));
        }

        // Show the preview window
        mainpageStage.showAndWait();
    }

    @FXML
    private void closeLoginPage(ActionEvent event) throws IOException {

        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void startProcess() {
        SheetManager s = new SheetManager(selectedGradeFile, selectedMatrikelFile);
        Bot b = new Bot(creds, s);
        b.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}