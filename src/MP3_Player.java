
/**
 * Before running it, it is necessary to add module javafx.media on VM Options in run configuration.
 */

// File imports
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

// Standard javafx imports.
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.util.Duration;

// Imports for components
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;

// Imports for layout.
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;


public class MP3_Player extends Application {
    // declare all components for application
    // objects for media
    Media media;
    MediaPlayer mediaPlayer;

    // observable list for storing track
    ObservableList<String> trackList =  FXCollections.observableArrayList();
    // observable list for managing track playing
    ObservableList<String> trackSelList = FXCollections.observableArrayList();

    Label lblAvailbleTracks, lblSelectedTracks, lblVol, lblStat;

    Slider sldrVolume, sldrStatus;

    Button btnAdd, btnRemove, btnRemoveAll, btnPlay, btnPause, btnStop, btnOpen;

    ListView<String> lvAvailableTracks, lvSelectedTracks;

    // duration object to check time
    Duration duration;

    // empty string to check media status
    Label lblMediaCheck = new Label("");

    /**
     * Constructor
     */
    public MP3_Player() {

        // instantiate all components.
        // labels
        lblAvailbleTracks = new Label("Available Tracks:");
        lblSelectedTracks = new Label("Selected Tracks:");
        lblVol = new Label("Volume:");
        lblStat = new Label("Status:");

        // buttons
        btnAdd = new Button("Add >");
        btnRemove = new Button("< Remove");
        btnRemoveAll = new Button("<< Remove All");
        btnPlay = new Button("Play");
        btnPause = new Button("Pause");
        btnStop = new Button("Stop");
        btnOpen = new Button("Open File");

        // sliders
        sldrVolume = new Slider();
        sldrStatus = new Slider();

        // listviews
        lvAvailableTracks = new ListView<>();
        lvSelectedTracks = new ListView<>();

        // sizing buttons.
        btnAdd.setMinWidth(130);
        btnRemove.setMinWidth(130);
        btnRemoveAll.setMinWidth(130);
        btnPlay.setMinWidth(130);
        btnPause.setMinWidth(130);
        btnStop.setMinWidth(130);
        btnOpen.setMinWidth(130);

        //manage slider sizes
        sldrVolume.setMaxWidth(130);

    }//constructor()

    @Override
    public void init() {
        // add selected tracks to the ListView
        lvSelectedTracks.setItems(trackSelList);

        // disable "Add" button if nothing is selected from available listview
        btnAdd.disableProperty().bind(Bindings.isNull(lvAvailableTracks.getSelectionModel().selectedItemProperty()));

        // action event for "Add" button
        btnAdd.setOnAction(ae-> add());

        // disable "Remove" button if nothing is selected from selected listview
        btnRemove.disableProperty().bind(Bindings.isNull(lvSelectedTracks.getSelectionModel().selectedItemProperty()));

        // action event for "Remove" button
        btnRemove.setOnAction(ae-> remove());

        // disable "Remove All" button if nothing is remained on selected listview
        btnRemoveAll.disableProperty().bind(Bindings.isEmpty(trackSelList));

        // action event for "Remove All" button
        btnRemoveAll.setOnAction(ae-> removeAll());

        // disable "Play" button if nothing is selected
        btnPlay.disableProperty().bind(Bindings.isNull(lvSelectedTracks.getSelectionModel().selectedItemProperty()));

        // action event for "Play" button
        btnPlay.setOnAction(ae-> play());

        // action event for "Pause" button
        btnPause.setOnAction(ae-> pause());

        // action event for "Stop" button
        btnStop.setOnAction(ae-> trackStop());

        // action event for "Open File" button
        btnOpen.setOnAction(ae-> getMusicFiles());

    }//init()

    @Override
    public void start(Stage primaryStage) throws Exception {
        // set the title.
        primaryStage.setTitle("My MP3 Player");

        // add an icon on title .
        Image image = new Image("logo_MP3_BlastBox.png");
        primaryStage.getIcons().add(image);

        // sizing the stage
        primaryStage.setWidth(700);
        primaryStage.setHeight(500);

        // create a main layout
        HBox hbMain = new HBox();

        // set spacing to main layout
        hbMain.setAlignment(Pos.CENTER);
        hbMain.setPadding(new Insets(10));
        hbMain.setSpacing(10);

        // create three sub layouts
        VBox vbLeft = new VBox();
        VBox vbCenter = new VBox();
        VBox vbRight = new VBox();

        // set spacing to sub layouts
        vbLeft.setPadding(new Insets(10));
        vbLeft.setSpacing(10);

        vbCenter.setPadding(new Insets(10));
        vbCenter.setSpacing(10);

        vbRight.setPadding(new Insets(10));
        vbRight.setSpacing(10);

        // add components to each sub layout
        vbLeft.getChildren().add(lblAvailbleTracks);
        vbLeft.getChildren().add(lvAvailableTracks);
        vbLeft.getChildren().add(btnOpen);

        vbCenter.getChildren().add(btnAdd);
        vbCenter.getChildren().add(btnRemove);
        vbCenter.getChildren().add(btnRemoveAll);
        vbCenter.getChildren().add(btnPlay);
        vbCenter.getChildren().add(btnPause);
        vbCenter.getChildren().add(btnStop);
        vbCenter.getChildren().add(lblVol);
        vbCenter.getChildren().add(sldrVolume);

        vbRight.getChildren().add(lblSelectedTracks);
        vbRight.getChildren().add(lvSelectedTracks);
        vbRight.getChildren().add(lblStat);
        vbRight.getChildren().add(sldrStatus);

        // add sub layouts to main HBox
        hbMain.getChildren().add(vbLeft);
        hbMain.getChildren().add(vbCenter);
        hbMain.getChildren().add(vbRight);

        // setting slider
        sldrVolume.setValue(50);
        sldrStatus.setValue(0);

        // create a scene
        Scene s= new Scene(hbMain);

        // set the scene.
        primaryStage.setScene(s);

        // set style sheet as external form
        s.getStylesheets().add(getClass().getResource("style_MP3_BlastBox.css").toExternalForm());

        // show the stage.
        primaryStage.show();

    }//start()

