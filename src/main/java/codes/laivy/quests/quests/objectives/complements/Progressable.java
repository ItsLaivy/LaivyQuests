package codes.laivy.quests.quests.objectives.complements;

import codes.laivy.quests.locale.IMessage;
import org.jetbrains.annotations.Nullable;

public interface Progressable {

    @Nullable IMessage getProgressMessage();

}
