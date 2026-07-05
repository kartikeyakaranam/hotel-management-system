package com.hotel.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private Label lblDateTime;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateClock();
    }

    private void updateClock() {
        String now = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd MMM yyyy  HH:mm"));
        lblDateTime.setText(now);
    }
}
