package ch.unibas.dmi.dbis.fds.p2p.ui.components;

import ch.unibas.dmi.dbis.fds.p2p.ui.drawing.ChordGraphicsContext;
import ch.unibas.dmi.dbis.fds.p2p.ui.drawing.VisualDummyNode;
import ch.unibas.dmi.dbis.fds.p2p.ui.drawing.VisualNode;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class ChordLegend extends VBox {
  private static final int size = 25;

  private Text legend;
  private Text blankText;
  private Text joiningText;
  private Text nodeText;

  private Canvas blankCanvas;
  private Canvas joiningCanvas;
  private Canvas nodeCanvas;
  private HBox emptyRow;
  private HBox joiningRow;
  private HBox nodeRow;

  public ChordLegend(){
    initComponents();
    attachComponents();
  }

  private void initComponents(){
    blankCanvas = new Canvas(size,size);
    joiningCanvas = new Canvas(size,size);
    nodeCanvas = new Canvas(size,size);
    emptyRow = new HBox();
    emptyRow.setAlignment(Pos.CENTER_LEFT);
    joiningRow = new HBox();
    joiningRow.setAlignment(Pos.CENTER_LEFT);
    nodeRow = new HBox();
    nodeRow.setAlignment(Pos.CENTER_LEFT);
    initTexts();
    initSymbols();
  }

  private void initSymbols() {
    ChordGraphicsContext cgc = new ChordGraphicsContext(blankCanvas.getGraphicsContext2D());
    cgc.render(0,0, new VisualDummyNode(1, false, false));
    cgc = new ChordGraphicsContext(joiningCanvas.getGraphicsContext2D());
    cgc.render(0,0, new VisualDummyNode(4, true, true));
    cgc = new ChordGraphicsContext(nodeCanvas.getGraphicsContext2D());
    cgc.render(0,0, new VisualDummyNode(8, true, false));
  }

  private void initTexts(){
    legend = new Text("Legend\n");
    blankText = new Text("Represents a blank (unused) node slot on the chord ring.");
    joiningText = new Text("Represents a joining node.");
    nodeText = new Text("Represents a node in the chord ring.");
  }

  private void setupContent(){
    emptyRow.getChildren().addAll(blankCanvas,blankText);
    joiningRow.getChildren().addAll(joiningCanvas,joiningText);
    nodeRow.getChildren().addAll(nodeCanvas,nodeText);
  }

  private void attachComponents(){
    setupContent();
    getChildren().addAll(legend,emptyRow,joiningRow,nodeRow);
  }
}
