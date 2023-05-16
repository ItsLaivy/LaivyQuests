package codes.laivy.quests.quests;

import org.jetbrains.annotations.NotNull;

public interface ObjectiveHolder {

    @NotNull QuestHolder getQuestHolder();

    @NotNull Objective getObjective();

    boolean isCompleted();
    void complete();

    boolean equals(@NotNull Objective objective);

}
