package codes.laivy.quests.events.objective;

import codes.laivy.quests.quests.Objective;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public class ObjectiveCompleteEvent extends ObjectiveEvent implements Cancellable {

    private boolean cancel = false;

    public ObjectiveCompleteEvent(@NotNull Objective objective) {
        super(objective);
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
