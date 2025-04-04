package kurata.interactivewhiteboard;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Stack;

public class CanvasManager {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final StackPane canvasContainer;
    private final Stack<WritableImage> undoStack;
    private final Stack<WritableImage> redoStack;
    private Color currentColor;
    private String selectedTool;
    private double fontSize;

    /**
     * Constructor initializes the canvas, its graphics context, and undo/redo stacks.
     * Sets default drawing properties and configures event handlers.
     */
    public CanvasManager() {
        canvas = new Canvas(1000, 600);
        gc = canvas.getGraphicsContext2D();
        canvasContainer = new StackPane();
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        currentColor = Color.BLACK;
        selectedTool = "Pencil";
        fontSize = 20;

        configureCanvas();
    }

    /**
     * Configures canvas settings, including default drawing properties and event listeners.
     */
    private void configureCanvas() {
        gc.setLineWidth(2);
        canvas.setCursor(Cursor.CROSSHAIR);
        canvas.setOnMousePressed(this::startDraw);
        canvas.setOnMouseDragged(this::draw);
        canvas.setOnMouseReleased(e -> saveState());

        canvasContainer.getChildren().add(canvas);
        canvasContainer.setStyle("-fx-background-color: white;");
    }

    // Getters for the canvas and graphics context
    public StackPane getCanvasContainer() { return canvasContainer; }
    public Canvas getCanvas() { return canvas; }
    public GraphicsContext getGraphicsContext() { return gc; }

    /**
     * Sets the current drawing color.
     */
    public void setColor(Color color) {
        this.currentColor = color;
        gc.setStroke(currentColor);
    }

    /**
     * Handles mouse press event to start drawing.
     */
    private void startDraw(MouseEvent event) {
        gc.beginPath();
        gc.moveTo(event.getX(), event.getY());
        gc.setStroke(currentColor);
        gc.stroke();
    }

    /**
     * Handles mouse drag event to draw on the canvas based on the selected tool.
     */
    private void draw(MouseEvent event) {
        switch (selectedTool) {
            case "Pencil":
                gc.lineTo(event.getX(), event.getY());
                gc.stroke();
                gc.moveTo(event.getX(), event.getY());
                break;
            case "Eraser":
                gc.clearRect(event.getX() - 10, event.getY() - 10, 20, 20);
                break;
        }
    }

    /**
     * Selects the drawing tool.
     */
    public void selectTool(String tool) {
        selectedTool = tool;
        if ("Text".equals(tool)) {
            setTextPosition();
        }
    }

    /**
     * Saves the current state of the canvas for undo/redo functionality.
     */
    public void saveState() {
        WritableImage snapshot = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, snapshot);

        if (undoStack.isEmpty() || !imagesAreEqual(snapshot, undoStack.peek())) {
            undoStack.push(snapshot);
            redoStack.clear();
        }
    }

    /**
     * Undoes the last drawing action.
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(undoStack.pop());
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            if (!undoStack.isEmpty()) {
                gc.drawImage(undoStack.peek(), 0, 0);
            }
        }
    }

    /**
     * Redoes the last undone action.
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            WritableImage snapshot = redoStack.pop();
            undoStack.push(snapshot);
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.drawImage(snapshot, 0, 0);
        }
    }

    /**
     * Clears the entire canvas and resets the background color.
     */
    public void clearCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        saveState();
    }

    /**
     * Saves the canvas content as a PNG image file.
     */
    public void saveCanvas(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image", "*.png"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                WritableImage image = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, image);
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds text to the canvas at a specified location with the selected color.
     */
    public void addText(String text, double x, double y, Color color) {
        if (!text.isEmpty()) {
            gc.setFill(color);
            gc.setFont(new Font(fontSize));
            gc.fillText(text, x, y);
            saveState();
        }
    }

    /**
     * Prompts the user for text input and adds text at the clicked position on the canvas.
     */
    public void setTextPosition() {
        canvas.setOnMouseClicked(event -> {
            double x = event.getX();
            double y = event.getY();
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Text");
            dialog.setHeaderText("Enter the text to place on the canvas:");
            dialog.setContentText("Text:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(text -> addText(text, x, y, currentColor));

            // Reset mouse click event after text is added
            canvas.setOnMouseClicked(null);
        });
    }

    /**
     * Compares two images pixel-by-pixel to check if they are identical.
     */
    private boolean imagesAreEqual(WritableImage img1, WritableImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return false;
        }
        for (int x = 0; x < img1.getWidth(); x++) {
            for (int y = 0; y < img1.getHeight(); y++) {
                if (img1.getPixelReader().getArgb(x, y) != img2.getPixelReader().getArgb(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }
}
