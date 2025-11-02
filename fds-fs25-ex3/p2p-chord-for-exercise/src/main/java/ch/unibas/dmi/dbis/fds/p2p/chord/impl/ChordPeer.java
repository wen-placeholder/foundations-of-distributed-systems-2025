package ch.unibas.dmi.dbis.fds.p2p.chord.impl;

import ch.unibas.dmi.dbis.fds.p2p.chord.api.*;
import ch.unibas.dmi.dbis.fds.p2p.chord.api.ChordNetwork;
import ch.unibas.dmi.dbis.fds.p2p.chord.api.data.Identifier;
// import ch.unibas.dmi.dbis.fds.p2p.chord.api.data.IdentifierCircle;
import ch.unibas.dmi.dbis.fds.p2p.chord.api.data.IdentifierCircularInterval;
import ch.unibas.dmi.dbis.fds.p2p.chord.api.math.CircularInterval;

import static ch.unibas.dmi.dbis.fds.p2p.chord.api.data.IdentifierCircularInterval.createLeftOpen;

import java.util.Map;
import java.util.Random;

/**
* @author loris.sauter
*/
public class ChordPeer extends AbstractChordPeer {

    /** Random for fixFingers */
    private final Random rng = new Random();

    /**
    *
    * @param identifier
    * @param network
    */
    protected ChordPeer(Identifier identifier, ChordNetwork network) {
        super(identifier, network);
    }
    
    /**
    * Asks this {@link ChordNode} to find {@code id}'s successor {@link ChordNode}.
    *
    * Defined in [1], Figure 4
    *
    * @param caller The calling {@link ChordNode}. Used for simulation - not part of the actual chord definition.
    * @param id The {@link Identifier} for which to lookup the successor. Does not need to be the ID of an actual {@link ChordNode}!
    * @return The successor of the node {@code id} from this {@link ChordNode}'s point of view
    */
    @Override
    public ChordNode findSuccessor(ChordNode caller, Identifier id) {
        ChordNode n0 = this.findPredecessor(this, id);
        return n0.successor();
    }
    
    /**
    * Asks this {@link ChordNode} to find {@code id}'s predecessor {@link ChordNode}
    *
    * Defined in [1], Figure 4
    *
    * @param caller The calling {@link ChordNode}. Used for simulation - not part of the actual chord definition.
    * @param id The {@link Identifier} for which to lookup the predecessor. Does not need to be the ID of an actual {@link ChordNode}!
    * @return The predecessor of or the node {@code of} from this {@link ChordNode}'s point of view
    */
    @Override
    public ChordNode findPredecessor(ChordNode caller, Identifier id) {        
        ChordNode n = this;
        
        // while id ∉ (n, n.successor]
        while (true) {
            ChordNode succ = n.successor();
            boolean inLeftOpen = IdentifierCircularInterval.createLeftOpen(n.id(), succ.id()).contains(id);
            if (inLeftOpen) {
                return n;
            } else {
                ChordNode next = n.closestPrecedingFinger(n, id);
                n = next;
            }
        }
    }
    
    /**
    * Return the closest finger preceding the  {@code id}
    *
    * Defined in [1], Figure 4
    *
    * @param caller The calling {@link ChordNode}. Used for simulation - not part of the actual chord definition.
    * @param id The {@link Identifier} for which the closest preceding finger is looked up.
    * @return The closest preceding finger of the node {@code of} from this node's point of view
    */
    @Override
    public ChordNode closestPrecedingFinger(ChordNode caller, Identifier id) {
        if (this.status() == NodeStatus.OFFLINE) return null;
        
        int m = getNetwork().getNbits();
        
        // Search fingers in reverse order
        for (int i = m; i >= 1; i--) {
            ChordNode fingerNode = this.finger().node(i).orElse(null);
            
            // Check if finger lies in (self, id)
            if (IdentifierCircularInterval.createOpen(this.id(), id).contains(fingerNode.id())) {
                return fingerNode;
            }
        }
        
        // No suitable finger, return self
        return this;
    }
    
    /**
    * Called on this {@link ChordNode} if it wishes to join the {@link ChordNetwork}. {@code nprime} references another {@link ChordNode}
    * that is already member of the {@link ChordNetwork}.
    *
    * Required for static {@link ChordNetwork} mode. Since no stabilization takes place in this mode, the joining node must make all
    * the necessary setup.
    *
    * Defined in [1], Figure 6
    *
    * @param nprime Arbitrary {@link ChordNode} that is part of the {@link ChordNetwork} this {@link ChordNode} wishes to join.
    */
    @Override
    public void joinAndUpdate(ChordNode nprime) {
        if (nprime != null) {
            initFingerTable(nprime);
            updateOthers();
            // Move / copy keys: see discussion; copy-only with current public API
            redistributeKeys();
        } else {
            for (int i = 1; i <= getNetwork().getNbits(); i++) {
                this.fingerTable.setNode(i, this);
            }
            this.setPredecessor(this);
        }
    }

