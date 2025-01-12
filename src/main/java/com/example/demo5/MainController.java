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
    public void onBlackAndWhiteImage() {
        if (imageView.getImage() == null) {
            System.out.println("No image to apply black and white filter!");
            return;
        }

        Image image = imageView.getImage();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage blackAndWhiteImage = new WritableImage(width, height);
        PixelReader pixelReader = image.getPixelReader();
        PixelWriter pixelWriter = blackAndWhiteImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);

                // Vypočítání průměru RGB hodnot pro černobílý efekt
                double gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;

                // Nastavení pixelu na novou barvu (odstín šedi)
                Color grayColor = new Color(gray, gray, gray, color.getOpacity());
                pixelWriter.setColor(x, y, grayColor);
            }
        }

        // Nastavení černobílého obrázku do ImageView
        imageView.setImage(blackAndWhiteImage);
        System.out.println("Applied Black and White Filter.");
    }

    @FXML
    public void onOpenImage() {
        FileChooser fileChooser = new FileChooser();

        // Přidání filtrů souborů, aby uživatel viděl pouze obrázky
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                // Ověření, že soubor lze načíst jako obrázek
                BufferedImage loadedImg = ImageIO.read(file);
                if (loadedImg != null) {
                    img = loadedImg;
                    imageView.setImage(new Image(file.toURI().toString()));
                    System.out.println("Image loaded: " + file.getAbsolutePath());
                } else {
                    System.out.println("Selected file is not a valid image.");
                }
            } catch (IOException e) {
                System.out.println("Error loading image: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No file selected.");
        }
    }


    /**
     * Saves the currently loaded image to a file.
     */
    public void onSaveImage() {
        if (imageView.getImage() == null) {
            System.out.println("No image to save!");
            return;
        }

        FileChooser fileChooser = new FileChooser();

        // Nastavení výchozího názvu souboru
        fileChooser.setInitialFileName("image.png");

        // Přidání filtrů souborů (například pouze PNG)
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png")
        );

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            // Ověření a přidání přípony .png, pokud chybí
            if (!file.getName().toLowerCase().endsWith(".png")) {
                file = new File(file.getAbsolutePath() + ".png");
            }

            try {
                // Získání aktuálního obrázku z ImageView
                WritableImage writableImage = (WritableImage) imageView.getImage();

                // Převod WritableImage na BufferedImage
                BufferedImage bufferedImage = convertToBufferedImage(writableImage);

                // Uložení obrázku do souboru
                ImageIO.write(bufferedImage, "png", file);
                System.out.println("Image saved to: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
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
                javafx.scene.paint.Color fxColor = pixelReader.getColor(x, y);
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

        // Nastavení negativního obrázku do ImageView
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
