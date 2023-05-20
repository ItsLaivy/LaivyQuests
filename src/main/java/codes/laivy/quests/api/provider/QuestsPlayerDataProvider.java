package codes.laivy.quests.api.provider;

import codes.laivy.quests.quests.Quest;
import codes.laivy.quests.quests.QuestsPlayerData;
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
}
