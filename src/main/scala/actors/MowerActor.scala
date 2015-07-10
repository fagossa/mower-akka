package actors

import akka.actor.{Actor, ActorLogging, Props}
import model.{Command, Forward, Mower}

class MowerActor extends Actor with ActorLogging {

  def receive = {
    case MowerMessages.ExecuteCommands(mower: Mower, commands: List[Command], retry: Int) =>
      log.info(s"Executing instructions for <$mower>, remaining: <$commands>")
      commands.size match {
        case 0 => context.parent ! MowerMessages.AllCommandsExecutedOn(mower)
        case _ => handleRemainingCommands(mower, commands, retry)
      }

    case MowerMessages.PositionAllowed(newState: Mower, commands: List[Command]) =>
      log.info(s"Position <${newState.pos}> authorized, remaining:<${commands.tail}>")
      self ! MowerMessages.ExecuteCommands(newState, commands.tail, 0)

    case MowerMessages.PositionRejected(mower: Mower, commands: List[Command], retry: Int) =>
      self ! MowerMessages.ExecuteCommands(mower, commands, retry)

    case MowerMessages.PrintPosition =>
      log.info(s"Current position ...")

    case MowerMessages.TerminateProcessing(mower: Mower) =>
      context stop self

    case _ =>
      log.error("Shutting down the system!!!")
      context.system.terminate()

  }

  def handleRemainingCommands(mower: Mower, commands: List[Command], retry: Int) = {
    commands match {
      case command@Forward :: tail =>
        log.debug(s"Going forward on <$mower>, remaining:<$tail>, retry:<$retry>")
        val newState = mower.forward
        context.parent ! MowerMessages.RequestAuthorisation(mower, newState, commands, retry)

      case command :: tail =>
        log.debug(s"Rotating:<$command>, remaining:<$tail>")
        val newState = mower.rotate(command)
        self ! MowerMessages.ExecuteCommands(newState, commands.tail, 0)
    }
  }
}

object MowerMessages {

  case class ExecuteCommands(mower: Mower, commands: List[Command], retry: Int)

  case class RequestAuthorisation(currentState: Mower, newState: Mower, commands: List[Command], retry: Int)

  case class PositionAllowed(mower: Mower, commands: List[Command])

  case class PositionRejected(mower: Mower, commands: List[Command], retry: Int)

  case object PrintPosition

  case class TerminateProcessing(mower: Mower)

  case class AllCommandsExecutedOn(mower: Mower)

}

object MowerActor {

  def props(): Props = Props(classOf[MowerActor])

}
