package fr.xebia.mower;

public interface MowerMessages {

    static class ExecuteCommands {
        final Mower mower;
        final int retry;

        public ExecuteCommands(Mower mower, int retry) {
            this.mower = mower;
            this.retry = retry;
        }
    }

    static class RequestAuthorisation {
        public final Mower currentState;
        public final Mower newState;
        public final int retry;

        public RequestAuthorisation(Mower currentState, Mower newState, int retry) {
            this.currentState = currentState;
            this.newState = newState;
            this.retry = retry;
        }
    }

    static class PositionAllowed {
        final Mower mower;

        public PositionAllowed(Mower mower) {
            this.mower = mower;
        }
    }

    static class PositionRejected {
        final Mower mower;
        int retry;

        public PositionRejected(Mower mower, int retry) {
            this.mower = mower;
            this.retry = retry;
        }
    }

    static class PrintPosition {
    }

    static class TerminateProcessing {
        Mower mower;

        public TerminateProcessing(Mower mower) {
            this.mower = mower;
        }
    }

    static class AllCommandsExecutedOn {
        public final Mower mower;

        public AllCommandsExecutedOn(Mower mower) {
            this.mower = mower;
        }
    }

}
