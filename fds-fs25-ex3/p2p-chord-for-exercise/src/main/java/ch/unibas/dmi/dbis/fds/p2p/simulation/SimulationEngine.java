package ch.unibas.dmi.dbis.fds.p2p.simulation;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import ch.unibas.dmi.dbis.fds.p2p.chord.api.AbstractChordPeer;
import ch.unibas.dmi.dbis.fds.p2p.chord.impl.ChordNetwork;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A simulation engine to run and test a {@link ChordNetwork}.
 *
 * @author Loris Sauter
 */
public class SimulationEngine implements SimulationEventListener, Runnable {

    /** Logger used to log errors. */
    private static final Logger LOGGER = LogManager.getLogger();
    /** */
    private final LinkedBlockingQueue<SimulationEvent> eventBuffer;

    /** */
    private final ObservableList<SimulationEvent> allEvents = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

    /** The pace at which the simulation is currently paused. */
    private final SimpleDoubleProperty pace = new SimpleDoubleProperty();

    /** The current step in the simulation. */
    private final AtomicLong simulationStep = new AtomicLong(0);

    /** The number of {@link SimulationEvent}s to keep in the log. */
    private final int keepEvents;

    /** The {@link ChordNetwork} that is being simulated. */
    private final SimulationNetwork network;

    /** Flag indicating whether the simulation is has been paused. */
    private volatile boolean paused = true;

    /** Flag indicating whether the simulation is has been interrupted. Once it has been interrupted in cannot be resumed! */
    private volatile boolean interrupted = false;

    /**
     * Constructor for {@link SimulationEngine}.
     *
     * @param nbits Number of bits to create the {@link ChordNetwork}. Equals the value <strong>m</strong> in the paper.
     * @param dynamic Whether or not a dynamic {@link ChordNetwork} should be simulated.
     * @param keepEvents The number of {@link SimulationEvent}s to keep in the log.
     */
    public SimulationEngine(int nbits, boolean dynamic, int keepEvents) {
        this.network = new SimulationNetwork(nbits, dynamic, this);
        this.eventBuffer = new LinkedBlockingQueue<>();
        this.keepEvents = keepEvents;
    }

    /**
     * Constructor for {@link SimulationEngine}.
     *
     * @param nbits Number of bits to create the {@link ChordNetwork}. Equals the value <strong>m</strong> in the paper.
     * @param dynamic Whether or not a dynamic {@link ChordNetwork} should be simulated.
     */
    public SimulationEngine(int nbits, boolean dynamic) {
        this(nbits, dynamic, 1000);
    }

