package codes.laivy.quests.quests;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public interface QuestHolder {

    @NotNull UUID getUniqueId();

    @NotNull Quest getQuest();

    @NotNull Date getStartDate();

    /**
     * @return The date this quest has finished or null if the quest hasn't finished yet
     */
    @Nullable Date getFinishDate();

    boolean isFinished();

    @NotNull Map<String, Object> serialize();

}
