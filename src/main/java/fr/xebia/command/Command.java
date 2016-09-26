package fr.xebia.command;

public enum Command {
    A(0, true),
    G(90, false),
    D(-90, false);

    private int angle;
    private boolean askAuth;

    Command(int angle, boolean askAuth) {
        this.angle = angle;
        this.askAuth = askAuth;
    }

    public boolean needToAskAuth() {
        return askAuth;
    }

    public int angle() {
        return angle;
    }
}
