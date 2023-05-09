package codes.laivy.quests.api;

import codes.laivy.quests.quests.Quest;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class QuestsPlayerDataProvider implements QuestsPlayerData {

    private final @NotNull UUID uuid;
    private final @NotNull Set<Quest> quests;

    public QuestsPlayerDataProvider(@NotNull UUID uuid, @NotNull Set<Quest> quests) {
        this.uuid = uuid;
        this.quests = quests;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return uuid;
    }

    @Override
    public @NotNull Set<Quest> getQuests() {
        return quests;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        @NotNull Map<String, Object> map = new LinkedHashMap<>();
        map.put("uuid", getUniqueId().toString());
        map.put("quests", new LinkedList<String>() {{
            for (Quest quest : getQuests()) {
                add(quest.getId());
            }
        }});
        return map;
    }
}
