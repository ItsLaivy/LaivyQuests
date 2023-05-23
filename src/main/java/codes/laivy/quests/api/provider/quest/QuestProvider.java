package codes.laivy.quests.api.provider.quest;

import codes.laivy.quests.events.quest.QuestCompleteEvent;
import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.quests.objectives.Objective;
import codes.laivy.quests.quests.Quest;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class QuestProvider implements Quest {

    private final @NotNull String id;
    private final @NotNull Set<Objective> objectives;

    private @NotNull IMessage name;
    private @NotNull IMessage description;

    private final @NotNull UUID uuid;

    private final @NotNull Date startDate;
    private @Nullable Date finishDate;

    public QuestProvider(@NotNull String id, @NotNull LinkedHashSet<Objective> objectives, @NotNull IMessage name, @NotNull IMessage description, @NotNull UUID uuid, @NotNull Date startDate, @Nullable Date finishDate) {
        this.id = id;
        this.objectives = objectives;
        this.name = name;
        this.description = description;
        this.uuid = uuid;
        this.startDate = startDate;
        this.finishDate = finishDate;
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
    public @NotNull Date getStartDate() {
        return startDate;
    }

    @Override
    public @Nullable Date getCompletedDate() {
        return finishDate;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return uuid;
    }

    @Override
    public boolean isCompleted() {
        return getCompletedDate() != null;
    }

    @Override
    public final void complete() {
        if (!isCompleted()) {
            throw new IllegalStateException("This quest is already completed");
        }

        QuestCompleteEvent event = new QuestCompleteEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            finishDate = new Date();
        }
    }

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
