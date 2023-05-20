package codes.laivy.quests.api;

import codes.laivy.quests.quests.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface QuestsApi {

    @NotNull Collection<@NotNull Objective> getQuestTypes();

    @NotNull Collection<ObjectiveType> getObjectiveTypes();
    @NotNull ObjectiveType getObjectiveType(@NotNull String id);

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
