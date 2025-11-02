package ch.unibas.dmi.dbis.fds.p2p.ui.drawing;


/**
 * A dummy implementation of the {@link AbstractVisualNode} for visualization in the help dialog.
 */
public final class VisualDummyNode extends AbstractVisualNode {

    /** Indicates whether or not a peer is associated with this {@link VisualDummyNode}. */
    private final boolean peer;

    /** Indicates whether the peer associated with this {@link VisualDummyNode} is currently joining. */
    private final boolean joining;

    /**
     * Constructor for {@link VisualDummyNode}.
     *
     * @param number The number of this {@link VisualDummyNode}
     * @param peer Whether or not a peer is associated with this {@link VisualDummyNode}.
     * @param joining If the peer associated with this {@link VisualDummyNode} is joining. Can only be true if peer is true.
     */
    public VisualDummyNode(int number, boolean peer, boolean joining) {
        super(number);
        this.peer = peer;
        this.joining = joining && peer;
    }

    @Override
    boolean hasPeer() {
        return this.peer;
    }

    @Override
    boolean isOnline() {
        return this.peer && !this.joining;
    }

    @Override
    boolean isJoining() {
        return this.joining;
    }
}