    /** Copy-only redistribution with current API (cannot delete source without local API). */
    private void redistributeKeys() {
        ChordNode succ = this.successor();
        if (succ == null || succ == this) {
            return; // nothing to do
        }

        Map<String, String> succData = ((AbstractChordPeer) succ).dump();

        for (Map.Entry<String, String> entry : succData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // Determine the correct owner after join
            ChordNode owner = this.lookupNodeForItem(key);

            // If this node should be the owner, copy key from successor → this
            if (owner == this) {
                this.store(null, key, value); // routes but owner is now 'this'
            }
        }
    }

    
    /**
    * Called on this {@link ChordNode} if it wishes to join the {@link ChordNetwork}. {@code nprime} references
    * another {@link ChordNode} that is already member of the {@link ChordNetwork}.
    *
    * Required for dynamic {@link ChordNetwork} mode. Since in that mode {@link ChordNode}s stabilize the network
    * periodically, this method simply sets its successor and waits for stabilization to do the rest.
    *
    * Defined in [1], Figure 7
    *
    * @param nprime Arbitrary {@link ChordNode} that is part of the {@link ChordNetwork} this {@link ChordNode} wishes to join.
    */
    @Override
    public void joinOnly(ChordNode nprime) {
        setPredecessor(null);
        if (nprime == null) {
            this.fingerTable.setNode(1, this);
        } else {
            this.fingerTable.setNode(1, nprime.findSuccessor(this,this));
        }
    }
    
    /**
    * Initializes this {@link ChordNode}'s {@link FingerTable} based on information derived from {@code nprime}.
    *
    * Defined in [1], Figure 6
    *
    * @param nprime Arbitrary {@link ChordNode} that is part of the network.
    */
    private void initFingerTable(ChordNode nprime) {
        int m = getNetwork().getNbits();
        IdentifierCircle<Identifier> circle = getNetwork().getIdentifierCircle();

        // Step 1: finger[1].node = n'.findSuccessor(start[1])
        Identifier start1 = circle.getIdentifierAt(this.finger().start(1));
        ChordNode succ1 = nprime.findSuccessor(this, start1);
        this.fingerTable.setNode(1, succ1);

        // Step 2: predecessor = successor.predecessor; successor.predecessor = this
        ChordNode succ = this.successor();
        this.setPredecessor(succ.predecessor());
        this.successor().setPredecessor(this);

        // Step 3: for i = 1 .. m-1
        for (int i = 1; i < m; i++) {
            Identifier startNext = circle.getIdentifierAt(this.finger().start(i + 1));
            ChordNode fi = this.finger().node(i).orElse(null);

            // if start[i+1] ∈ [n, finger[i])
            boolean inRange =
                IdentifierCircularInterval
                    .createRightOpen(this.id(), fi.id())
                    .contains(startNext);

            if (inRange) {
                this.fingerTable.setNode(i + 1, fi);
            } else {
                Identifier start = getNetwork().getIdentifierCircle().getIdentifierAt(this.finger().start(i + 1));
                this.fingerTable.setNode(i + 1, nprime.findSuccessor(this, start));
            }
        }
    }
    
    /**
    * Updates all {@link ChordNode} whose {@link FingerTable} should refer to this {@link ChordNode}.
    *
    * Defined in [1], Figure 6
    */
    private void updateOthers() {
        int m = getNetwork().getNbits();

        for (int i = 1; i <= m; i++) {
            int offset = (int) Math.pow(2, i - 1);
            int idIndex = this.id().getIndex() - offset;

            Identifier id = getNetwork().getIdentifierCircle().getIdentifierAt(idIndex);

            // Must lookup using the old network, not the new node
            ChordNode p = this.findPredecessor(this, id);

            // 
            if (p.successor().id().equals(id)) {
                p = p.successor();
            }
            p.updateFingerTable(this, i);
        }
    }
    
    /**
    * If node {@code s} is the i-th finger of this node, update this node's finger table with {@code s}
    *
    * Defined in [1], Figure 6
    *
    * @param s The should-be i-th finger of this node
    * @param i The index of {@code s} in this node's finger table
    */
    @Override
    public void updateFingerTable(ChordNode s, int i) {
        finger().node(i).ifPresent(node -> {
        if (createLeftOpen(this.id(), node.id()).contains(s.id())) {
            this.fingerTable.setNode(i, s);
            ChordNode p = this.predecessor();
            p.updateFingerTable(s, i);
            }
        });
    }
    
    /**
    * Called by {@code nprime} if it thinks it might be this {@link ChordNode}'s predecessor. Updates predecessor
    * pointers accordingly, if required.
    *
    * Defined in [1], Figure 7
    *
    * @param nprime The alleged predecessor of this {@link ChordNode}
    */
    @Override
    public void notify(ChordNode nprime) {
        if (this.status() == NodeStatus.OFFLINE || this.status() == NodeStatus.JOINING) return;

        ChordNode pred = this.predecessor();
        if (pred == null) {
            this.setPredecessor(nprime);
            return;
        }

        // if nprime ∈ (predecessor, this)
        boolean inOpen = IdentifierCircularInterval.createOpen(pred.id(), this.id()).contains(nprime.id());
        if (inOpen) {
            this.setPredecessor(nprime);
        }
    }
    
