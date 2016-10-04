package fr.xebia.mower;

import fr.xebia.DataSamples;
import javaslang.collection.List;
import org.junit.Test;

import static fr.xebia.command.Command.*;
import static fr.xebia.command.Location.NORTH;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class MowerTest implements DataSamples {

    @Test
    public void should_not_affect_mower_when_no_commands() {
        // given
        final Mower mower = aMower(withName(), atOrigin, at5X5, NORTH, List.empty());

        // when
        final Mower newMower = mower.dequeueCommand();

        // then
        assertThat(newMower).isEqualTo(mower);
    }

    @Test
    public void should_inform_when_current_command_needs_permission() {
        // given
        final Mower mower = aMower(withName(), atOrigin, at5X5, NORTH, List.of(A));

        // when / then
        assertThat(mower.commandNeedToAskAuth()).isTrue();
    }

    @Test
    public void should_inform_when_current_command_needs_no_permission() {
        // given
        final Mower mower = aMower(withName(), atOrigin, at5X5, NORTH, List.of(D));

        // when / then
        assertThat(mower.commandNeedToAskAuth()).isFalse();
    }

    @Test
    public void should_inform_no_permission_when_no_commands() {
        // given
        final Mower mower = aMower(withName(), atOrigin, at5X5, NORTH, List.empty());

        // when / then
        assertThat(mower.commandNeedToAskAuth()).isFalse();
    }

}
