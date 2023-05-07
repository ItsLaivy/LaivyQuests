package codes.laivy.quests.api;

import codes.laivy.quests.quests.Quest;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface QuestsPlayerData {

    @NotNull Set<Quest> getQuests();

}
