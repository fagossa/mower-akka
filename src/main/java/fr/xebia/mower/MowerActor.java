package fr.xebia.mower;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

import static java.lang.String.format;

public class MowerActor extends AbstractLoggingActor {

    private ActorRef parent;

    public MowerActor(ActorRef parent) {
        this.parent = parent;
        receive(
                ReceiveBuilder
                        .match(MowerMessages.ExecuteCommands.class, this::onExecuteCommands)
                        .match(MowerMessages.PositionAllowed.class, this::onPositionAllowed)
                        .match(MowerMessages.PositionRejected.class, this::onPositionRejected)
                        .match(MowerMessages.TerminateProcessing.class, this::onTerminateProcessing)
                        .build()
        );
    }

    public static Props props(ActorRef parent) {
        return Props.create(MowerActor.class, parent);
    }

    private void onExecuteCommands(MowerMessages.ExecuteCommands message) {
        log().debug(format("Executing instructions for <$mower>, remaining: <%s>", message.mower.commands()));
        if (message.mower.commands().isEmpty()) {
            handleNoMoreCommands(message);
        } else {
            handleRemainingCommands(message.mower, message.retry);
        }
    }

    private void onPositionAllowed(MowerMessages.PositionAllowed message) {
        final Mower newState = message.mower.dequeueCommand();
        log().debug(format("Position <%s> authorized, remaining:<%s>", message.mower.position(), newState.commands()));
        self().tell(new MowerMessages.ExecuteCommands(newState, 0), self());
    }

    private void onPositionRejected(MowerMessages.PositionRejected message) {
        self().tell(new MowerMessages.ExecuteCommands(message.mower, message.retry), self());
    }

    private void onTerminateProcessing(MowerMessages.TerminateProcessing message) {
        log().debug(format("Terminating %s", message.mower));
        context().stop(self());
    }

    private void handleNoMoreCommands(MowerMessages.ExecuteCommands message) {
        parent.tell(new MowerMessages.AllCommandsExecutedOn(message.mower), self());
    }

    private void handleRemainingCommands(Mower mower, int retry) {
        log().debug(format("Going forward on <%s>, remaining:<%s>, retry:<%d>", mower, mower.commands().tail(), retry));
        final Mower newState = mower.dequeueCommand();
        if (mower.commandNeedToAskAuth()) {
            parent.tell(new MowerMessages.RequestAuthorisation(mower, newState, retry), self());
        } else {
            self().tell(new MowerMessages.ExecuteCommands(newState, 0), self());
        }
    }
}
