package codes.laivy.quests.api;

import codes.laivy.quests.quests.*;
import codes.laivy.quests.quests.objectives.Objective;
import codes.laivy.quests.quests.objectives.ObjectiveType;
import codes.laivy.quests.quests.objectives.reward.Reward;
import codes.laivy.quests.quests.objectives.reward.RewardType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

public interface QuestsApi {

    @NotNull Collection<ObjectiveType> getObjectiveTypes();

    // TODO: 23/05/2023 Nullable
    @NotNull ObjectiveType getObjectiveType(@NotNull String id);

    @NotNull Collection<RewardType<? extends Reward>> getRewardTypes();

    @NotNull RewardType<? extends Reward> getRewardType(@NotNull String id);

    @NotNull QuestsPlayerData getPlayerData(@NotNull UUID uuid);

    @NotNull QuestsCommandApi getCommandApi();

    /**
     * Loads the API and prepare everything
     */
    void load();

    /**
     * Unloads the APIs and disable everything
     */
    void unload();

    /**
     * Checks if the API is loaded
     * @return true if the API is loaded, false otherwise
     */
    boolean isLoaded();

}
