package fr.xebia.surface;

import fr.xebia.command.Position;

public class Surface {

    private Position maxPosition;

    public Surface(Position maxPosition) {
        this.maxPosition = maxPosition;
    }

    public Position maxPosition() {
        return maxPosition;
    }

    public boolean contains(Position location) {
        return (location.x() >= 0 && location.y() >= 0)
                        && (location.x() <= maxPosition.x() && location.y() <= maxPosition.y());
    }
}
