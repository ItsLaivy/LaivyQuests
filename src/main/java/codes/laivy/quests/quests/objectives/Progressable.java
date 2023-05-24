package codes.laivy.quests.quests.objectives;

import codes.laivy.quests.locale.IMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public interface Progressable<T> {

    @UnknownNullability T getProgress();
    void setProgress(@UnknownNullability T progress);

    @NotNull IMessage getProgressMessage(@NotNull Objective objective);

}
