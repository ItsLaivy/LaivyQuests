package codes.laivy.quests.quests;

import org.jetbrains.annotations.NotNull;

public interface Objective {

    @NotNull ObjectiveType getType();

    boolean isCompleted();
    void complete();

}
