package ch.unibas.dmi.dbis.fds.p2p.ui;/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */

import ch.unibas.dmi.dbis.fds.p2p.ui.components.ChordCanvas;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ChordTestingUI extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    final BorderPane borderPane = new BorderPane();
    ChordCanvas cc = new ChordCanvas(200);
    borderPane.setCenter(cc);
    cc.bindSize(borderPane);
    primaryStage.setScene(new Scene(borderPane));
    primaryStage.show();
  }
}
