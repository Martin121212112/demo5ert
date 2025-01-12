package com.example.demo5;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainController {
    @FXML
    private ToggleGroup imageToggleGroup;


    @FXML
    private MenuItem menuOpen;

    @FXML
    private MenuItem menuSave;

    @FXML
    private MenuItem menuExit;

    @FXML
    private ImageView imageView;

    private BufferedImage img;

    @FXML
    public void onOpenImage() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                img = ImageIO.read(file);
                imageView.setImage(new Image(file.toURI().toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void onSaveImage() {
        if (img == null) {
            System.out.println("No image to save!");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                ImageIO.write(img, "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    public void onNegative() {
        if (img == null) {
            System.out.println("No image to save!");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                ImageIO.write(img, "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    public void onPixelazer() {
        if (img == null) {
            System.out.println("No image to save!");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                ImageIO.write(img, "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void onExit() {
        System.exit(0);
    }
}
