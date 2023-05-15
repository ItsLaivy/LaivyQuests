package codes.laivy.quests.quests;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public interface QuestsPlayerData {

    @NotNull UUID getUniqueId();

    @NotNull QuestHolder[] getQuests();

    @NotNull Map<String, Object> serialize();

}
