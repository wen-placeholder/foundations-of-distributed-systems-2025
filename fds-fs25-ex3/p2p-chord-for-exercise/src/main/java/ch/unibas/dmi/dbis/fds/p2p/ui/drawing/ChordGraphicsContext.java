package ch.unibas.dmi.dbis.fds.p2p.ui.drawing;

import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class ChordGraphicsContext {

  private final GraphicsContext gc;

  private final double originX;
  private final double originY;

  public ChordGraphicsContext(GraphicsContext gc) {
    this.gc = gc;
    originX = gc.getCanvas().getWidth() /2d;
    originY = gc.getCanvas().getHeight() /2d;
  }

  public ChordGraphicsContext(GraphicsContext gc, double originX, double originY){
    this.gc = gc;
    this.originX = originX;
    this.originY = originY;
  }

  /**
   * x and y relative to center
   * @param x
   * @param y
   * @param r
   */
  public void strokeCircle(double x, double y, double r){
    r *= 2d;
    gc.strokeOval(originX+x-r/2d, originY+y-r/2d,r,r);
  }

  public void fillCircle(double x, double y, double r){
    r *= 2d;
    gc.fillOval(originX+x-r/2d, originY + y-r/2d ,r,r);
  }

  public void renderRegularlyOnCircle(double x, double y, double r, List<? extends Drawable> drawables){
    double angle = 2*Math.PI / drawables.size();
    for(int i=0; i<drawables.size();i++){
      double ix,iy;
      ix = r * Math.sin(i*angle);
      iy = -1d*r * Math.cos(i*angle); // y axis up
      double offsetX = 0d;
      double offsetY = 0d;
      Drawable drawable = drawables.get(i);
      if(drawable instanceof DrawableRegion){
        offsetX = ((DrawableRegion)drawable).getWidth() / 2d;
        offsetY = ((DrawableRegion)drawable).getHeight() / 2d;
      }
      drawable.draw(gc, originX + x+ix - offsetX, originY +y+ iy-offsetY);
    }
  }

  /**
   * Renders the specified {@link Drawable} at the given position.
   * The position is center-based as all operations of {@link ChordGraphicsContext}
   * @param x
   * @param y
   * @param drawable
   */
  public void render(double x, double y, Drawable drawable){
    drawable.draw(gc,originX+x,originY+y);
  }

  public void strokeText(int x, int y, String text) {
    gc.strokeText(text, originX + x, originY + y);
  }

  @FunctionalInterface
  public interface Drawable {
    void draw(GraphicsContext gc, double x, double y);
  }

  public interface DrawableRegion extends Drawable {
    double getWidth();
    double getHeight();
  }

  public GraphicsContext getGraphicsContext(){
    return gc;
  }


  /**
   * Kindly provided by https://stackoverflow.com/a/35754647
   * @param x1
   * @param y1
   * @param x2
   * @param y2
   */
  public static void strokeArrow(GraphicsContext gc, double x1, double y1, double x2, double y2){
    double ARR_SIZE = 10;

    double dx = x2 - x1, dy = y2 - y1;
    double angle = Math.atan2(dy, dx);
    int len = (int) Math.sqrt(dx * dx + dy * dy);

    Affine trans = gc.getTransform();

    Transform transform = Transform.translate(x1, y1);
    transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));
    gc.setTransform(new Affine(transform));

    gc.strokeLine(0, 0, len, 0);
    gc.fillPolygon(new double[]{len, len - ARR_SIZE, len - ARR_SIZE, len}, new double[]{0, -ARR_SIZE, ARR_SIZE, 0},
        4);

    gc.setTransform(trans);
  }


  public static void strokeTextAngled(GraphicsContext gc, String text, double x, double y, double angle, double offset){
    Affine trans = gc.getTransform();

    Transform transform = Transform.translate(x,y);
    transform = transform.createConcatenation(Transform.rotate(angle,0,0));
    gc.setTransform(new Affine(transform));

    gc.strokeText(text,offset,0);

    gc.setTransform(trans);
  }
}
