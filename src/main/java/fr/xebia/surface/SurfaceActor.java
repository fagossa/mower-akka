package fr.xebia.surface;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import fr.xebia.command.Position;
import fr.xebia.mower.Mower;
import fr.xebia.mower.MowerActor;
import fr.xebia.mower.MowerMessages;
import javaslang.collection.HashMap;
import javaslang.collection.List;
import javaslang.collection.Map;

import static java.lang.String.*;

class SurfaceActor extends AbstractLoggingActor {

    private int MAX_RETRY = 10;
    private Surface surface;
    private List<Mower> initialState;
    Map<Integer, Position> usedPositions = new HashMap<>();

    public SurfaceActor(Surface surface, List<Mower> initialState) {
        this.surface = surface;
        this.initialState = initialState;
    }

    static Props props(Surface surface) {
        return Props.create(SurfaceActor.class, surface);
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        context().become(
                ReceiveBuilder
                        .match(SurfaceMessages.BeginProcessing.class, this::onBeginProcessing)
                        .build()
        );
    }

    private void onBeginProcessing(SurfaceMessages.BeginProcessing message) {
        log().info("BeginProcessing...");
        initialState.toStream().forEach((mower) -> {
            final ActorRef mowerRef = context().actorOf(MowerActor.props(self()), mower.name());
            log().info(format("Handling actor <%s>", mowerRef));
            mowerRef.tell(new MowerMessages.ExecuteCommands(mower, 0), self());
        });
        context().become(
                ReceiveBuilder
                        .match(MowerMessages.RequestAuthorisation.class, this::onRequestAuthorisation)
                        .match(MowerMessages.AllCommandsExecutedOn.class, this::onAllCommandsExecutedOn)
                        .build()
        );
    }

    private void onRequestAuthorisation(MowerMessages.RequestAuthorisation message) {
        // TODO
        log().info(format("RequestPosition:<%s> usedPositions:<$usedPositions> retry:<$retry>",
                message.newState.position()));
    }

    private void onAllCommandsExecutedOn(MowerMessages.AllCommandsExecutedOn message) {
        log().info(format("All commands executed on <%s> ...", message.mower));
        sender().tell(new MowerMessages.TerminateProcessing(message.mower), self());
    }
}
