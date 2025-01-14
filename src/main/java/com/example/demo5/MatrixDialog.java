package com.example.demo5;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MatrixDialog {

    private final int size; // Size of the matrix (e.g., 10x10)
    private final List<Spinner<Integer>> spinners = new ArrayList<>();
    private double normalizationCoefficient = 1.0; // Normalization coefficient
    private double matrixSum = 0.0;

    public MatrixDialog(int size) {
        this.size = size;
    }

    public double[][] showAndWait() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Matrix Editor");

        // Grid for spinners
        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        // Populate the grid with spinners
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Spinner<Integer> spinner = new Spinner<>(-100, 100, 0);
                spinner.setPrefWidth(50);
                spinner.setMaxWidth(50);

                spinner.setEditable(true);
                spinners.add(spinner);
                gridPane.add(spinner, col, row);
            }
        }

        // Normalization controls
        CheckBox normalizationCheckBox = new CheckBox("Normalization / Coefficient");
        Label coefficientLabel = new Label("0.0");
        normalizationCheckBox.setOnAction(e -> updateNormalization(coefficientLabel));

        // Buttons
        Button saveButton = new Button("Save Matrix");
        Button loadButton = new Button("Load Matrix");
        Button clearButton = new Button("Clear Matrix");
        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");

        // Set button actions
        saveButton.setOnAction(e -> saveMatrixToFile());
        loadButton.setOnAction(e -> loadMatrixFromFile());
        clearButton.setOnAction(e -> clearMatrix());
        cancelButton.setOnAction(e -> stage.close());
        okButton.setOnAction(e -> stage.close()); // Return values handled later

        // Layout
        VBox layout = new VBox(10, gridPane, normalizationCheckBox, coefficientLabel);
        HBox buttonBox = new HBox(10, saveButton, loadButton, clearButton, okButton, cancelButton);
        buttonBox.setPadding(new Insets(10));
        layout.getChildren().add(buttonBox);

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.showAndWait();

        // Return the matrix values
        return getMatrixValues();
    }

    private void updateNormalization(Label coefficientLabel) {
        // Calculate the sum of matrix values and update the normalization coefficient
        matrixSum = spinners.stream().mapToDouble(spinner -> spinner.getValue()).sum();
        normalizationCoefficient = matrixSum != 0 ? 1.0 / matrixSum : 0.0;
        coefficientLabel.setText(String.format("Coefficient: %.3f", normalizationCoefficient));
    }

    private void saveMatrixToFile() {
        // Implementation for saving the matrix to a file
        // (e.g., use BufferedWriter and write each row of the matrix)
    }

    private void loadMatrixFromFile() {
        // Implementation for loading the matrix from a file
        // (e.g., read each line and set spinner values accordingly)
    }

    private void clearMatrix() {
        spinners.forEach(spinner -> spinner.getValueFactory().setValue(0));
    }

    private double[][] getMatrixValues() {
        double[][] matrix = new double[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                matrix[row][col] = spinners.get(row * size + col).getValue();
            }
        }
        return matrix;
    }
}
