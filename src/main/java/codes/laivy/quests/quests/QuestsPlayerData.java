package codes.laivy.quests.quests;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface QuestsPlayerData {

    @NotNull UUID getUniqueId();

    @NotNull Quest[] getQuests();

}
