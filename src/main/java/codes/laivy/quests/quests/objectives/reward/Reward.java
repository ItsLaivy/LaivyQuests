package codes.laivy.quests.quests.objectives.reward;

import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.quests.Quest;
import codes.laivy.quests.quests.objectives.Objective;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Reward {

    @NotNull RewardType<Reward> getType();

    @NotNull IMessage getMessage();

    void give(@NotNull Quest quest, @NotNull Objective objective);

}
