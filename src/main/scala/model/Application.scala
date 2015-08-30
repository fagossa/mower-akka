package model

import actors.SurfaceActor
import actors.SurfaceMessages.BeginProcessing
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object Application extends App {
  val surface = Surface(Position(5, 5))

  val commands: Map[Mower, List[Command]] = Map(
    Mower(1, surface, pos = Position(1, 2), ori = North) -> List(Left, Forward, Left, Forward, Left, Forward, Left, Forward, Forward)
    ,
    Mower(2, surface, pos = Position(3, 3), ori = East) -> List(Forward, Forward, Right, Forward, Forward, Right, Forward, Right, Right, Forward)
  )

  val config = ConfigFactory.load()
  val system = ActorSystem("system", config)
  val surfaceRef = system.actorOf(SurfaceActor.props(surface, commands), "MySurfaceActor")
  surfaceRef ! BeginProcessing

  system.registerOnTermination {
    System.exit(1)
  }
}
