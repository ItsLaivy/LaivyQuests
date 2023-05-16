package codes.laivy.quests.quests;

import codes.laivy.quests.locale.IMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface Quest {

    /**
     * The identification of this quest
     * @return the quest id
     */
    @NotNull String getId();

    @NotNull IMessage getName();

    @NotNull IMessage getDescription();

    @NotNull Collection<Objective> getObjectives();

    void complete(@NotNull QuestHolder holder);

}