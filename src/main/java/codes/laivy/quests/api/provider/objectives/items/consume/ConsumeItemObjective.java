package codes.laivy.quests.api.provider.objectives.items.consume;

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
import static codes.laivy.quests.api.provider.objectives.items.consume.ConsumeItemObjectiveType.CONSUME_ITEM_OBJECTIVE_TYPE_ID;

public class ConsumeItemObjective extends ObjectiveProvider implements Rewardable, Progressable {

    private final @NotNull Item item;

    private final @Range(from = 1, to = Integer.MAX_VALUE) int meta;
    private @Range(from = 0, to = Integer.MAX_VALUE) int progress;

    private final @Nullable Reward reward;

    public ConsumeItemObjective(
            @NotNull IMessage name,
            @NotNull IMessage description,

            @NotNull Item item,

            @Range(from = 1, to = Integer.MAX_VALUE) int meta,
            @Range(from = 0, to = Integer.MAX_VALUE) int progress,

            @Nullable Reward reward
    ) {
        super(name, description);

        this.meta = meta;
        this.progress = progress;

        this.item = item;
        this.reward = reward;

        if (!getItem().getMaterial().isEdible()) {
            throw new IllegalArgumentException("This material '" + getItem().getMaterial() + "' isn't edible.");
        }
    }

    public @NotNull Item getItem() {
        return item;
    }

    @Override
    protected void abstractComplete() {

    }

    @Override
    public @NotNull ConsumeItemObjectiveType getType() {
        ObjectiveType type = laivyQuests().getApi().getObjectiveType(CONSUME_ITEM_OBJECTIVE_TYPE_ID);

        if (type instanceof ConsumeItemObjectiveType) {
            return (ConsumeItemObjectiveType) type;
        } else {
            throw new IllegalStateException("This objective type '" + CONSUME_ITEM_OBJECTIVE_TYPE_ID + "' isn't a instance of the consume item objective type class. (" + ConsumeItemObjectiveType.class.getName() + ")");
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
    public @Nullable IMessage getProgressMessage() {
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
