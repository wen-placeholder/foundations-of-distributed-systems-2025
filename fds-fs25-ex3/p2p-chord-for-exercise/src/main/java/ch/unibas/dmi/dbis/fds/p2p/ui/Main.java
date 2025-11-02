package ch.unibas.dmi.dbis.fds.p2p.ui;

/**
 * Workaround entrypoint for an issue that arises when starting application directly with {@link ChordApplication}
 * and not having OpenJFX in the classpath.
 *
 * @version 1.0
 * @author Ralph Gasser
 */
public class Main {
  public static void main(String[] args) {
    /* Launch application. */
    ChordApplication.main(args);
  }
}
