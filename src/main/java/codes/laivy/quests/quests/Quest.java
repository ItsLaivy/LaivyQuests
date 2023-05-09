package codes.laivy.quests.quests;

import codes.laivy.mlanguage.lang.Message;
import codes.laivy.mlanguage.lang.MessageArray;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface Quest {

    /**
     * The identification of this quest
     * @return the quest id
     */
    @NotNull String getId();

    @NotNull Message<?> getName();
    @NotNull MessageArray<?> getDescription();

    @NotNull Map<String, Object> serialize();

}
