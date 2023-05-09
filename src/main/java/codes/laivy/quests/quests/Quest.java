package codes.laivy.quests.quests;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface Quest {

    /**
     * The identification of this quest
     * @return the quest id
     */
    @NotNull String getId();

    @NotNull Map<String, Object> serialize();

}
