package lab1;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.Collections;
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
    private Canvas canvas;

    public void close(ActionEvent actionEvent) {
        Platform.exit();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        canvas.widthProperty().bind(centerPane.widthProperty());
        canvas.heightProperty().bind(centerPane.heightProperty());
        canvas.widthProperty().addListener(e->draw());
        canvas.heightProperty().addListener(e->draw());
    }

    public void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITESMOKE);
        gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        gc.strokeLine(toScreenX(minX), toScreenY(0), toScreenX(maxX), toScreenY(0));
        gc.strokeLine(toScreenX(0),toScreenY(maxY),toScreenX(0),toScreenY(minY));
        List<Point> points = tabulation();
        gc.beginPath();
        gc.moveTo(toScreenX(points.get(0).getX()), toScreenY(points.get(0).getY()));
        for (int i=1; i<points.size(); i++) {
            gc.lineTo(toScreenX(points.get(i).getX()), toScreenY(points.get(i).getY()));
        }
        gc.stroke();
    }

    private List<Point> tabulation() {
        return IntStream
                .range(0, (int) canvas.getWidth())
                .mapToDouble(this::toWorldX)
                .mapToObj(x->new Point(x, f(x)))
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

    private double f(double x) {
        return x*Math.sin(x);
    }
}
