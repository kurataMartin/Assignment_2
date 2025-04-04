package kurata.interactivewhiteboard;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import java.io.File;

public class MediaManager {
    private final GraphicsContext gc;
    private final Canvas canvas;
    private final StackPane canvasContainer;
    private MediaPlayer mediaPlayer;  // Handles audio playback
    private MediaPlayer videoPlayer;  // Handles video playback
    private Text mediaDescriptionText;  // Displays the current media file name

    /**
     * Constructor initializes media manager components and sets up a text field for media descriptions.
     *
     * @param canvas          The drawing canvas
     * @param gc              The graphics context for the canvas
     * @param canvasContainer The container holding the canvas and media elements
     */
    public MediaManager(Canvas canvas, GraphicsContext gc, StackPane canvasContainer) {
        this.canvas = canvas;
        this.gc = gc;
        this.canvasContainer = canvasContainer;
        this.mediaDescriptionText = new Text();

        // Add media description text to the container and align it at the top
        canvasContainer.getChildren().add(mediaDescriptionText);
        StackPane.setAlignment(mediaDescriptionText, Pos.TOP_CENTER);
    }

    /**
     * Opens a file chooser to select an image and draws it onto the canvas.
     *
     * @param stage The JavaFX stage
     */
    public void addImage(Stage stage) {
        File file = openFileChooser(stage, "Image Files", "*.png", "*.jpg", "*.jpeg");
        if (file != null) {
            Image selectedImage = new Image(file.toURI().toString());

            // Scale image to fit within canvas dimensions
            double[] newSize = getScaledDimensions(selectedImage.getWidth(), selectedImage.getHeight());

            // Draw image onto canvas
            gc.drawImage(selectedImage, 50, 50, newSize[0], newSize[1]);
            saveState();
        }
    }

    /**
     * Opens a file chooser to select a video and plays it within the canvas container.
     *
     * @param stage The JavaFX stage
     */
    public void addVideo(Stage stage) {
        File file = openFileChooser(stage, "Video Files", "*.mp4", "*.avi", "*.mov");
        if (file != null) {
            Media media = new Media(file.toURI().toString());
            videoPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(videoPlayer);

            // Set video dimensions and alignment
            mediaView.setFitWidth(400);
            mediaView.setFitHeight(300);
            mediaView.setPreserveRatio(true);
            StackPane.setAlignment(mediaView, Pos.CENTER);

            // Add video to canvas container and start playback
            canvasContainer.getChildren().add(mediaView);
            videoPlayer.play();

            // Display media file name at the top
            mediaDescriptionText.setText("Playing Video: " + file.getName());

            // Create a play/pause button for the video
            Button playPauseButton = new Button("Pause");
            playPauseButton.setOnAction(e -> toggleVideoPlayPause(playPauseButton));
            StackPane.setAlignment(playPauseButton, Pos.BOTTOM_CENTER);
            playPauseButton.setPrefWidth(100);

            // Add button to container
            canvasContainer.getChildren().add(playPauseButton);
        }
    }

    /**
     * Toggles play and pause for the video player.
     *
     * @param playPauseButton The button controlling playback
     */
    private void toggleVideoPlayPause(Button playPauseButton) {
        if (videoPlayer != null) {
            if (videoPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                videoPlayer.pause();
                playPauseButton.setText("Play");
            } else {
                videoPlayer.play();
                playPauseButton.setText("Pause");
            }
        }
    }

    /**
     * Opens a file chooser to select an audio file and plays it.
     * Also displays a music icon and play/pause controls.
     *
     * @param stage The JavaFX stage
     */
    public void addSong(Stage stage) {
        File file = openFileChooser(stage, "Audio Files", "*.mp3");
        if (file != null) {
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();

            // Load and display music icon
            Image musicIconImage = new Image(getClass().getResource("icon.jpg").toExternalForm());
            ImageView musicIcon = new ImageView(musicIconImage);
            musicIcon.setFitWidth(200);
            musicIcon.setFitHeight(200);
            StackPane.setAlignment(musicIcon, Pos.CENTER);
            canvasContainer.getChildren().add(musicIcon);

            // Display media file name
            mediaDescriptionText.setText("Playing Audio: " + file.getName());

            // Create a play/pause button for the audio
            Button playPauseButton = new Button("Pause");
            playPauseButton.setOnAction(e -> toggleAudioPlayPause(playPauseButton));
            StackPane.setAlignment(playPauseButton, Pos.BOTTOM_CENTER);
            playPauseButton.setPrefWidth(100);
            canvasContainer.getChildren().add(playPauseButton);

            // Remove media elements when playback ends
            mediaPlayer.setOnEndOfMedia(() -> {
                canvasContainer.getChildren().remove(musicIcon);
                canvasContainer.getChildren().remove(playPauseButton);
            });
        }
    }

    /**
     * Toggles play and pause for the audio player.
     *
     * @param playPauseButton The button controlling playback
     */
    private void toggleAudioPlayPause(Button playPauseButton) {
        if (mediaPlayer != null) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playPauseButton.setText("Play");
            } else {
                mediaPlayer.play();
                playPauseButton.setText("Pause");
            }
        }
    }

    /**
     * Opens a file chooser dialog with specified filters.
     *
     * @param stage       The JavaFX stage
     * @param description The description of the file type
     * @param extensions  The allowed file extensions
     * @return The selected file
     */
    private File openFileChooser(Stage stage, String description, String... extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(description, extensions));
        return fileChooser.showOpenDialog(stage);
    }

    /**
     * Calculates scaled dimensions for an image to fit within the canvas.
     *
     * @param originalWidth  The original width of the image
     * @param originalHeight The original height of the image
     * @return A double array containing the new width and height
     */
    private double[] getScaledDimensions(double originalWidth, double originalHeight) {
        double maxWidth = canvas.getWidth();
        double maxHeight = canvas.getHeight();
        double newWidth = originalWidth;
        double newHeight = originalHeight;

        // Scale the image while maintaining aspect ratio
        if (originalWidth > maxWidth || originalHeight > maxHeight) {
            double aspectRatio = originalWidth / originalHeight;
            if (originalWidth > maxWidth) {
                newWidth = maxWidth;
                newHeight = newWidth / aspectRatio;
            }
            if (newHeight > maxHeight) {
                newHeight = maxHeight;
                newWidth = newHeight * aspectRatio;
            }
        }
        return new double[]{newWidth, newHeight};
    }

    /**
     * Saves the current state of the canvas.
     * Placeholder for undo functionality.
     */
    private void saveState() {
        // Implement undo functionality if needed
    }
}
