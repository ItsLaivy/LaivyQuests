package codes.laivy.quests.api.provider;

import codes.laivy.quests.quests.QuestsPlayerData;
import codes.laivy.quests.quests.QuestHolder;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class QuestsPlayerDataProvider implements QuestsPlayerData {

    private final @NotNull UUID uuid;
    private final @NotNull QuestHolder[] quests;

    public QuestsPlayerDataProvider(@NotNull UUID uuid, @NotNull Collection<QuestHolder> quests) {
        this.uuid = uuid;
        this.quests = quests.toArray(new QuestHolder[0]);
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return uuid;
    }

    @Override
    public @NotNull QuestHolder[] getQuests() {
        return quests;
    }
}
