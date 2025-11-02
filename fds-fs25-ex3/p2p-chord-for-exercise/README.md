# P2P Chord

This is an implementation of the Chord protocol. The Chord protocol is a scalable peer-to-peer lookup protocol by Stoica, Morris, Karger, Kaashoek and Balakrishnan.
For further information see http://pdos.lcs.mit.edu/chord/ (which forwards to: https://github.com/sit/dht/wiki)

This particular implementation is tailored as the exercise for the course _Foundations of Distributed Systems_ at the University of Basel.
The goal of the exercise is to implement a Chord-based lookup algorithm.

While this implementation is tailored for educational usage, it provides the full solution (for the moment).

This implementation is courtesy of the [Databases and Information Systems Group (DBIS)](https://dbis.dmi.unibas.ch ) at the University of Basel. 


## Requirements / Compatibility
We recommend Java 17 to run the Chord simulation, because of its reliance on Open JFX 17.0.2 (see [openjfx.io](https://openjfx.io/openjfx-docs/#introduction)). If you want to run the simulation with another Java version, please adjust the Open JFX dependency accordingly!

**Important:** Please use the class `Main` as entrypoint to run the application. If you use `ChordApplication` directly, you might run into issues due to Open JFX not being in the classpath upon launch.

## Simulator usage

The simulator can be started as follows:

`java -jar p2p-1.0-SNAPSHOT.jar --bits=<number of bits> --dynamic=<true|false>`

The following parameters can be used:

* --bits: The number of bits to use for the Chord-Ring (*m* in paper, defaults to 3)
* --dynamic: Whether or not to use stabilization for simulation (defaults to *false*)