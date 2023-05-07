package codes.laivy.quests.quests;

import codes.laivy.mlanguage.lang.Message;
import codes.laivy.mlanguage.lang.MessageArray;
import org.jetbrains.annotations.NotNull;

public interface Quest {

    /**
     * The identification of this quest
     * @return the quest id
     */
    @NotNull String getId();

    @NotNull Message<?> getName();
    @NotNull MessageArray<?> getDescription();

}