    //method for adding tracks from available to selected list view
    public void add() {
        String music = lvAvailableTracks.getSelectionModel().getSelectedItem();
        if(music != null) {
            lvAvailableTracks.getSelectionModel().clearSelection();
            trackSelList.add(music);
        }
    }//add()

    // method for removing tracks from selected to available list view
    public void remove() {
        String music = lvSelectedTracks.getSelectionModel().getSelectedItem();
        if(music != null) {
            lvSelectedTracks.getSelectionModel().clearSelection();
            trackSelList.remove(music);
        }
    }//remove()

    // method that removes all selected list view tracks
    public void removeAll() {
        trackSelList.clear();
    }//removeAll()

    //method to play the selected track
    public void play() {
        String musicTrack = lvSelectedTracks.getSelectionModel().getSelectedItem();

        File mediaFile = new File(musicTrack);
        media = new Media(Paths.get(String.valueOf(mediaFile)).toUri().toString());
        mediaPlayer = new MediaPlayer(media);

        // if status is paused then play it from that moment
        if(lblMediaCheck.getText() == "paused"){
            mediaPlayer.play();
            btnPause.setDisable(false); // enable pause button
        }
        // if status is not paused then implement below
        else {
            if(mediaPlayer != null) {
                mediaPlayer.stop();  // stop the media player
            }

            // enable stop and pause buttons
            btnPause.setDisable(false);
            btnStop.setDisable(false);

            // set media check "playing"
            lblMediaCheck.setText("playing");

            // invoke status method to bind progress of media to slider
            status(mediaPlayer, sldrStatus);

            mediaPlayer.play();
        }

        // invoke volume method to bind volume of media to slider
        volume(mediaPlayer, sldrVolume);

    }//play()

    // method to pause the track playing
    public void pause() {
        mediaPlayer.pause();
        // get the time duration at the moment of paused
        this.duration = mediaPlayer.getCurrentTime();
        // set check text "paused"
        lblMediaCheck.setText("paused");
        // disable pause button
        btnPause.setDisable(true);

    }//pause()

    // method to stop track playing
    public void trackStop() {
        // implement only if media player is not empty
        if(mediaPlayer != null ) {
            mediaPlayer.stop();
            // set check text "stopped"
            lblMediaCheck.setText("stopped");
            // disable stop and pause buttons
            btnStop.setDisable(true);
            btnPause.setDisable(true);
        }
    }//trackStop()

    // method to bind media to volume slider
    public void volume(MediaPlayer mediaPlayer, Slider sldrVolume) {
        if(mediaPlayer != null) {
            mediaPlayer.volumeProperty().bind(sldrVolume.valueProperty().divide(100));
        }
    }//volume(MediaPlayer, Slider)

    // method to bind media to progress slider
    public void status (MediaPlayer mediaPlayer, Slider sldrStatus) {
        mediaPlayer.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            sldrStatus.setValue(newValue.toSeconds());
        });
    }//status(MediaPlayer, Slider)

    // method to open file and store in available list
    public void getMusicFiles () {
        // ObservableList to store the music files
        ObservableList<String> musics = FXCollections.observableArrayList();

        // Create a FileChooser to allow selection of a file
        FileChooser fc = new FileChooser();

        // filtering files
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac")
        );

        // assign a file
        File file = fc.showOpenDialog(null);

        // String to store file
        String fileName;

        // if dialog is confirmed (OK clicked)
        if(file != null) {
            // call getName() to get a file into the string.
            fileName = file.getPath();

            // add audio file to the musics(Observable List)
            musics.addAll(fileName);

            //returns mp3 files from the getMusicFile method stores in trackList
            trackList.addAll(musics);

            // add the tracks to the ListView.
            lvAvailableTracks.setItems(trackList);

        }else; // dialog cancelled. do nothing.
    }// getMusicFiles()

    public static void main(String[] args) {
        // Launch the application
        launch();
    }//main()

}//class

