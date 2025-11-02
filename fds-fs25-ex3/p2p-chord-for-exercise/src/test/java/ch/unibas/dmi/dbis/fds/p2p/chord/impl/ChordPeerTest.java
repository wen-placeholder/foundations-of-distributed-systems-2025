package ch.unibas.dmi.dbis.fds.p2p.chord.impl;

import static ch.unibas.dmi.dbis.fds.p2p.chord.Utils.NUMBER_OF_BYTES;

import ch.unibas.dmi.dbis.fds.p2p.chord.api.data.Identifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class ChordPeerTest {

  private ChordNetwork network;
  private ChordPeer n0, n1, n3;


  @BeforeEach
  public void setupNetwork(){
    network = new ChordNetwork(NUMBER_OF_BYTES,false);
    n0 = network.createChordPeer(0);
    n1 = network.createChordPeer(1);
    n3 = network.createChordPeer(3);


    n0.join(null);
    n1.join(n0);
    n3.join(n0);

    Assertions.assertFalse(network.isDynamic());
  }

  @Test
  public void testFingerTableNodeZero(){
    Identifier fingerOneSuccessor = n0.finger().node(1).get().getIdentifier();
    Identifier fingerTwoSuccessor = n0.finger().node(2).get().getIdentifier();
    Identifier fingerThreeSuccessor = n0.finger().node(3).get().getIdentifier();

    Assertions.assertEquals(1,fingerOneSuccessor.getIndex());
    Assertions.assertEquals(3,fingerTwoSuccessor.getIndex());
    Assertions.assertEquals(0,fingerThreeSuccessor.getIndex());
  }

  @Test
  public void testFingerTableNodeOne(){
    Identifier fingerOneSuccessor = n1.finger().node(1).get().getIdentifier();
    Identifier fingerTwoSuccessor = n1.finger().node(2).get().getIdentifier();
    Identifier fingerThreeSuccessor = n1.finger().node(3).get().getIdentifier();

    Assertions.assertEquals(3,fingerOneSuccessor.getIndex());
    Assertions.assertEquals(3,fingerTwoSuccessor.getIndex());
    Assertions.assertEquals(0,fingerThreeSuccessor.getIndex());
  }

  @Test
  public void testJoinOnThree(){
    ChordPeer node6 = network.createChordPeer(6);
    node6.join(n3);

    // Checking FT of node 6
    Identifier fingerOneSuccessor = node6.finger().node(1).get().getIdentifier();
    Identifier fingerTwoSuccessor = node6.finger().node(2).get().getIdentifier();
    Identifier fingerThreeSuccessor = node6.finger().node(3).get().getIdentifier();

    Assertions.assertEquals(0, fingerOneSuccessor.getIndex());
    Assertions.assertEquals(0, fingerTwoSuccessor.getIndex());
    Assertions.assertEquals(3, fingerThreeSuccessor.getIndex());

    // Checking FT of node 3
    fingerOneSuccessor = n3.finger().node(1).get().getIdentifier();
    fingerTwoSuccessor = n3.finger().node(2).get().getIdentifier();
    fingerThreeSuccessor = n3.finger().node(3).get().getIdentifier();

    Assertions.assertEquals(6, fingerOneSuccessor.getIndex());
    Assertions.assertEquals(6, fingerTwoSuccessor.getIndex());
    Assertions.assertEquals(0, fingerThreeSuccessor.getIndex());

    // Checking FT of node 1
    fingerOneSuccessor = n1.finger().node(1).get().getIdentifier();
    fingerTwoSuccessor = n1.finger().node(2).get().getIdentifier();
    fingerThreeSuccessor = n1.finger().node(3).get().getIdentifier();

    Assertions.assertEquals(3, fingerOneSuccessor.getIndex());
    Assertions.assertEquals(3, fingerTwoSuccessor.getIndex());
    Assertions.assertEquals(6, fingerThreeSuccessor.getIndex());

    // Checking FT of node 0

    fingerOneSuccessor = n0.finger().node(1).get().getIdentifier();
    fingerTwoSuccessor = n0.finger().node(2).get().getIdentifier();
    fingerThreeSuccessor = n0.finger().node(3).get().getIdentifier();

    Assertions.assertEquals(1, fingerOneSuccessor.getIndex());
    Assertions.assertEquals(3, fingerTwoSuccessor.getIndex());
    Assertions.assertEquals(6, fingerThreeSuccessor.getIndex());
  }





}
