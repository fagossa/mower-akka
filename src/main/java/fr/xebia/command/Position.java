package fr.xebia.command;

import javaslang.control.Option;

import java.util.Objects;

public class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public static Option<Position> advance(Location location, Position current, Position limit) {
        return Option
                .of(new Position(current.x() + location.deltaX(), current.y() + location.deltaY()))
                .filter(pos ->
                        (pos.x() >= 0 && pos.x() <= limit.x())
                                && (pos.y() >= 0 && pos.y() <= limit.y())
                );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return x == position.x &&
                y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
