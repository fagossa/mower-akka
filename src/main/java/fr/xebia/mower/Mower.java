package fr.xebia.mower;

import fr.xebia.command.Command;
import fr.xebia.command.Location;
import fr.xebia.command.Position;
import javaslang.collection.List;

public class Mower {
    private final Position position;
    private final List<Command> commands;
    private String name;
    private Location location;

    public Mower(String name, Position position, Location location, List<Command> commands) {
        this.name = name;
        this.position = position;
        this.location = location;
        this.commands = commands;
    }

    public String name() {
        return name;
    }

    public Position position() {
        return position;
    }

    public List<Command> commands() {
        return commands;
    }

    public Location location() {
        return location;
    }

    Mower dequeueCommand() {
        return commands.headOption().map(curr -> {
            final int newAngle = curr.angle() + location.angle();
            Location newLocation = Location.from(newAngle).orElse(location);
            return new Mower(name, position, newLocation, commands);
        }).getOrElse(this);
    }

    boolean commandNeedToAskAuth() {
        return commands().peek().needToAskAuth();
    }
}
