package com.example.demo5;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Pos;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javafx.scene.control.Alert.AlertType;

public class MainController {


    @FXML
    private RadioButton originalImageRadioButton;

    @FXML
    private RadioButton modifiedImageRadioButton;

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
        int width = 580;
        int height = 580;
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
        logToTextArea("Absolute random Image genereated");
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
    @FXML
    public void onRestoreOriginalImage(){
        if (originalImage != null) {
            imageView.setImage(originalImage);
            logToTextArea("Original Image restored.");
            originalImageRadioButton.setSelected(true);
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
            logToTextArea("No image to apply negative filter!");
            return;
        }

        // Get the image from the ImageView
        Image image = imageView.getImage();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        // Check for empty or invalid image dimensions
        if (width <= 0 || height <= 0) {
            logToTextArea("Invalid image dimensions!");
            return;
        }

        // Create a writable image to store the negative version
        WritableImage negativeImage = new WritableImage(width, height);
        PixelReader pixelReader = image.getPixelReader();
        PixelWriter pixelWriter = negativeImage.getPixelWriter();

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
        logToTextArea("Negative filter applied successfully.");
    }


    public void showAboutDialog() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Tento program vytvořili s láskou Martin Čechovič, Miroslav Baláte a Matěj Kupec :)");
        alert.showAndWait();
    }




    @FXML
    public void onExit() {
        System.exit(0);
    }

    @FXML
    private MenuItem menuPixelizer;

    /**
     * Spustí dialogové okno pro aplikaci filtru Pixelizer.
     */
    @FXML
    private void onPixelizerFilter() {
        if (imageView.getImage() == null) {
            logToTextArea("No image loaded to apply Pixelizer filter!");
            return;
        }

        // Původní obrázek
        Image originalImage = imageView.getImage();

        // Nastavení stage a layoutu
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Pixelizer Filter");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        // Vytvoření slideru
        Slider slider = new Slider(1, 50, 10);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);

        // Náhledový obrázek zmenšený na 50x50 pixelů
        ImageView previewImageView = new ImageView(originalImage);
        previewImageView.setFitWidth(400);
        previewImageView.setFitHeight(400);
        previewImageView.setPreserveRatio(true);

        // Aktualizace náhledu při posunu slideru
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int pixelSize = newValue.intValue();
            previewImageView.setImage(applyPixelizerFilter(originalImage, pixelSize));
        });

        // Tlačítka OK a Cancel
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);

        Button okButton = new Button("OK");
        okButton.setOnAction(e -> {
            imageView.setImage(previewImageView.getImage());
            modifiedImage = previewImageView.getImage();
            dialog.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> dialog.close());

        buttons.getChildren().addAll(okButton, cancelButton);

        // Přidání komponent do layoutu
        layout.getChildren().addAll(previewImageView, slider, buttons);

        Scene scene = new Scene(layout);
        dialog.setScene(scene);
        dialog.showAndWait();
        modifiedImageRadioButton.setSelected(true);
    }

    private Image applyPixelizerFilter(Image image, int pixelSize) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage pixelizedImage = new WritableImage(width, height);
        PixelReader pixelReader = image.getPixelReader();
        PixelWriter pixelWriter = pixelizedImage.getPixelWriter();

        for (int y = 0; y < height; y += pixelSize) {
            for (int x = 0; x < width; x += pixelSize) {
                // Průměrná barva pixelového bloku
                Color color = pixelReader.getColor(x, y);
                for (int dy = 0; dy < pixelSize; dy++) {
                    for (int dx = 0; dx < pixelSize; dx++) {
                        if (x + dx < width && y + dy < height) {
                            pixelWriter.setColor(x + dx, y + dy, color);
                        }
                    }
                }
            }
        }

        return pixelizedImage;
    }

    @FXML
    private void enableZoom() {
        // Check if an image is loaded
        if (imageView.getImage() == null) {
            logToTextArea("No image loaded to apply zoom!");
            return;
        }

        // Set initial scale
        imageView.setScaleX(1.0);
        imageView.setScaleY(1.0);

        // Enable scroll zooming
        imageView.setOnScroll(event -> {
            double zoomFactor = 1.1; // Zoom factor
            if (event.getDeltaY() > 0) {
                // Zoom in
                imageView.setScaleX(imageView.getScaleX() * zoomFactor);
                imageView.setScaleY(imageView.getScaleY() * zoomFactor);
            } else {
                // Zoom out
                imageView.setScaleX(imageView.getScaleX() / zoomFactor);
                imageView.setScaleY(imageView.getScaleY() / zoomFactor);
            }
            event.consume(); // Prevent event propagation
        });

        logToTextArea("Zoom enabled for the image.");
    }

    @FXML
    private void resetZoom() {
        if (imageView.getImage() == null) {
            logToTextArea("No image to reset zoom!");
            return;
        }

        imageView.setScaleX(1.0);
        imageView.setScaleY(1.0);

        logToTextArea("Zoom reset to original size.");
    }


    @FXML
    private void onCropImage() {
        // Kontrola, jestli je načtený obrázek
        if (imageView.getImage() == null) {
            logToTextArea("No image loaded!");
            return;
        }

        // Získání šířky
        int cropWidth = getDimensionFromUser("Enter crop width:", (int) imageView.getImage().getWidth());
        if (cropWidth <= 0) return;

        // Získání výšky
        int cropHeight = getDimensionFromUser("Enter crop height:", (int) imageView.getImage().getHeight());
        if (cropHeight <= 0) return;

        // Oříznutí obrázku
        cropImage(cropWidth, cropHeight);
    }

    private int getDimensionFromUser(String prompt, int max) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Crop Dimension");
        dialog.setHeaderText(prompt);
        dialog.setContentText("Max: " + max);

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int value = Integer.parseInt(result.get());
                if (value > 0 && value <= max) {
                    return value;
                } else {
                    logToTextArea("Invalid value. Please enter a number between 1 and " + max);
                }
            } catch (NumberFormatException e) {
                logToTextArea("Invalid input. Please enter an integer.");
            }
        }
        return -1;
    }

    private void cropImage(int cropWidth, int cropHeight) {
        Image originalImage = imageView.getImage();
        int startX = (int) (originalImage.getWidth() - cropWidth) / 2;
        int startY = (int) (originalImage.getHeight() - cropHeight) / 2;

        // Vytvoření nového obrázku s požadovanými rozměry
        WritableImage croppedImage = new WritableImage(cropWidth, cropHeight);
        for (int y = 0; y < cropHeight; y++) {
            for (int x = 0; x < cropWidth; x++) {
                Color color = originalImage.getPixelReader().getColor(startX + x, startY + y);
                croppedImage.getPixelWriter().setColor(x, y, color);
            }
        }

        // Nastavení nového obrázku do ImageView
        modifiedImage = croppedImage;
        imageView.setImage(modifiedImage);
        modifiedImageRadioButton.setSelected(true);
    }
}


