package kurata.interactivewhiteboard;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Main application class for the Interactive Digital Whiteboard.
 * This class initializes and manages the application's UI components.
 */
public class DigitalWhiteboard extends Application {

    // Managers for canvas operations and media handling
    private final CanvasManager canvasManager = new CanvasManager();
    private final MediaManager mediaManager = new MediaManager(
            canvasManager.getCanvas(),
            canvasManager.getGraphicsContext(),
            canvasManager.getCanvasContainer()
    );

    // Toolbar to provide drawing and media tools
    private final Toolbar toolbar = new Toolbar(canvasManager, mediaManager);

    /**
     * Initializes and sets up the primary stage of the JavaFX application.
     *
     * @param primaryStage The main window of the application.
     */
    @Override
    public void start(Stage primaryStage) {
        // Create the main layout container
        BorderPane root = new BorderPane();

        // Set up UI components
        root.setTop(toolbar.createTopToolbar(primaryStage));       // Toolbar at the top
        root.setRight(toolbar.createSideToolbar());                // Sidebar for tools
        root.setBottom(toolbar.createBottomToolbar(primaryStage, mediaManager)); // Bottom media control toolbar
        root.setCenter(canvasManager.getCanvasContainer());        // Canvas area at the center

        // Create the scene with specified dimensions and apply styles
        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(getClass().getResource("Styles.css").toExternalForm());

        // Configure and display the primary stage
        primaryStage.setTitle("Digital Whiteboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
