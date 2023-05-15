package codes.laivy.quests.api;

import codes.laivy.quests.quests.Quest;
import codes.laivy.quests.quests.QuestsPlayerData;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public interface QuestsApi {

    @NotNull Set<@NotNull Quest> getQuests();

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
