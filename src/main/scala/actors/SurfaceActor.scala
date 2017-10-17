package actors

import actors.SurfaceActor.SurfaceConfig
import actors.SurfaceMessages.BeginProcessing
import akka.actor._
import model.{Command, Mower, Position, Surface}

class SurfaceActor(val sur: Surface, initialState: SurfaceConfig) extends Actor with ActorLogging {

  val MAX_RETRY = 10

  var usedPositions = scala.collection.mutable.Map.empty[Int, Position]

  override def preStart(): Unit = {
    super.preStart()
    // TODO 2 switch to ready state
  }

  def receive = PartialFunction.empty

  def ready: Receive = {
    case BeginProcessing =>
      log.info(s"BeginProcessing...")
      initialState foreach (key => {

        // TODO 3.1 for each key (mower) create a MowerActor with the SurfaceActor in parameter

        log.debug(s"Handling actor $mowerRef")

        // TODO 3.2 for each MowerActor send ExecuteCommands message which takes the current mower and command list

      })

      // TODO 3.3 Switch to working state

  }

  def working: Receive = {
    case MowerMessages.RequestAuthorisation(currentState: Mower, newState: Mower, remainingCommands: List[Command], retry: Int) =>
      log.info(s"RequestPosition:<${newState.pos}> usedPositions:<$usedPositions> retry:<$retry>")

      // Find if another mower is in the new position
      val filterPositions = usedPositions.filterKeys(_ != currentState.id).map(_._2).filter(_ == newState.pos)

      filterPositions match {
        case Nil =>
          // TODO 4.1 no mower is present at the new position, we can send to the sender the PositionAllowed message
          usedPositions = usedPositions + (currentState.id -> newState.pos)

        case _ if retry <= MAX_RETRY =>
          log.info(s"Position <${newState.pos}> rejected!!")
          // TODO 4.2 send to sender that the PositionRejected message (think to increment retry parameter)

        case _ if retry > MAX_RETRY =>
          log.info(s"Stopping mower:<$currentState> because no attempts remaining!")
          // TODO 4.3 As we reach the end, send to sender the TerminateProcessing message
      }

      // TODO 4.4 add a case AllCommandsExecutedOn and tell to the sender to terminate the process (TerminateProcessing message)

  }

}

object SurfaceMessages {

  object PrintSystemState

  object BeginProcessing

}

object SurfaceActor {

  type SurfaceConfig = Map[Mower, List[Command]]

  def props(surface: Surface, initialState: SurfaceConfig): Props =
    Props(classOf[SurfaceActor], surface, initialState)
}
