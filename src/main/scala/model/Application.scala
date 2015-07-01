package model

import actors.SurfaceActor
import actors.SurfaceMessages.BeginProcessing
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object Application extends App {
  val surface = Surface(Dimension(5, 5))

  val commands: Map[Mower, List[Command]] = Map(
    Mower(surface, pos = Position(0, 0), ori = North) -> List(Right, Forward, Left, Forward, Forward)
    ,
    Mower(surface, pos = Position(3, 3), ori = South) -> List(Left, Forward, Right, Forward, Forward, Left)
  )

  val config = ConfigFactory.load()
  val system = ActorSystem("system", config)
  val surfaceRef = system.actorOf(SurfaceActor.props(surface, commands), "MySurfaceActor")
  surfaceRef ! BeginProcessing

  //surfaceRef ! TerminateProcessing

  system.registerOnTermination {
    System.exit(1)
  }
}
