package com.example.demo5;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
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

    /**
     * Opens an image file and displays it in the ImageView.
     */
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

    /**
     * Saves the currently loaded image to a file.
     */
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

    /**
     * Applies a negative filter to the currently displayed image.
     */
    @FXML
    public void onNegativeImage() {
        if (imageView.getImage() == null) {
            System.out.println("No image to apply negative filter!");
            return;
        }

        Image image = imageView.getImage();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage negativeImage = new WritableImage(width, height);
        PixelReader pixelReader = image.getPixelReader();
        PixelWriter pixelWriter = negativeImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                Color negativeColor = new Color(
                        1.0 - color.getRed(),
                        1.0 - color.getGreen(),
                        1.0 - color.getBlue(),
                        color.getOpacity()
                );
                pixelWriter.setColor(x, y, negativeColor);
            }
        }

        imageView.setImage(negativeImage);
        System.out.println("Applied Negative Filter.");
    }

    /**
     * Placeholder method for pixelating an image (not yet implemented).
     */
    @FXML
    public void onPixelazer() {
        System.out.println("Pixelate functionality not yet implemented!");
    }

    /**
     * Exits the application.
     */
    @FXML
    public void onExit() {
        System.exit(0);
    }
}
