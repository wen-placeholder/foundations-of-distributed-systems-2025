package ch.unibas.dmi.dbis.fds.p2p.simulation;

import ch.unibas.dmi.dbis.fds.p2p.simulation.SimulationEvent.EventType;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
@FunctionalInterface
public interface SimulationEventListener {

  void handle(SimulationEvent event);

}
