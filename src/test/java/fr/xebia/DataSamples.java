package fr.xebia;

import fr.xebia.command.Command;
import fr.xebia.command.Location;
import fr.xebia.command.Position;
import fr.xebia.mower.Mower;
import javaslang.collection.List;

import java.util.UUID;

public interface DataSamples {

    default String withName() {
        return UUID.randomUUID().toString();
    }

    Position atOrigin = new Position(0, 0);

    Position at5X5 = new Position(5, 5);

    default Mower aMower(String name, Position position, Position limit, Location location, List<Command> commands) {
        return new Mower(name, position, limit, location, commands);
    }
}
