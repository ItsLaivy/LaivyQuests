package codes.laivy.quests.quests;

import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.quests.objectives.Objective;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public interface Quest {

    /**
     * The identification of this quest
     * @return the quest id
     */
    @NotNull String getId();

    @NotNull IMessage getName();

    @NotNull IMessage getDescription();

    /**
     * Gets the objective collection
     *
     * @param categories if true will retrieve the objectives from {@link codes.laivy.quests.api.provider.objectives.CategoryObjective} too; If false, just the registered objectives
     * @return the objectives collection
     */
    @NotNull Collection<Objective> getObjectives(boolean categories);

    @NotNull Date getStartDate();

    /**
     * @return The date this quest has finished or null if the quest hasn't finished yet
     */
    @Nullable Date getCompletedDate();

    @NotNull UUID getUniqueId();

    boolean isCompleted();

    void complete();

}