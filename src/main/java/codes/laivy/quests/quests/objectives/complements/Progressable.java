package codes.laivy.quests.quests.objectives.complements;

import codes.laivy.quests.locale.IMessage;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public interface Progressable<T> {

    @UnknownNullability T getProgress();
    void setProgress(@UnknownNullability T progress);

    @Nullable IMessage getProgressMessage();

}
