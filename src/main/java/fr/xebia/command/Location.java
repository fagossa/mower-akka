package fr.xebia.command;

import java.util.Optional;
import java.util.stream.Stream;

public enum Location {
    NORTH("N", 90),
    EAST("E", 0),
    WEST("W", 180),
    SOUTH("S", 270);

    private String value;
    private final int angle;

    Location(String value, int angle) {
        this.value = value;
        this.angle = angle;
    }

    public int angle() {
        return angle;
    }

    public static Optional<Location> from(int newAngle) {
        return Stream.of(values())
                .filter(s -> s.angle() == newAngle)
                .findFirst();
    }

    public static Optional<Location> from(String value) {
        return Stream.of(values())
                .filter(s -> s.value.equals(value))
                .findFirst();
    }
}
