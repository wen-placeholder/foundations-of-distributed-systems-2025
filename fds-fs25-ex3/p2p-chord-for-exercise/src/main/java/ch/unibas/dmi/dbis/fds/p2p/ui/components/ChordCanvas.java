package ch.unibas.dmi.dbis.fds.p2p.ui.components;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 * A special resizable canvas, which is used for chord purposes
 *
 * @author loris.sauter
 */
public class ChordCanvas extends Canvas {

  /*
  This is based on the blog post at
  https://dlsc.com/2014/04/10/javafx-tip-1-resizable-canvas/
   */

  private final int size;

  public ChordCanvas(int size){
    super();
    this.size = size;
    widthProperty().addListener(evt-> {
      draw();

    });
    heightProperty().addListener(evt-> draw());
  }



  @Override
  public double minHeight(double height){
    return size;
  }

  @Override
  public double minWidth(double width){
    return size;
  }


  @Override
  public boolean isResizable() {
    return true;
  }

  @Override
  public double prefHeight(double height) {
    return super.getWidth();
  }

  @Override
  public double prefWidth(double width){
    return super.getWidth();
  }

  public void draw(){
    double size = Math.min(getWidth(), getHeight());
    GraphicsContext gc = getGraphicsContext2D();
    gc.clearRect(0, 0, size, size);
    gc.setStroke(Color.RED);
    gc.strokeLine(0, 0, size, size);
    gc.strokeLine(0, size, size, 0);
    gc.strokeRect(0,0,size, size);
  }


  public void bindSize(Region region) {
    heightProperty().bindBidirectional(widthProperty());
    widthProperty().bind(region.widthProperty());
  }
}
