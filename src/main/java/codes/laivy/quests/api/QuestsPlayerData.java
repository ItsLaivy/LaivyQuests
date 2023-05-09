package codes.laivy.quests.api;

import codes.laivy.quests.quests.Quest;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface QuestsPlayerData {

    @NotNull UUID getUniqueId();

    @NotNull Set<Quest> getQuests();

    @NotNull Map<String, Object> serialize();

}
