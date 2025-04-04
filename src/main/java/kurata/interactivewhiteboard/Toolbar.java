package kurata.interactivewhiteboard;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Toolbar {
    private CanvasManager canvasManager;
    private MediaManager mediaManager;

    // Constructor to initialize CanvasManager and MediaManager
    public Toolbar(CanvasManager canvasManager, MediaManager mediaManager) {
        this.canvasManager = canvasManager;
        this.mediaManager = mediaManager;
    }

    /**
     * Creates the top toolbar containing:
     * - Color Picker for selecting text color
     * - Text Field for inputting text
     * - Add Text Button to add text to the canvas
     * - Save Button to save the canvas
     */
    public HBox createTopToolbar(Stage stage) {
        // UI Components
        ColorPicker colorPicker = new ColorPicker();
        TextField textField = new TextField();
        Button addTextButton = new Button("Add Text");
        Button saveButton = new Button("Save");

        // Set cursor style for buttons
        addTextButton.setCursor(Cursor.HAND);
        saveButton.setCursor(Cursor.HAND);

        // Event handler: Change text color when a new color is selected
        colorPicker.setOnAction(e -> canvasManager.setColor(colorPicker.getValue()));

        // Event handler: Adding text to the canvas at the clicked location
        addTextButton.setOnAction(e -> {
            canvasManager.getCanvas().setOnMouseClicked(mouseEvent -> {
                double x = mouseEvent.getX();  // Get X coordinate
                double y = mouseEvent.getY();  // Get Y coordinate
                String text = textField.getText();  // Get text from input field
                Color color = colorPicker.getValue();  // Get selected color

                // Add text to the canvas at the specified coordinates with chosen color
                canvasManager.addText(text, x, y, color);
            });
        });

        // Event handler: Save canvas when the save button is clicked
        saveButton.setOnAction(e -> canvasManager.saveCanvas(stage));

        // Toolbar layout and arrangement
        HBox toolbar = new HBox(10, colorPicker, textField, addTextButton, saveButton);
        toolbar.setAlignment(Pos.CENTER);
        return toolbar;
    }

    /**
     * Creates the side toolbar containing:
     * - Pencil Button for drawing
     * - Eraser Button for erasing content
     * - Undo Button to revert last action
     * - Redo Button to redo undone actions
     * - Clear Button to clear the entire canvas
     */
    public VBox createSideToolbar() {
        VBox sideToolbar = new VBox(10);

        // UI Components
        Button pencilButton = new Button("Pencil");
        Button eraserButton = new Button("Eraser");
        Button undoButton = new Button("Undo");
        Button redoButton = new Button("Redo");
        Button clearButton = new Button("Clear");

        // Set cursor style for buttons
        pencilButton.setCursor(Cursor.HAND);
        eraserButton.setCursor(Cursor.HAND);
        undoButton.setCursor(Cursor.HAND);
        redoButton.setCursor(Cursor.HAND);
        clearButton.setCursor(Cursor.HAND);

        // Event handlers for each tool
        pencilButton.setOnAction(e -> canvasManager.selectTool("Pencil"));
        eraserButton.setOnAction(e -> canvasManager.selectTool("Eraser"));
        undoButton.setOnAction(e -> canvasManager.undo());
        redoButton.setOnAction(e -> canvasManager.redo());
        clearButton.setOnAction(e -> canvasManager.clearCanvas());

        // Add buttons to the side toolbar
        sideToolbar.getChildren().addAll(pencilButton, eraserButton, undoButton, redoButton, clearButton);
        return sideToolbar;
    }

    /**
     * Creates the bottom toolbar containing:
     * - Add Image Button to insert an image
     * - Add Video Button to insert a video
     * - Add Song Button to insert audio
     */
    public HBox createBottomToolbar(Stage stage, MediaManager mediaManager) {
        // UI Components
        Button addImageButton = new Button("Add Image");
        Button addVideoButton = new Button("Add Video");
        Button addSongButton = new Button("Add Song");

        // Set cursor style for buttons
        addImageButton.setCursor(Cursor.HAND);
        addVideoButton.setCursor(Cursor.HAND);
        addSongButton.setCursor(Cursor.HAND);

        // Event handlers for media actions
        addImageButton.setOnAction(e -> mediaManager.addImage(stage));
        addVideoButton.setOnAction(e -> mediaManager.addVideo(stage));
        addSongButton.setOnAction(e -> mediaManager.addSong(stage));

        // Toolbar layout and arrangement
        HBox bottomToolbar = new HBox(10, addImageButton, addVideoButton, addSongButton);
        bottomToolbar.setAlignment(Pos.CENTER);
        return bottomToolbar;
    }
}
