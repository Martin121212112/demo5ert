package com.example.demo5;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.*;
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
    private RadioButton originalImageRadioButton;

    @FXML
    private RadioButton modifiedImageRadioButton;

    @FXML
    private MenuItem menuOpen;

    @FXML
    private MenuItem menuSave;

    @FXML
    private MenuItem menuExit;

    @FXML
    private ImageView imageView;

    @FXML
    private TextArea outputTextArea;

    private Image originalImage; // Stores the original image
    private Image modifiedImage; // Stores the modified image

    /**
     * Logs messages to the text area, ensuring each message appears on a new line.
     *
     * @param message The message to log.
     */
    private void logToTextArea(String message) {
        String currentText = outputTextArea.getText();
        outputTextArea.setText((currentText == null ? "" : currentText + "\n") + message);
    }

    /**
     * Opens an image file and displays it in the ImageView, storing it as the original image.
     */
    @FXML
    public void onOpenImage() {
        FileChooser fileChooser = new FileChooser();

        // Add filters to show only image files
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                BufferedImage loadedImg = ImageIO.read(file);
                if (loadedImg != null) {
                    originalImage = new Image(file.toURI().toString());
                    modifiedImage = null; // Reset the modified image
                    imageView.setImage(originalImage);
                    originalImageRadioButton.setSelected(true);
                    logToTextArea("Image loaded: " + file.getAbsolutePath());
                } else {
                    logToTextArea("Selected file is not a valid image.");
                }
            } catch (IOException e) {
                logToTextArea("Error loading image: " + e.getMessage());
            }
        } else {
            logToTextArea("No file selected.");
        }
    }

    /**
     * Saves the currently displayed image to a file.
     */
    @FXML
    public void onSaveImage() {
        if (imageView.getImage() == null) {
            logToTextArea("No image to save!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("image.png");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"));

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            if (!file.getName().toLowerCase().endsWith(".png")) {
                file = new File(file.getAbsolutePath() + ".png");
            }

            try {
                WritableImage writableImage = (WritableImage) imageView.getImage();
                BufferedImage bufferedImage = convertToBufferedImage(writableImage);
                ImageIO.write(bufferedImage, "png", file);
                logToTextArea("Image saved to: " + file.getAbsolutePath());
            } catch (IOException e) {
                logToTextArea("Error saving image: " + e.getMessage());
            }
        }
    }

    /**
     * Converts a WritableImage to BufferedImage.
     */
    private BufferedImage convertToBufferedImage(WritableImage writableImage) {
        int width = (int) writableImage.getWidth();
        int height = (int) writableImage.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        PixelReader pixelReader = writableImage.getPixelReader();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color fxColor = pixelReader.getColor(x, y);
                int argb = ((int) (fxColor.getOpacity() * 255) << 24) |
                        ((int) (fxColor.getRed() * 255) << 16) |
                        ((int) (fxColor.getGreen() * 255) << 8) |
                        ((int) (fxColor.getBlue() * 255));
                bufferedImage.setRGB(x, y, argb);
            }
        }

        return bufferedImage;
    }

    /**
     * Applies a black-and-white filter to the original image.
     */
    @FXML
    public void onBlackAndWhiteImage() {
        if (originalImage == null) {
            logToTextArea("No image to apply black and white filter!");
            return;
        }

        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();
        WritableImage blackAndWhiteImage = new WritableImage(width, height);
        PixelReader pixelReader = originalImage.getPixelReader();
        PixelWriter pixelWriter = blackAndWhiteImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                double gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                Color grayColor = new Color(gray, gray, gray, color.getOpacity());
                pixelWriter.setColor(x, y, grayColor);
            }
        }

        modifiedImage = blackAndWhiteImage;
        imageView.setImage(modifiedImage);
        modifiedImageRadioButton.setSelected(true);
        logToTextArea("Applied Black and White Filter.");
    }

    /**
     * Generates a random chessboard pattern with random colors and cell size.
     */
    @FXML
    public void onGenerateRandomImage() {
        int width = 400;
        int height = 400;
        int minCellSize = 20;
        int maxCellSize = 100;
        int cellSize = (int) (Math.random() * (maxCellSize - minCellSize + 1) + minCellSize);

        Color color1 = new Color(Math.random(), Math.random(), Math.random(), 1.0);
        Color color2 = new Color(Math.random(), Math.random(), Math.random(), 1.0);

        WritableImage chessboardImage = new WritableImage(width, height);
        PixelWriter pixelWriter = chessboardImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int cellX = x / cellSize;
                int cellY = y / cellSize;
                boolean isColor1 = (cellX + cellY) % 2 == 0;
                Color color = isColor1 ? color1 : color2;
                pixelWriter.setColor(x, y, color);
            }
        }

        modifiedImage = chessboardImage;
        imageView.setImage(modifiedImage);
        modifiedImageRadioButton.setSelected(true);
        logToTextArea("Generated chessboard with random colors and cell size: " + cellSize);
    }

    /**
     * Handles switching to the original image.
     */
    @FXML
    public void onOriginalImageSelected() {
        if (originalImage != null) {
            imageView.setImage(originalImage);
            logToTextArea("Switched to Original Image.");
        } else {
            logToTextArea("No original image available.");
        }
    }

    /**
     * Handles switching to the modified image.
     */
    @FXML
    public void onModifiedImageSelected() {
        if (modifiedImage != null) {
            imageView.setImage(modifiedImage);
            logToTextArea("Switched to Modified Image.");
        } else {
            logToTextArea("No modified image available.");
        }
    }
    @FXML
    public void onNegativeImage() {
        // Check if there's no image loaded
        if (imageView.getImage() == null) {
            System.out.println("No image to apply negative filter!");
            return;
        }

        // Get the image from the ImageView
        Image image = imageView.getImage();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        // Check for empty or invalid image dimensions
        if (width <= 0 || height <= 0) {
            System.out.println("Invalid image dimensions!");
            return;
        }

        // Create a writable image to store the negative version
        WritableImage negativeImage = new WritableImage(width, height);
        PixelReader pixelReader = image.getPixelReader();
        PixelWriter pixelWriter = negativeImage.getPixelWriter();

        // Log the start of the processing
        System.out.println("Applying negative filter to the image...");

        // Process each pixel in the image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Read the color of the current pixel
                Color color = pixelReader.getColor(x, y);

                // Calculate the negative color
                Color negativeColor = Color.color(
                        1.0 - color.getRed(),
                        1.0 - color.getGreen(),
                        1.0 - color.getBlue(),
                        color.getOpacity() // Preserve the original opacity
                );

                // Write the negative color to the writable image
                pixelWriter.setColor(x, y, negativeColor);
            }
        }
        modifiedImage = negativeImage;
        imageView.setImage(modifiedImage);
        modifiedImageRadioButton.setSelected(true);
        // Set the resulting negative image back to the ImageView


        // Log the completion of the processing
        System.out.println("Negative filter applied successfully.");
    }

    /**
     * Exits the application.
     */
    @FXML
    public void onExit() {
        System.exit(0);
    }
}
