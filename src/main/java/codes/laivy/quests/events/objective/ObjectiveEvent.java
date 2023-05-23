package codes.laivy.quests.events.objective;

import codes.laivy.quests.quests.objectives.Objective;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ObjectiveEvent extends Event {

    private static final @NotNull HandlerList handlers = new HandlerList();

    private final @NotNull Objective objective;

    public ObjectiveEvent(@NotNull Objective objective) {
        this.objective = objective;
    }

    public final @NotNull Objective getObjective() {
        return objective;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }

}
