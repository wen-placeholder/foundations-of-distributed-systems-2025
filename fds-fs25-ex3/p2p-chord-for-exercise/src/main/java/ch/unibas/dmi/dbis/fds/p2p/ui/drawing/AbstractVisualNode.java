package ch.unibas.dmi.dbis.fds.p2p.ui.drawing;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public abstract class AbstractVisualNode implements ChordGraphicsContext.Drawable {
    private final static int SIZE = 20;
    private final static int BORDER = 2;
    private final static int INNER_RADIUS = (SIZE / 2) - BORDER;

    private final static Color NODE_FILL = Color.AQUA;
    private final static Color NODE_ONLINE = Color.DARKGREEN;
    private final static Color NODE_JOINING = Color.GRAY;
    private final static Color NODE_OFFLINE = Color.DARKRED;
    private final static Color NODE_STROKE = Color.BLACK;
    private final static Color NODE_NUMBER_NOPEER = Color.SLATEGRAY;
    private final static Color NODE_NUMBER_PEERED = Color.BLACK;

    /** X and Y coordinates of the {@link AbstractVisualNode} */
    protected double x, y;

    /** The number of this {@link AbstractVisualNode}. */
    protected final int number;

    /**
     *
     * @param number
     */
    public AbstractVisualNode(int number){
        this.number = number;
    }

    /**
     *
     * @param gc
     * @param x
     * @param y
     */
    @Override
    public void draw(GraphicsContext gc, double x, double y) {
        this.x = x;
        this.y = y;
        ChordGraphicsContext cgc = new ChordGraphicsContext(gc,x,y);
        drawShape(cgc);
    }

    /**
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isInArea(double x, double y){
        return (x - this.x) * (x-this.x) + (y - this.y) * (y-this.y) <= SIZE * SIZE;
    }

    /**
     *
     * @param gc
     */
    protected void drawShape(ChordGraphicsContext gc){
        gc.getGraphicsContext().setLineWidth(1);
        gc.getGraphicsContext().setFill(NODE_FILL);
        gc.fillCircle(0,0,SIZE/2);
        if(hasPeer()) {
            // border only iff inhabited
            if (isOnline()) {
                gc.getGraphicsContext().setStroke(NODE_ONLINE);
            } else if(isJoining()){
                gc.getGraphicsContext().setStroke(NODE_JOINING);
            }else{
                gc.getGraphicsContext().setStroke(NODE_OFFLINE);
            }
            gc.getGraphicsContext().setLineWidth(BORDER);
            gc.strokeCircle(0,0,SIZE/2);
        }
        gc.getGraphicsContext().setLineWidth(1);
        gc.getGraphicsContext().setStroke(NODE_STROKE);
        Paint stroke = gc.getGraphicsContext().getStroke();
        gc.getGraphicsContext().setStroke(hasPeer() ? NODE_NUMBER_PEERED : NODE_NUMBER_NOPEER);
        gc.strokeText(-INNER_RADIUS/2,INNER_RADIUS/2,"" + number);
        gc.getGraphicsContext().setStroke(stroke);
    }

    /**
     *
     * @return
     */
    public Point2D getPosition() {
        return new Point2D(x,y);
    }

    /**
     *
     * @return
     */
    public final int getNumber() {
        return this.number;
    }

    /**
     * Returns true if this {@link VisualNode} currently has a peer and false otherwise.
     *
     * @return True if a peer is associated with this {@link VisualNode}.
     */
    abstract boolean hasPeer();

    /**
     * Returns true if the peer associated with this {@link VisualNode} is currently joining, and false otherwise.
     *
     * @return Joining state of the peer associated with this {@link VisualNode}.
     */
    abstract boolean isJoining();

    /**
     * Returns true if the peer associated with this {@link VisualNode} is currently online, and false otherwise.
     *
     * @return Online state of the peer associated with this {@link VisualNode}.
     */
    abstract boolean isOnline();
}
