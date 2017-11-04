package actors

import akka.actor.{ActorRef, Actor, ActorLogging, Props}
import model.{Command, Forward, Mower}

class MowerActor(parent: ActorRef) extends Actor with ActorLogging {

  def receive = {

    // TODO 1.2 add four case
    // TODO 1.2.1 first one has to handle ExecuteCommands message

    log.debug(s"Executing instructions for <$mower>, remaining: <$commands>")

    commands match {
      case Nil => // TODO 1.2.1.1 no commands left, send AllCommandsExecutedOn message to the parent actor
      case _ => // TODO 1.2.1.2 call handleRemainingCommands function
    }

    // TODO 1.2.2 second one has to handle PositionAllowed message and send to itself ExecuteCommands message
    log.debug(s"Position <${newState.pos}> authorized, remaining:<${commands.tail}>")

    // TODO 1.2.3 third one has to handle PositionRejected message and send to itself ExecuteCommands message

    // TODO 1.2.4 fourth one has to handle TerminateProcessing message and stop the current actor
    log.debug(s"Terminating $mower")

  }

  def handleRemainingCommands(mower: Mower, commands: List[Command], retry: Int) = {
    commands match {
      case command@Forward :: tail =>
        log.debug(s"Going forward on <$mower>, remaining:<$tail>, retry:<$retry>")
        val newState = mower.forward
        parent ! MowerMessages.RequestAuthorisation(mower, newState, commands, retry)

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

  // TODO 1.1 finish to implement the props function
  // def props(parent: ActorRef) =

}
