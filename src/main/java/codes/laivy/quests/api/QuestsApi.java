package codes.laivy.quests.api;

import codes.laivy.quests.quests.QuestHolder;
import codes.laivy.quests.quests.Quest;
import codes.laivy.quests.quests.Objective;
import codes.laivy.quests.quests.QuestsPlayerData;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface QuestsApi {

    @NotNull Collection<@NotNull Quest> getQuests();

    @NotNull Collection<@NotNull Objective> getQuestTypes();
    @NotNull Collection<@NotNull QuestHolder> getQuestHolders();

    /**
     * This is the {@link Objective} serializers, if you create a custom {@link Objective} you need to add
     * your objective here attached with a serializer, this needs to be done before the LaivyQuests loading
     *
     * @return the map of serializers, the key (String) represents the objective type id.
     */
    @NotNull Map<@NotNull String, @NotNull Serializer<? extends Objective>> getObjectiveSerializers();

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
