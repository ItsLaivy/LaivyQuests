package codes.laivy.quests.api.provider.quest;

import codes.laivy.quests.events.objective.ObjectiveCompleteEvent;
import codes.laivy.quests.quests.objectives.Objective;
import org.bukkit.Bukkit;

public abstract class ObjectiveProvider implements Objective {

    @Override
    public final void complete() {
        if (!isCompleted()) {
            throw new IllegalStateException("This objective is already completed");
        }

        ObjectiveCompleteEvent event = new ObjectiveCompleteEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            abstractComplete();
        }
    }

    protected abstract void abstractComplete();

}
