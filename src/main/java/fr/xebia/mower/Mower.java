package fr.xebia.mower;

import fr.xebia.command.Command;
import fr.xebia.command.Location;
import fr.xebia.command.Position;
import javaslang.collection.List;
import javaslang.control.Option;
import javaslang.control.Try;

import static javaslang.API.For;

public class Mower {
    private final Position position;
    private final List<Command> commands;
    private String name;
    private Position limit;
    private Location location;

    public Mower(String name, Position position, Position limit, Location location, List<Command> commands) {
        this.name = name;
        this.position = position;
        this.limit = limit;
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
        Option<Mower> maybeMower = For(commands.headOption(), (command) ->
                For(Location.rotate(location.angle(), command.angle()), newLocation ->
                        For(Position.advance(newLocation, position, limit))
                                .yield((newPosition) ->
                                        new Mower(name, newPosition, limit, newLocation, commands.tail())
                                )
                )
        ).toOption();
        return maybeMower.getOrElse(this);
    }

    boolean commandNeedToAskAuth() {
        return Try.of(() -> commands().peek())
                .map(Command::needToAskAuth)
                .getOrElse(false);
    }
}
