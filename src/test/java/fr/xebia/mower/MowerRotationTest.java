package fr.xebia.mower;

import fr.xebia.DataSamples;
import fr.xebia.command.Command;
import fr.xebia.command.Location;
import fr.xebia.command.Position;
import javaslang.collection.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static fr.xebia.command.Command.A;
import static fr.xebia.command.Command.G;
import static fr.xebia.command.Location.NORTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class MowerRotationTest implements DataSamples {

    @Parameterized.Parameter
    public Position withPosition;
    @Parameterized.Parameter(value = 1)
    public Location withLocation;
    @Parameterized.Parameter(value = 2)
    public List<Command> withCommands;
    @Parameterized.Parameter(value = 3)
    public Position expectedPosition;
    @Parameterized.Parameter(value = 4)
    public Location expectedLocation;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                //{new Position(1, 2), NORTH, List.of(A, A, A), new Position(1, 5), NORTH},
                {new Position(1, 2), NORTH, List.of(G, A, G, A, G, A, G, A, A), new Position(1, 2), NORTH}
        });
    }

    Mower dequeueAllCommands(Mower aMower) {
        Mower result = aMower;
        while (result.commands().size() > 0) {
            result = result.dequeueCommand();
        }
        return result;
    }

    @Test
    public void should_() {
        fail("not implemented");
        final Position limit = new Position(5, 5);
        // given
        final Mower mower = aMower(withName(), withPosition, limit, withLocation, withCommands);

        // when
        final Mower result = dequeueAllCommands(mower);

        // then
        assertThat(result.position()).isEqualTo(expectedPosition);
        assertThat(result.location()).isEqualTo(expectedLocation);
    }
}
