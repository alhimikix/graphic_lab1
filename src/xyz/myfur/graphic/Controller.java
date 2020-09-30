package xyz.myfur.graphic;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

    double minX = -100;
    double maxX = 100;
    double minY = -100;
    double maxY = 100;

    @FXML
    private Pane centerPane;

    @FXML
    private TextField minXField;

    @FXML
    private TextField maxXField;

    @FXML
    private TextField minYField;

    @FXML
    private TextField maxYField;

    @FXML
    private Canvas canvas;

    public void close(ActionEvent actionEvent) {
        Platform.exit();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        canvas.widthProperty().bind(centerPane.widthProperty());
        canvas.heightProperty().bind(centerPane.heightProperty());
        canvas.widthProperty().addListener(e -> draw());
        canvas.heightProperty().addListener(e -> draw());
    }

    public void saveToFile() {
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, writableImage);
        RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
        String filename = JOptionPane.showInputDialog("Enter filename!");
        try {
            ImageIO.write(renderedImage, "png", new File("./" + filename + ".png"));
        } catch (Throwable ignored) {
        }
    }

    public void draw() {

        minX = getFieldInteger(minXField, minX);
        minY = getFieldInteger(minYField, minY);
        maxX = getFieldInteger(maxXField, maxX);
        maxY = getFieldInteger(maxYField, maxY);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITESMOKE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.strokeLine(toScreenX(minX), toScreenY(0), toScreenX(maxX), toScreenY(0));
        gc.strokeLine(toScreenX(0), toScreenY(maxY), toScreenX(0), toScreenY(minY));
        List<Point> points = tabulation();
        gc.beginPath();
        gc.moveTo(toScreenX(points.get(0).getX()), toScreenY(points.get(0).getY()));
        for (int i = 1; i < points.size(); i++) {
            gc.lineTo(toScreenX(points.get(i).getX()), toScreenY(points.get(i).getY()));
        }
        gc.stroke();
    }

    private List<Point> tabulation() {
        return IntStream
                .range(0, (int) canvas.getWidth())
                .mapToDouble(this::toWorldX)
                .mapToObj(x -> new Point(x, f(x)))
                .collect(Collectors.toList());
    }

    private int toScreenX(double x) {
        return (int) (canvas.getWidth() * (x - minX) / (maxX - minX));
    }

    private int toScreenY(double y) {
        return (int) (canvas.getHeight() * (1 - (y - minY) / (maxY - minY)));
    }

    private double toWorldX(int xs) {
        return 1.0 * xs / canvas.getWidth() * (maxX - minX) + minX;
    }

    private double toWorldY(int ys) {
        return (1.0 * ys - canvas.getHeight()) / (-canvas.getHeight()) * (maxY - minY) + minY;
    }

    public void checkChanges() {
        if (
                isChanged(minXField, minX) ||
                        isChanged(minYField, minY) ||
                        isChanged(minXField, maxX) ||
                        isChanged(maxYField, maxY)
        ) {
            draw();
        }
    }

    private double getFieldInteger(TextField field, double defaultValue) {

        try {
            return Double.parseDouble(field.getText().trim());
        } catch (Throwable ignored) {
            try {
                return Integer.parseInt(field.getText().trim());
            } catch (Throwable ignore) {
            }
        }
        return defaultValue;

    }

    private boolean isChanged(TextField field, double prevValue) {
        return getFieldInteger(field, prevValue) != prevValue;
    }

    private double f(double x) {
        return (1.3 + 0.5 * Math.pow(x, 4)) / (Math.log(1.14 + Math.sqrt(0.34 + x * x)));
    }
}
