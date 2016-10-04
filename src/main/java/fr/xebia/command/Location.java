package fr.xebia.command;

import javaslang.collection.List;
import javaslang.control.Option;

import java.util.stream.Stream;

import static javaslang.collection.List.*;

public enum Location {
    NORTH("N", 0, 1, of(90)),
    EAST("E", 1, 0, of(0, 360)),
    WEST("W", -1, 0, of(180)),
    SOUTH("S", 0, -1, of(270));

    private final List<Integer> angle;
    private String value;
    private int deltaX;
    private int deltaY;

    Location(String value, int deltaX, int deltaY, List<Integer> angle) {
        this.value = value;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.angle = angle;
    }

    public static Option<Location> rotate(int angle, int delta) {
        int newAngle;
        if (angle + delta > 360) {
            newAngle = delta;
        } else if (angle + delta < 0) {
            newAngle = 360 - delta;
        } else {
            newAngle = angle + delta;
        }
        return from(newAngle);
    }

    public int deltaX() {
        return deltaX;
    }

    public int deltaY() {
        return deltaY;
    }

    public static Option<Location> from(int anotherAngle) {
        return of(values())
                .filter(s -> s.angle.contains(anotherAngle))
                .headOption();
    }

    public static Option<Location> from(String value) {
        return of(values())
                .filter(s -> s.value.equals(value))
                .headOption();
    }

    public int angle() {
        return angle.max().getOrElse(0);
    }
}
