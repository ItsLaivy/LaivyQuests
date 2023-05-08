package codes.laivy.quests.api;

import codes.laivy.quests.quests.Quest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;
import java.util.UUID;

public interface QuestsApi {

    @NotNull Set<@NotNull Quest> getQuests();

    @Nullable QuestsPlayerData getPlayerData(@NotNull UUID uuid);

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
