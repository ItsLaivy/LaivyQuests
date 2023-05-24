package codes.laivy.quests.api.provider.objectives.blocks;

import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.quests.objectives.Objective;
import codes.laivy.quests.quests.objectives.ObjectiveType;
import codes.laivy.quests.quests.objectives.complements.Progressable;
import codes.laivy.quests.quests.objectives.complements.Rewardable;
import codes.laivy.quests.quests.objectives.reward.Reward;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import static codes.laivy.quests.LaivyQuests.laivyQuests;
import static codes.laivy.quests.api.provider.objectives.blocks.BreakBlockObjectiveType.BREAK_BLOCKS_OBJECTIVE_TYPE_ID;

public class BreakBlockObjective implements Objective, Progressable<Integer>, Rewardable {

    private final @NotNull IMessage name;
    private final @NotNull IMessage description;

    private final @NotNull Material material;
    private final @Range(from = 1, to = Integer.MAX_VALUE) int meta;
    private @Range(from = 0, to = Integer.MAX_VALUE) int progress;

    private final @Nullable Reward reward;

    public BreakBlockObjective(
            @NotNull IMessage name,
            @NotNull IMessage description,

            @NotNull Material material,
            @Range(from = 1, to = Integer.MAX_VALUE) int meta,
            @Range(from = 0, to = Integer.MAX_VALUE) int progress,
            @Nullable Reward reward
    ) {
        this.name = name;
        this.description = description;

        this.material = material;

        this.meta = meta;
        this.progress = progress;

        this.reward = reward;

        // Security checks
        if (!material.isBlock()) {
            throw new IllegalArgumentException("This material '" + material + "' isn't a block!");
        }
    }

    public final @NotNull Material getMaterial() {
        return material;
    }

    public @Range(from = 1, to = Integer.MAX_VALUE) int getMeta() {
        return meta;
    }

    @Override
    public @NotNull BreakBlockObjectiveType getType() {
        ObjectiveType type = laivyQuests().getApi().getObjectiveType(BREAK_BLOCKS_OBJECTIVE_TYPE_ID);

        if (type instanceof BreakBlockObjectiveType) {
            return (BreakBlockObjectiveType) type;
        } else {
            throw new IllegalStateException("This objective type '" + BREAK_BLOCKS_OBJECTIVE_TYPE_ID + "' isn't a instance of the break blocks objective type class. (" + BreakBlockObjectiveType.class.getName() + ")");
        }
    }

    @Override
    public @NotNull IMessage getName() {
        return name;
    }

    @Override
    public @NotNull IMessage getDescription() {
        return description;
    }

    @Override
    public boolean isCompleted() {
        return getProgress() >= getMeta();
    }

    @Override
    public void complete() {

    }

    @Override
    public @NotNull @Range(from = 0, to = Integer.MAX_VALUE) Integer getProgress() {
        return progress;
    }

    @Override
    public void setProgress(@NotNull @Range(from = 0, to = Integer.MAX_VALUE) Integer progress) {
        this.progress = progress;
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
