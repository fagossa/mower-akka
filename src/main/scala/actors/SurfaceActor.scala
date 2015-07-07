package actors

import java.util.UUID

import actors.SurfaceActor.SurfaceConfig
import actors.SurfaceMessages.BeginProcessing
import akka.actor._
import model.{Command, Mower, Position, Surface}

class SurfaceActor(val sur: Surface, initialState: SurfaceConfig) extends Actor with Stash with ActorLogging {

  val MAX_RETRY = 10

  var children = Set.empty[ActorRef]

  var usedPositions = Set.empty[Position]

  override def preStart(): Unit = {
    super.preStart()
    usedPositions = usedPositions.empty
    context become ready
  }

  def receive = PartialFunction.empty

  def ready: Receive = {
    case BeginProcessing =>
      log.info(s"BeginProcessing...")
      initialState foreach (key => {
        val mowerId = s"Mower-${UUID.randomUUID().toString}"
        val mowerRef = context.actorOf(MowerActor.props(), mowerId)
        children += mowerRef
        log.debug(s"Handling actor $mowerRef")
        mowerRef ! MowerMessages.ExecuteCommands(mower = key._1, commands = key._2, 0)
      })
      context become working
  }

  def working: Receive = {
    case MowerMessages.RequestAuthorisation(currentState: Mower, newState: Mower, remainingCommands: List[Command], retry: Int) =>
      log.info(s"RequestPosition:<${newState.pos}> usedPositions:<$usedPositions> retry:<$retry>")

      usedPositions.filter(_ == newState.pos).toList match {
        case Nil =>
          sender() ! MowerMessages.PositionAllowed(newState, remainingCommands: List[Command])
          usedPositions -= currentState.pos
          usedPositions += newState.pos

        case _ if retry <= MAX_RETRY =>
          log.info(s"Position <${newState.pos}> rejected!!")
          sender() ! MowerMessages.PositionRejected(newState, remainingCommands, retry + 1)

        case _ if retry > MAX_RETRY =>
          log.info(s"Stopping mower:<$currentState> because no attempts remaining!")
          sender() ! MowerMessages.TerminateProcessing(currentState)
      }

    case MowerMessages.AllCommandsExecutedOn(mower: Mower) =>
      log.info(s"All commands executed on <$mower> ...")
      usedPositions -= mower.pos

    case SurfaceMessages.PrintSystemState =>
      children foreach (_ ! MowerMessages.PrintPosition)

    case MowerMessages.TerminateProcessing =>
      children foreach (_ ! MowerMessages.TerminateProcessing)
      context become ready
      //context.system.terminate()
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
