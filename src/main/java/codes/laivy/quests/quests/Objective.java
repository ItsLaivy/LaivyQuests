package codes.laivy.quests.quests;

import org.jetbrains.annotations.NotNull;

public interface Objective {

    @NotNull String getId();

    boolean isCompleted();
    void complete();

}
