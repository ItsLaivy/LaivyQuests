package codes.laivy.quests.api.provider.quest;

import codes.laivy.quests.events.objective.ObjectiveCompleteEvent;
import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.quests.Quest;
import codes.laivy.quests.quests.objectives.Objective;
import codes.laivy.quests.quests.objectives.complements.Rewardable;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public abstract class ObjectiveProvider implements Objective {

    private final @NotNull IMessage name;
    private final @NotNull IMessage description;

    public ObjectiveProvider(@NotNull IMessage name, @NotNull IMessage description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public final @NotNull IMessage getName() {
        return name;
    }

    @Override
    public final @NotNull IMessage getDescription() {
        return description;
    }

    @Override
    public final void complete(@NotNull Quest quest) {
        if (!isCompleted()) {
            throw new IllegalStateException("This objective is already completed");
        }

        ObjectiveCompleteEvent event = new ObjectiveCompleteEvent(quest, this);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            abstractComplete();
            if (this instanceof Rewardable) {
                Rewardable rewardable = (Rewardable) this;
                if (rewardable.getReward() != null) {
                    rewardable.getReward().give(quest, this);
                }
            }
        }
    }

    protected abstract void abstractComplete();

}
