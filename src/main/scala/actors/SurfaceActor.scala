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
    context become ready
  }

  def receive = PartialFunction.empty

  def ready: Receive = {
    case BeginProcessing =>
      log.info(s"BeginProcessing...")
      initialState foreach (key => {
        val mower = key._1
        val mowerRef = context.actorOf(MowerActor.props(context.self), mower.id.toString)
        log.debug(s"Handling actor $mowerRef")
        mowerRef ! MowerMessages.ExecuteCommands(mower = mower, commands = key._2, 0)
      })
      context become working
  }

  def working: Receive = {
    case MowerMessages.RequestAuthorisation(currentState: Mower, newState: Mower, remainingCommands: List[Command], retry: Int) =>
      log.info(s"RequestPosition:<${newState.pos}> usedPositions:<$usedPositions> retry:<$retry>")

      val filterPositions = usedPositions.filterKeys(_ != currentState.id).map(_._2).filter(_ == newState.pos)

      filterPositions match {
        case Nil =>
          sender() ! MowerMessages.PositionAllowed(newState, remainingCommands: List[Command])
          usedPositions = usedPositions + (currentState.id -> newState.pos)

        case _ if retry <= MAX_RETRY =>
          log.info(s"Position <${newState.pos}> rejected!!")
          sender() ! MowerMessages.PositionRejected(newState, remainingCommands, retry + 1)

        case _ if retry > MAX_RETRY =>
          log.info(s"Stopping mower:<$currentState> because no attempts remaining!")
          sender() ! MowerMessages.TerminateProcessing(currentState)
      }

    case MowerMessages.AllCommandsExecutedOn(mower: Mower) =>
      log.info(s"All commands executed on <$mower> ...")
      sender() !  MowerMessages.TerminateProcessing(mower)
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
