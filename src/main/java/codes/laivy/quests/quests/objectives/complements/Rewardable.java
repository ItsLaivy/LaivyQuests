package codes.laivy.quests.quests.objectives.complements;

import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.quests.objectives.reward.Reward;
import org.jetbrains.annotations.Nullable;

public interface Rewardable {

    @Nullable Reward getReward();

    @Nullable IMessage getRewardMessage();

}
