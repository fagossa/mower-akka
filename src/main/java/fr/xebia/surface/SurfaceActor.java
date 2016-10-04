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

import static java.lang.String.format;

class SurfaceActor extends AbstractLoggingActor {

    private final int MAX_RETRY = 10;
    private final Surface surface;
    private final List<Mower> initialState;
    private Map<String, Position> usedPositions = HashMap.empty();

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
        log().info(format("RequestPosition:<%s> usedPositions:<%s> retry:<%s>",
                message.newState.position(), usedPositions, message.retry));

        /*usedPositions
                .filter((tuple)->tuple._1 == message.currentState.id());
        val filterPositions = usedPositions.filterKeys(_ != currentState.id).map(_._2).filter(_ == newState.pos)*/

        /*case MowerMessages.RequestAuthorisation(currentState: Mower, newState: Mower, remainingCommands: List[Command], retry: Int) =>
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
*/

    }

    private void onAllCommandsExecutedOn(MowerMessages.AllCommandsExecutedOn message) {
        log().info(format("All commands executed on <%s> ...", message.mower));
        sender().tell(new MowerMessages.TerminateProcessing(message.mower), self());
    }
}
