package fr.xebia.mower;

import fr.xebia.command.Command;
import fr.xebia.command.Location;
import javaslang.control.Option;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static fr.xebia.command.Command.D;
import static fr.xebia.command.Command.G;
import static fr.xebia.command.Location.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(Parameterized.class)
public class LocationTest {

    @Parameterized.Parameter
    public Location location;
    @Parameterized.Parameter(value = 1)
    public Command command;
    @Parameterized.Parameter(value = 2)
    public Location expectedLocation;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                // clockwise
                {NORTH, D, EAST},
                {EAST, D, SOUTH},
                {SOUTH, D, WEST},
                {WEST, D, NORTH},
                // counter clockwise
                {NORTH, G, WEST},
                {WEST, G, SOUTH},
                {SOUTH, G, EAST},
                {EAST, G, NORTH}
        });
    }

    @Test
    public void should_rotate() {
        // when
        final Option<Location> maybeLocation = Location.rotate(location.angle(), command.angle());
        final Location result = maybeLocation.getOrElseThrow(() -> new IllegalStateException(
                String.format("Invalid position for locatedAt: %s, command: %s",
                        this.location.toString(),
                        command.toString()))
        );

        // then
        assertThat(result).isEqualTo(expectedLocation);
    }
}
