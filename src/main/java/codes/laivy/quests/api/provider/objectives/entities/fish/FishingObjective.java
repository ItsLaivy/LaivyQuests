package codes.laivy.quests.api.provider.objectives.entities.fish;

import codes.laivy.quests.api.provider.objectives.items.mechanic.Item;
import codes.laivy.quests.api.provider.quest.ObjectiveProvider;
import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.quests.objectives.ObjectiveType;
import codes.laivy.quests.quests.objectives.complements.Progressable;
import codes.laivy.quests.quests.objectives.complements.Rewardable;
import codes.laivy.quests.quests.objectives.reward.Reward;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import static codes.laivy.quests.LaivyQuests.laivyQuests;
import static codes.laivy.quests.api.provider.objectives.entities.fish.FishingObjectiveType.FISHING_OBJECTIVE_TYPE_ID;

public class FishingObjective extends ObjectiveProvider implements Rewardable, Progressable {

    private final @NotNull Item fish;
    private final @Range(from = 1, to = Integer.MAX_VALUE) int meta;
    private @Range(from = 0, to = Integer.MAX_VALUE) int progress;

    private final @Nullable Reward reward;

    public FishingObjective(
            @NotNull IMessage name,
            @NotNull IMessage description,

            @NotNull Item fish,
            @Range(from = 1, to = Integer.MAX_VALUE) int meta,
            @Range(from = 0, to = Integer.MAX_VALUE) int progress,
            @Nullable Reward reward
    ) {
        super(name, description);

        this.fish = fish;

        this.meta = meta;
        this.progress = progress;

        this.reward = reward;
    }

    @Override
    protected void abstractComplete() {

    }

    public @NotNull Item getFish() {
        return fish;
    }

    @Override
    public @NotNull FishingObjectiveType getType() {
        ObjectiveType type = laivyQuests().getApi().getObjectiveType(FISHING_OBJECTIVE_TYPE_ID);

        if (type instanceof FishingObjectiveType) {
            return (FishingObjectiveType) type;
        } else {
            throw new IllegalStateException("This objective type '" + FISHING_OBJECTIVE_TYPE_ID + "' isn't a instance of the fishing objective type class. (" + FishingObjectiveType.class.getName() + ")");
        }
    }

    @Override
    public boolean isCompleted() {
        return getProgress() >= getMeta();
    }

    public @Range(from = 0, to = Integer.MAX_VALUE) int getProgress() {
        return progress;
    }

    public void setProgress(@Range(from = 0, to = Integer.MAX_VALUE) int progress) {
        this.progress = Math.min(progress, getMeta());
    }

    public @Range(from = 1, to = Integer.MAX_VALUE) int getMeta() {
        return meta;
    }

    @Override
    public @NotNull IMessage getProgressMessage() {
        return laivyQuests().getMessageStorage().getMessage("Objectives: progress message", getProgress(), getMeta());
    }

    @Override
    public @Nullable Reward getReward() {
        return reward;
    }

    @Override
    public @Nullable IMessage getRewardMessage() {
        return reward != null ? reward.getMessage() : null;
    }
}
