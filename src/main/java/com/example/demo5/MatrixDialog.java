package com.example.demo5;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MatrixDialog {

    private final int size;
    private final List<Spinner<Integer>> spinners = new ArrayList<>();
    private double[][] savedMatrix;
    private static final String DEFAULT_FILE_NAME = "matrix.xml";

    public MatrixDialog(int size) {
        this.size = size;
        this.savedMatrix = new double[size][size]; // Inicializace prázdné matice
    }

    public double[][] showAndWait() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Matrix Editor");

        // Grid pro matici
        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Spinner<Integer> spinner = new Spinner<>(-100, 100, 0);
                spinner.setPrefWidth(50);
                spinner.setEditable(true);
                spinners.add(spinner);
                gridPane.add(spinner, col, row);
            }
        }

        // Tlačítka
        Button saveButton = new Button("Save Matrix");
        Button loadButton = new Button("Load Matrix");
        Button applyButton = new Button("Apply Matrix Filter");
        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");

        // Akce tlačítek
        saveButton.setOnAction(e -> saveMatrixToFile());
        loadButton.setOnAction(e -> loadMatrixFromFile());
        applyButton.setOnAction(e -> applyMatrixFilter());
        okButton.setOnAction(e -> {
            saveMatrixToMemory();
            stage.close();
        });
        cancelButton.setOnAction(e -> stage.close());

        // Layout
        VBox layout = new VBox(10, gridPane);
        HBox buttonBox = new HBox(10, saveButton, loadButton, applyButton, okButton, cancelButton);
        buttonBox.setPadding(new Insets(10));
        layout.getChildren().add(buttonBox);

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.showAndWait();

        return savedMatrix; // Vrátí aktuálně uloženou matici
    }

    private void saveMatrixToFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Matrix");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.newDocument();

                Element rootElement = doc.createElement("matrix");
                doc.appendChild(rootElement);

                for (int row = 0; row < size; row++) {
                    Element rowElement = doc.createElement("row");
                    for (int col = 0; col < size; col++) {
                        Element cell = doc.createElement("cell");
                        cell.appendChild(doc.createTextNode(String.valueOf(spinners.get(row * size + col).getValue())));
                        rowElement.appendChild(cell);
                    }
                    rootElement.appendChild(rowElement);
                }

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(file);
                transformer.transform(source, result);

                System.out.println("Matrix saved to " + file.getAbsolutePath());
            } catch (ParserConfigurationException | TransformerException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadMatrixFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Matrix");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(file);

                doc.getDocumentElement().normalize();
                NodeList rowList = doc.getElementsByTagName("row");

                for (int row = 0; row < rowList.getLength() && row < size; row++) {
                    NodeList cellList = rowList.item(row).getChildNodes();
                    for (int col = 0; col < cellList.getLength() && col < size; col++) {
                        Node cell = cellList.item(col);
                        int value = Integer.parseInt(cell.getTextContent());
                        spinners.get(row * size + col).getValueFactory().setValue(value);
                    }
                }

                System.out.println("Matrix loaded from " + file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void applyMatrixFilter() {
        saveMatrixToMemory();
        System.out.println("Matrix filter applied with the following matrix:");
        for (double[] row : savedMatrix) {
            for (double cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    private void saveMatrixToMemory() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                savedMatrix[row][col] = spinners.get(row * size + col).getValue();
            }
        }
        System.out.println("Matrix saved to memory.");
    }
}
