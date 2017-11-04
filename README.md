[![License][license-badge]][license-url]

# Mower-akka 

A simple implementation of the mower scenario using akka and scala 

## I need help!

The [solution](https://github.com/fagossa/mower-akka/tree/solution) branch could be useful.

[license-badge]: https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square
[license-url]: LICENSE.txt

## Track

### TODO 1
Complete [MowerActor](./src/main/scala/actors/MowerActor.scala).
Once done, [MowerActorSpecs](./src/test/scala/model/MowerActorSpec.scala) should pass.

#### TODO 1.1
Complete `MowerActor`'s companion object `props()` method. The goal is to create a `Propos` instance with the arguments of the actor class's constructor.
See [doc on props](https://doc.akka.io/docs/akka/current/scala/actors.html#props)

##### What are props ?
`Props` instance is a memoization of the your business actor class constructor arguments.

##### What is the use of props ?
Once a `Props` instance is created, the actor system remembers the arguments needed to build your business actor class.

Thus, it is able to trash it an restart it as many times as the actor's lifecycle need it: the same original arguments are passed to your actor constructor each time it is called.

You will see when thereafter that you never create yourself an instance of your actor class;
instead, you tell the actor system its props, and then the actor system create it.

#### TODO 1.2
You need to have this code compiling. See comments from 1.2.* todos in `MowerActor.ready` method.

Goal: pattern match the received message to choose what to do.

### TODO 2
We want the [SurfaceActor](./src/main/scala/actors/SurfaceActor.scala) to listen for message using `ready` method; because this is the starting state.

See the ["become" doc](https://doc.akka.io/docs/akka/current/scala/actors.html#become-unbecome) to see how to change the method that is receiving messages.

#### Why become ?
`become` word implies a state change. In he actor model, you can think of the state machine matrix as an UML's state/transition diagram:
* some message makes your actor to transition to a new state
* each state is able to receive a specific set of messages

Thus `become` will be useful elsewhere in the code...

### TODO 3: SurfaceActor's ready state
Fill in the missing code to 
* iterate through surface config keys. The keys are Mowers, values are the list of commands Mower as key must execute.
* for each Mower key, start a MowerActor and send an `ExecuteCommands` message with the list of commands
* then switch to working state

### TODO 4:  SurfaceActor's working state
In working state, `SurfaceActor` will receive request from `MowerActor`s. It goal is to answer to `MowerActor` if it can go forward (the requested position on surface is free) or not (the requested position is occupied by another Mower).

This is where collisions are managed.

Finding if next position is free or not is given. You just need to implement
* what to do if the result is "free"
* what do to if the result is "occupied"
* what to do if the result is occupied and we tried it to much time

See help in TODO 4.* comments

### TODO 5: Let's try it
See the doc about [creating an actor with props](https://doc.akka.io/docs/akka/current/scala/actors.html#creating-actors-with-props) to create your actor system, your `SurfaceActor` and start it.

Ensure that log levels are properly configured to see the system live.
