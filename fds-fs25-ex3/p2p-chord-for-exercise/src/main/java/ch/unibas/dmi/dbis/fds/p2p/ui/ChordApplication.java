package ch.unibas.dmi.dbis.fds.p2p.ui;/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */

import ch.unibas.dmi.dbis.fds.p2p.simulation.SimulationEngine;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ch.unibas.dmi.dbis.fds.p2p.ui.simulation.ChordEventLog;
import ch.unibas.dmi.dbis.fds.p2p.ui.simulation.ChordPanel;
import ch.unibas.dmi.dbis.fds.p2p.ui.simulation.NodeInspector;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChordApplication extends Application {

  private static final String DYNAMIC_PARAMETER_NAME = "dynamic";

  private static final String NB_OF_BYTES_PARAMETER_NAME = "bits";

  /** The thread pool used for execution of the simulation. */
  private final static ExecutorService THREADPOOL = Executors.newFixedThreadPool(1);

  /** Logger used to log errors. */
  private final static Logger LOGGER = LogManager.getLogger();

  public static void main(String[] args) {
    /* Launch application. */
    launch(args);
  }

  /**
   * Called when the application is started.
   *
   * @param primaryStage
   */
  @Override
  public void start(Stage primaryStage) {
    /* Read configuration and prepare config. */
    final boolean dynamic = Boolean.parseBoolean(getParameters().getNamed().getOrDefault(DYNAMIC_PARAMETER_NAME, "false"));
    final int nbits = Integer.parseInt(getParameters().getNamed().getOrDefault(NB_OF_BYTES_PARAMETER_NAME, "3"));
    final ChordConfiguration config = new ChordConfiguration(dynamic, nbits);

    System.out.println("Starting Chord simulator with config: " + config);

    /* Prepare and fire-up the simulation engine. */
    final SimulationEngine engine = new SimulationEngine(config.getNbits(), config.isDynamic());
    THREADPOOL.submit(engine);

    /* Prepare ChordPanel and associated Inspector + EventLog. */
    final AnchorPane main = new AnchorPane();
    final ChordPanel chordPanel = new ChordPanel(650, engine);
    final NodeInspector inspector = new NodeInspector(chordPanel);
    final ChordEventLog eventLog = new ChordEventLog(engine);

    /* Prepare buttons to control simulation. */
    final HBox buttonContainer = new HBox();
    final Button startButton = new Button();
    startButton.setOnAction(e -> {
      engine.toggle();
      if (engine.isPaused()) {
        startButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLAY));
      } else {
        startButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PAUSE));
      }
    });
    startButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLAY));

    final Button stopButton = new Button();
    stopButton.setOnAction(e -> {
      engine.interrupt();
      Platform.exit();
    });
    stopButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.STOP));

    final Button helpBtn = new Button("Help");
    helpBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.QUESTION));
    helpBtn.setOnAction(e -> chordPanel.showHelp());

    final Label sliderLabel = new Label("Speed: ");
    final Slider slider = new Slider(0,1,0.01);
    slider.setMin(-1.0);
    slider.setValue(0);
    slider.setMax(1.0);
    engine.bindPace(slider.valueProperty());
    buttonContainer.getChildren().addAll(startButton,stopButton,helpBtn,sliderLabel,slider);
    VBox.setVgrow(eventLog, Priority.SOMETIMES);

    buttonContainer.setStyle("-fx-padding: 10px; -fx-spacing: 10px;" + buttonContainer.getStyle());
    buttonContainer.prefWidthProperty().bindBidirectional(main.prefWidthProperty());
    buttonContainer.setPrefHeight(75.0);
    buttonContainer.setMaxHeight(75.0);
    buttonContainer.setMinHeight(75.0);

    /* Set anchors for button container. */
    AnchorPane.setBottomAnchor(buttonContainer, 0.0);
    AnchorPane.setLeftAnchor(buttonContainer, 0.0);

    /* Set anchors for ChordPanel. */
    AnchorPane.setTopAnchor(chordPanel, 0.0);
    AnchorPane.setLeftAnchor(chordPanel, 0.0);
    AnchorPane.setRightAnchor(chordPanel, 0.0);
    AnchorPane.setBottomAnchor(chordPanel, buttonContainer.getHeight());
    main.getChildren().addAll(chordPanel, buttonContainer);

    /* Prepare SplitPane between EventLog and NodeInspector. */
    final SplitPane logInspectorPane = new SplitPane();
    logInspectorPane.setOrientation(Orientation.VERTICAL);
    logInspectorPane.setDividerPositions(0.75f);
    logInspectorPane.getItems().addAll(eventLog, inspector);

    /* Prepare SplitPane between SimulationCanvas and information. */
    final SplitPane split = new SplitPane();
    split.setDividerPositions(0.65);
    split.getItems().addAll(main, logInspectorPane);

    /* Prepare final scene. */
    Scene scene = new Scene(split, 900, 700);
    primaryStage.setTitle(String.format("Chord Simulation (bits=%d, dynamic=%b)", nbits, dynamic));
    primaryStage.setScene(scene);
    primaryStage.setMinWidth(750);
    primaryStage.setMinHeight(700);
    primaryStage.show();
  }

  /**
   * Called when the application is shutdown.
   */
  public void stop() {
    try {
      THREADPOOL.shutdownNow();
      THREADPOOL.awaitTermination(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      LOGGER.log(Level.ERROR, "Interrupted while waiting for thread pool to wind down.");
    }
  }

  private static class ChordConfiguration{

    private final boolean dynamic;
    private final int nbits;

    private ChordConfiguration(boolean dynamic, int nbits) {
      this.dynamic = dynamic;
      this.nbits = nbits;
    }

    public boolean isDynamic() {
      return dynamic;
    }

    public int getNbits() {
      return nbits;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      ChordConfiguration that = (ChordConfiguration) o;
      return isDynamic() == that.isDynamic() &&
          getNbits() == that.getNbits();
    }

    @Override
    public int hashCode() {

      return Objects.hash(isDynamic(), getNbits());
    }

    @Override
    public String toString() {
      return String.format("ChordConfiguration{bits=%d, dynamic=%b}", nbits, dynamic);
    }
  }
}
