package sample; 

import javafx.application.Application;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.Stage;

public class Main extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override 
    public void start{Stage primaryStage} throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Multigrading-Tool");
        primaryStage.setScene(new Scene(root,500,500));//window with,hight
        primaryStage.show();
    }
    
}
