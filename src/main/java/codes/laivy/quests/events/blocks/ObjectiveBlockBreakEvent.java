package codes.laivy.quests.events.blocks;

import codes.laivy.quests.api.provider.objectives.blocks.mechanic.IBlock;
import org.jetbrains.annotations.NotNull;

public class ObjectiveBlockBreakEvent extends ObjectiveBlockEvent {
    public ObjectiveBlockBreakEvent(@NotNull IBlock block) {
        super(block);
    }
}
