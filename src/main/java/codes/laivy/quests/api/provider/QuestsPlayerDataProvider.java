package codes.laivy.quests.api.provider;

import codes.laivy.quests.quests.Quest;
import codes.laivy.quests.quests.QuestsPlayerData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class QuestsPlayerDataProvider implements QuestsPlayerData {

    private final @NotNull UUID uuid;
    private final @NotNull Quest[] quests;

    public QuestsPlayerDataProvider(@NotNull UUID uuid, @NotNull Collection<Quest> quests) {
        this.uuid = uuid;
        this.quests = quests.toArray(new Quest[0]);
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return uuid;
    }

    @Override
    public @NotNull Quest[] getQuests() {
        return quests;
    }
}
