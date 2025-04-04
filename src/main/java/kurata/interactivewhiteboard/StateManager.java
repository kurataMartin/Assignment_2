package kurata.interactivewhiteboard;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.embed.swing.SwingFXUtils;

import java.awt.image.BufferedImage;
import java.util.Stack;

public class StateManager {
    private final Stack<BufferedImage> undoStack = new Stack<>();
    private final Stack<BufferedImage> redoStack = new Stack<>();
    private final Canvas canvas;
    private final GraphicsContext gc;

    public StateManager(GraphicsContext gc, Canvas canvas) {
        this.gc = gc;
        this.canvas = canvas;
        saveState(); // Save initial state
    }

    public void saveState() {
        WritableImage snapshot = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, snapshot);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);
        undoStack.push(bufferedImage);
        redoStack.clear(); // Clear redo stack on new action
    }

    public void undo() {
        if (undoStack.size() > 1) {
            redoStack.push(undoStack.pop()); // Move last state to redo stack
            restoreState(undoStack.peek());
        } else {
            clearCanvas();
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            BufferedImage redoImage = redoStack.pop();
            undoStack.push(redoImage);
            restoreState(redoImage);
        }
    }

    private void restoreState(BufferedImage image) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Clear the canvas
        gc.drawImage(SwingFXUtils.toFXImage(image, null), 0, 0); // Draw saved state
    }

    public void clearCanvas() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        saveState();
    }
}
