package codes.laivy.quests.api.provider.quest;

import codes.laivy.quests.events.objective.ObjectiveCompleteEvent;
import codes.laivy.quests.quests.objectives.Objective;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public abstract class ObjectiveProvider implements Objective {

    protected final @NotNull Date startedDate;
    protected @Nullable Date completedDate;

    public ObjectiveProvider(@NotNull Date startedDate, @Nullable Date completedDate) {
        this.startedDate = startedDate;
        this.completedDate = completedDate;
    }

    @Override
    public boolean isCompleted() {
        return completedDate != null;
    }

    public final @NotNull Date getStartedDate() {
        return startedDate;
    }
    public final @Nullable Date getCompletedDate() {
        return completedDate;
    }

    @Override
    public final void complete() {
        if (!isCompleted()) {
            throw new IllegalStateException("This objective is already completed");
        }

        ObjectiveCompleteEvent event = new ObjectiveCompleteEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            abstractComplete();
            completedDate = new Date();
        }
    }

    protected abstract void abstractComplete();

}
