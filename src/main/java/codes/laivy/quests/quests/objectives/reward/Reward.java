package codes.laivy.quests.quests.objectives.reward;

import codes.laivy.quests.locale.IMessage;
import org.jetbrains.annotations.NotNull;

public interface Reward {

    @NotNull RewardType<Reward> getType();

    @NotNull IMessage getMessage();

}
