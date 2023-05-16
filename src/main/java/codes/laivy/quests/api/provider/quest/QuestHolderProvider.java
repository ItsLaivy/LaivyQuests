package codes.laivy.quests.api.provider.quest;

import codes.laivy.quests.quests.ObjectiveHolder;
import codes.laivy.quests.quests.Quest;
import codes.laivy.quests.quests.QuestHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class QuestHolderProvider implements QuestHolder {

    private final @NotNull UUID uuid;
    private final @NotNull Quest quest;
    private final @NotNull Date start;
    private final @Nullable Date finish;

    private final @NotNull Set<ObjectiveHolder> objectiveHolders = new HashSet<>();

    public QuestHolderProvider(@NotNull UUID uuid, @NotNull Quest quest, @NotNull Date start, @Nullable Date finish) {
        this.uuid = uuid;
        this.quest = quest;
        this.start = start;
        this.finish = finish;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return uuid;
    }

    @Override
    public @NotNull Quest getQuest() {
        return quest;
    }

    @Override
    public @NotNull Date getStartDate() {
        return start;
    }

    @Override
    public @Nullable Date getFinishDate() {
        return finish;
    }

    @Override
    public boolean isFinished() {
        return getFinishDate() != null;
    }

    @Override
    public @NotNull Collection<ObjectiveHolder> getObjectiveHolders() {
        return objectiveHolders;
    }

    @Override
    public void finish() {

    }
}