    /**
     * The run method that executes the actual simulation.
     */
    public void run() {
        try {
            initNetwork();
            while (!this.interrupted) {
                if (this.paused) {
                    Thread.sleep(500);
                } else {
                    /* Progress simulation by one. */
                    this.next();

                    /* Now sleep according to pace value (at least the 100ms). */
                    Thread.sleep(pace.intValue() + 100);
                }
            }
        }
        catch (InterruptedException e) {
            LOGGER.log(Level.WARN, "Chord simulation was terminated forcefully.");
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, "Chord simulation crashed with an exception.", e);
        }
    }

    /**
     * Accessor to the underlying {@link SimulationNetwork}. Only for simulation and display purposes!
     *
     * @return {@link SimulationNetwork}
     */
    public SimulationNetwork getNetwork() {
        return this.network;
    }

    /**
     *
     * @param value
     */
    public void bindPace(DoubleProperty value) {
        pace.bind(new SimpleDoubleProperty(1).subtract(value).multiply(1000));
    }

    /**
     * Returns the current step in the simulation run by this {@link SimulationEngine}. Only for simulation and display purposes!
     *
     * @return Current simulation step.
     */
    public long getSimulationStep() {
        return this.simulationStep.get();
    }

    /**
     * Called whenever the user decided that an idle node should be joining. Creates a new {@link SimulationPeer} for that node.
     *
     * @param nodeIndex The nodeIndex of the {@link SimulationPeer} that should be leaving.
     */
    public synchronized void nodeJoining(int nodeIndex) {
        final SimulationPeer nprime = this.network.getRandomPeer(); /* Important: First select nprime; otherwise getRandomPeer() might return joining node. */
        final SimulationPeer n = this.network.createChordPeer(nodeIndex);
        n.join(nprime);
    }

    /**
     * Called whenever the user decided that a {@link SimulationPeer} should be leaving.
     *
     * @param nodeIndex The nodeIndex of the {@link SimulationPeer} that should be leaving.
     */
    public synchronized void nodeLeaving(int nodeIndex) {
        this.network.remove(nodeIndex);
    }

    /**
     * Called whenever a user wants to store data in the {@link SimulationNetwork}.
     *
     * @param nodeIndex The nodeIndex of the {@link SimulationPeer} that has received the request.
     * @param key The key of the data item.
     * @param value The value of the data item.
     */
    public synchronized void storeData(int nodeIndex, String key, String value) {
        this.network.getSimulationPeer(nodeIndex).ifPresent(p -> p.store(null, key, value));

    }

    /**
     * Called whenever a user wants to retrieve data from the {@link SimulationNetwork}.
     *
     * @param nodeIndex The nodeIndex of the {@link SimulationPeer} that has received the request.
     * @param key The key of the data item.
     */
    public synchronized Optional<String> lookupData(int nodeIndex, String key) {
        return this.network.getSimulationPeer(nodeIndex).flatMap(p -> p.lookup(null, key));
    }

    /**
     * Toggles the simulation.
     */
    public synchronized void toggle() {
        this.paused = !this.paused;
    }

    /**
     * Interrupts the simulation.
     */
    public synchronized void interrupt() {
        this.interrupted = true;
    }

    /**
     * Returns the pause status of this {@link SimulationEngine}.
     *
     * @return True if simulation has been paused, false otherwise.
     */
    public synchronized boolean isPaused() {
        return this.paused;
    }

    /**
     * Provides access to all {@link SimulationEvent}s that have occurred in the lifetime of this
     * {@link SimulationEngine}. Only up to {@link SimulationEngine#keepEvents} items are kept!
     *
     * @return {@link ObservableList} of {@link SimulationEvent}s.
     */
    public ObservableList<SimulationEvent> allEvents() {
        return allEvents;
    }

    /**
     * Provides access to the {@link SimulationEvent}s that have occurred in the current simulation cycle.
     *
     * @return Collection of current {@link SimulationEvent}s.
     */
    public Collection<SimulationEvent> eventBuffer() {
        return Collections.unmodifiableCollection(this.eventBuffer);
    }

    /**
     * Handles a simulation event.
     * Simulation events are independetly created, but do have a dependency and ultimately form
     * an event chain. (e.g. 0.FIND_SUCCESOR(3) -> 0.FIND_PREDECESSOR(3) -> 0.CLOSEST_PRECEDING_FINGER(3) -> 2.CLOSEST_PRECEDING_FINGER(3) ...)
     * Thus, those dependencies must be managed.
     * We differentiate between TopLevelEvents TLE and FollowUpEvents FUE:
     * TLEs: find_succ (notify, join), as all calls to fix or stabilize firstly result in a find_succ
     * FUE:  find_pred, closest_prec_fing
     * While notify is treated specially, as it has no FUE.
     * Another special case is closest_preceding_finger:
     * It might be called from different nodes, but always with the same argument,
     * thus if a TLE is called, each FUE with the same source is treated as part of a dependency chain,
     * within the same chain there might be multiple closest_preceding_finger calls, but all with the same
     * argument.
     *
     * @param event
     */
    @Override
    public void handle(SimulationEvent event) {
        this.eventBuffer.add(event);
        event.setWhen(this.simulationStep.get());
        Platform.runLater(() -> {
            this.allEvents.add(event);
            if (this.allEvents.size() > this.keepEvents) {
                this.allEvents.remove(0, 25);
            }
        });
    }

    /**
     * Transitions to the next simulation step.
     */
    private void next() {
        /* Clear the event buffer and increment counter to prepare for next iteration. */
        this.simulationStep.incrementAndGet();
        this.eventBuffer.clear();

        /* Dynamic network simulation; call stabilize,  */
        if (this.network.isDynamic()) {
            this.network.getRandomPeer().stabilize();

            /* Randomly fix fingers on a peer. */
            this.network.getRandomPeer().fixFingers();

            /* Randomly check predecessor of one peer. */
            this.network.getRandomPeer().checkPredecessor();

            /* Randomly check predecessor of one peer. */
            this.network.getRandomPeer().checkSuccessor();
        }
    }


    /**
     * Initializes the {@link SimulationEngine}s {@link ChordNetwork} with three default peers according to the paper.
     */
    private void initNetwork() {
        final SimulationPeer peer1 = network.createChordPeer(0);
        final SimulationPeer peer2 = network.createChordPeer(1);
        final SimulationPeer peer3 = network.createChordPeer(3);

        /* Let peers join. */
        peer1.join(null);
        peer2.join(peer1);
        peer3.join(peer1);

        /* Clear buffers from joining actions. */
        this.eventBuffer.clear();
        Platform.runLater(this.allEvents::clear);
    }
}
