package com.hotel;

import com.hotel.db.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize database tables on startup
        DatabaseConnection.initializeDatabase();

        Parent root = FXMLLoader.load(
            Objects.requireNonNull(getClass().getResource("/com/hotel/fxml/MainView.fxml"))
        );

        Scene scene = new Scene(root, 1100, 700);
        scene.getStylesheets().add(
            Objects.requireNonNull(getClass().getResource("/com/hotel/css/dark-theme.css")).toExternalForm()
        );

        primaryStage.setTitle("🏨 Hotel Management System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    @Override
    public void stop() {
        DatabaseConnection.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
