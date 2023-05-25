package codes.laivy.quests.events.objective;

import codes.laivy.quests.quests.Quest;
import codes.laivy.quests.quests.objectives.Objective;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public class ObjectiveCompleteEvent extends ObjectiveEvent implements Cancellable {

    private boolean cancel = false;

    private final @NotNull Quest quest;

    public ObjectiveCompleteEvent(@NotNull Quest quest, @NotNull Objective objective) {
        super(objective);
        this.quest = quest;
    }

    public @NotNull Quest getQuest() {
        return quest;
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
