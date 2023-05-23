package codes.laivy.quests.quests.objectives;

import codes.laivy.quests.locale.IMessage;
import org.jetbrains.annotations.NotNull;

public interface Objective {

    @NotNull ObjectiveType getType();

    @NotNull IMessage getName();
    @NotNull IMessage getDescription();

    boolean isCompleted();
    void complete();

}
