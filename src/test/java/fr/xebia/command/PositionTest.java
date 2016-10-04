package fr.xebia.command;

import javaslang.control.Option;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static fr.xebia.command.Location.NORTH;
import static fr.xebia.command.Location.SOUTH;
import static fr.xebia.command.Position.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class PositionTest {

    @Parameterized.Parameter
    public Location locatedAt;
    @Parameterized.Parameter(value = 1)
    public Position atPosition;
    @Parameterized.Parameter(value = 2)
    public Position withLimit;
    @Parameterized.Parameter(value = 3)
    public Option<Position> expectedPosition;

    // TODO: add additional scenarios
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                // north overflow
                {NORTH, new Position(1, 3), new Position(1, 3), Option.none()},
                {NORTH, new Position(1, 3), new Position(1, 2), Option.none()},
                // south overflow
                {SOUTH, new Position(3, 1), new Position(3, 1), Option.of(new Position(3, 0))},
                {SOUTH, new Position(3, 0), new Position(1, 2), Option.none()}
        });
    }

    @Test
    public void should_move_until_limits() {
        final Option<Position> result = advance(locatedAt, atPosition, withLimit);
        assertThat(result).isEqualTo(expectedPosition);
    }
}
