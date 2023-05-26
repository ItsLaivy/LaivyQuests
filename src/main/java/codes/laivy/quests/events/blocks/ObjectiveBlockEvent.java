package codes.laivy.quests.events.blocks;

import codes.laivy.quests.api.provider.objectives.blocks.mechanic.IBlock;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ObjectiveBlockEvent extends Event {

    private static final @NotNull HandlerList handlers = new HandlerList();

    private final @NotNull IBlock block;

    public ObjectiveBlockEvent(@NotNull IBlock block) {
        this.block = block;
    }

    public @NotNull IBlock getBlock() {
        return block;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }

}
