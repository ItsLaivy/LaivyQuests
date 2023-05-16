package codes.laivy.quests.api.provider.quest;

import codes.laivy.quests.events.QuestCompleteEvent;
import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.quests.Objective;
import codes.laivy.quests.quests.Quest;
import codes.laivy.quests.quests.QuestHolder;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public abstract class QuestProvider implements Quest {

    private final @NotNull String id;
    private final @NotNull Set<Objective> objectives;

    private @NotNull IMessage name;
    private @NotNull IMessage description;

    public QuestProvider(@NotNull String id, @NotNull LinkedHashSet<Objective> objectives, @NotNull IMessage name, @NotNull IMessage description) {
        this.id = id;
        this.objectives = objectives;
        this.name = name;
        this.description = description;
    }

    /**
     * The identification of this quest
     * @return the quest id
     */
    @Override
    public final @NotNull String getId() {
        return this.id;
    }

    @Override
    public final @NotNull IMessage getName() {
        return this.name;
    }
    public final void setName(@NotNull IMessage name) {
        this.name = name;
    }

    @Override
    public final @NotNull IMessage getDescription() {
        return this.description;
    }
    public final void setDescription(@NotNull IMessage description) {
        this.description = description;
    }

    @Override
    public final @NotNull Set<Objective> getObjectives() {
        return objectives;
    }

    @Override
    public final void complete(@NotNull QuestHolder holder) {
        QuestCompleteEvent event = new QuestCompleteEvent(holder);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            run(holder);
            holder.finish();
        }
    }

    protected abstract void run(@NotNull QuestHolder holder);

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestProvider that = (QuestProvider) o;
        return getId().equals(that.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getId());
    }
}