    /**
    * Called periodically in order to refresh entries in this {@link ChordNode}'s {@link FingerTable}.
    *
    * Defined in [1], Figure 7
    */
    @Override
    public void fixFingers() {
        if (this.status() == NodeStatus.OFFLINE || this.status() == NodeStatus.JOINING) return;

        int m = getNetwork().getNbits();
        if (m <= 1) return;

        // pick random index i > 1
        int i = 2 + rng.nextInt(Math.max(1, m - 1)); // range [2, m]
        int startIdx = this.finger().start(i);
        Identifier startId = getNetwork().getIdentifierCircle().getIdentifierAt(startIdx);

        ChordNode s = this.findSuccessor(this, startId);
        if (s != null) {
            this.fingerTable.setNode(i, s);
        }
    }
    
    /**
    * Called periodically in order to verify this node's immediate successor and inform it about this
    * {@link ChordNode}'s presence,
    *
    * Defined in [1], Figure 7
    */
    @Override
    public void stabilize() {
        if (this.status() == NodeStatus.OFFLINE || this.status() == NodeStatus.JOINING) return;

        ChordNode succ = this.successor();
        // x = successor.predecessor
        ChordNode x = succ.predecessor();
        if (x != null ) {
            boolean inOpen = IdentifierCircularInterval.createOpen(this.id(), succ.id()).contains(x.id());
            if (inOpen) {
                this.fingerTable.setNode(1, x); // successor = x
                succ = x;
            }
        }

        // successor.notify(n)
        succ.notify(this);
    }

    /** Helper: try to recover a missing/offline successor from known references. */
    private void recoverSuccessor() {
        // try predecessor
        ChordNode pred = this.predecessor();
        if (pred != null && pred != this && pred.status() == NodeStatus.ONLINE) {
            ChordNode s = pred.findSuccessor(this, this);
            if (s != null) {
                this.fingerTable.setNode(1, s);
                return;
            }
        }
        // try any finger
        int m = getNetwork().getNbits();
        for (int i = 2; i <= m; i++) {
            ChordNode f = this.finger().node(i).orElse(null);
            if (f != null && f != this && f.status() == NodeStatus.ONLINE) {
                ChordNode s = f.findSuccessor(this, this);
                if (s != null) {
                    this.fingerTable.setNode(1, s);
                    return;
                }
            }
        }
        // fallback to self
        this.fingerTable.setNode(1, this);
    }
    
    /**
    * Called periodically in order to check activity of this {@link ChordNode}'s predecessor.
    *
    * Not part of [1]. Required for dynamic network to handle node failure.
    */
    @Override
    public void checkPredecessor() {
        if (this.status() == NodeStatus.OFFLINE || this.status() == NodeStatus.JOINING) return;

        ChordNode pred = this.predecessor();
        if (pred == null || pred == this) return;

        if (pred.status() == NodeStatus.OFFLINE) {
            this.setPredecessor(null);
        }
    }
    
    /**
    * Called periodically in order to check activity of this {@link ChordNode}'s successor.
    *
    * Not part of [1]. Required for dynamic network to handle node failure.
    */
    @Override
    public void checkSuccessor() {
        if (this.status() == NodeStatus.OFFLINE || this.status() == NodeStatus.JOINING) return;

        ChordNode succ = this.successor();
        if (succ == null || succ.status() == NodeStatus.OFFLINE) {
            recoverSuccessor();
        }
    }
    
    /**
    * Performs a lookup for where the data with the provided key should be stored.
    *
    * @return Node in which to store the data with the provided key.
    */
    @Override
    protected ChordNode lookupNodeForItem(String key) {
        if (this.status() == NodeStatus.OFFLINE || this.status() == NodeStatus.JOINING) {
            return null;
        }
        
        // 1. Hash the key and convert to Identifier
        int hash = getNetwork().getHashFunction().hash(key);
        int index = hash % (int) Math.pow(2, getNetwork().getNbits());
        Identifier keyId = getNetwork().getIdentifierCircle().getIdentifierAt(index);
        
        // 2. If no predecessor (single-node network), this node handles all keys
        ChordNode pred = this.predecessor();
        if (pred == null || pred == this) {
            return this;
        }
        
        int predIdx = pred.id().getIndex();
        int selfIdx = this.id().getIndex();
        int keyIdx = keyId.getIndex();
        
        // 3. Check if key is in (pred, this]
        boolean inRange = CircularInterval.createLeftOpen(predIdx, selfIdx).contains(keyIdx);
        
        if (inRange) {
            return this;
        }
        
        // 4. Otherwise route the query using Chord
        return this.findSuccessor(this, keyId);
    }
}
