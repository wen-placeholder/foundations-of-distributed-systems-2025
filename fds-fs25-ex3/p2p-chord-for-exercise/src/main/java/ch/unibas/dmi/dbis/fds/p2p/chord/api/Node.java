package ch.unibas.dmi.dbis.fds.p2p.chord.api;

import java.util.Optional;
import java.util.Set;

/**
 * A simple {@link Node} as used in an arbitrary network. Exposes simple primitives such as joining and leaving and storing + looking up data.
 *
 * @author Ralph Gasser
 */
public interface Node {
    /**
     * Saves a a piece of data identified by the provided key in the network storage. The assumption behind this method is, that it can be
     * invoked on any node within the network. Resolving the node that might ultimately hold the data is up to the implementation.
     *
     * @param origin the node calling the method (purely for logging purposes). Null if it's the client (i.e., not a node in the network)
     * @param key of data item
     * @param value of data item
     */
    void store(Node origin, String key, String value );

    /**
     * Retrieves a piece of data identified by the provided key from the network storage.  The assumption behind this method is, that it can be
     * invoked on any node within the network. Resolving the node that might ultimately hold the data is up to the implementation.
     *
     * @param origin Origin of the request. Null if the query comes from client otherwise the first peer in the network
     * @param key Key of data item
     * @return value of data item identified by the key.
     */
    Optional<String> lookup(Node origin, String key);

    /**
     * Removes a piece of data identified by the provided key from the local storage. The data deleted is returned by this method.
     *
     * @param origin Origin of the request. Null if the query comes from client otherwise the first peer in the network
     * @param key    Key of data item
     * @return Value of data item identified by the key.
     */
    Optional<String> delete(Node origin, String key);

    /**
     * Returns a set of keys held by this {@link Node}.
     *
     * @return Set of keys held by this {@link Node}
     */
    Set<String> keys();

    /**
     * Causes the {@link Node} to join the network. Changes its {@link NodeStatus} to online. A {@link Node} can only join a single network.
     *
     * @param node The {@link Node} that assists the joining {@link Node} in joining. May be null!
     */
    void join(Node node);

    /**
     * Causes the {@link Node} to leave the network. Changes its {@link NodeStatus} to offline.
     */
    void leave();

    /**
     * Current status of the {@link Node} with respect to the network it belongs to.
     *
     * @return {@link NodeStatus} of the current {@link Node}.
     */
    NodeStatus status();
}
