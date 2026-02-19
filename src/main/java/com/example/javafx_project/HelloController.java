package com.example.javafx_project;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class HelloController {

    @FXML
    void printWelcome(MouseEvent event) {
        System.out.println("Welcome to Planora.");
    }

}