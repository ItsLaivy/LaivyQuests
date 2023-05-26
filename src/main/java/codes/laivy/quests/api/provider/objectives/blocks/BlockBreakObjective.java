package codes.laivy.quests.api.provider.objectives.blocks;

import codes.laivy.quests.api.provider.quest.ObjectiveProvider;
import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.quests.objectives.ObjectiveType;
import codes.laivy.quests.quests.objectives.complements.Progressable;
import codes.laivy.quests.quests.objectives.complements.Rewardable;
import codes.laivy.quests.quests.objectives.reward.Reward;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import static codes.laivy.quests.LaivyQuests.laivyQuests;
import static codes.laivy.quests.api.provider.objectives.blocks.BlockBreakObjectiveType.BLOCK_BREAK_OBJECTIVE_TYPE_ID;

public class BlockBreakObjective extends ObjectiveProvider implements Progressable, Rewardable {

    private final @NotNull Material material;
    private final @Range(from = 1, to = Integer.MAX_VALUE) int meta;
    private @Range(from = 0, to = Integer.MAX_VALUE) int progress;

    private final @Nullable Reward reward;

    public BlockBreakObjective(
            @NotNull IMessage name,
            @NotNull IMessage description,

            @NotNull Material material,
            @Range(from = 1, to = Integer.MAX_VALUE) int meta,
            @Range(from = 0, to = Integer.MAX_VALUE) int progress,
            @Nullable Reward reward
    ) {
        super(name, description);

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

    @Override
    public @NotNull BlockBreakObjectiveType getType() {
        ObjectiveType type = laivyQuests().getApi().getObjectiveType(BLOCK_BREAK_OBJECTIVE_TYPE_ID);

        if (type instanceof BlockBreakObjectiveType) {
            return (BlockBreakObjectiveType) type;
        } else {
            throw new IllegalStateException("This objective type '" + BLOCK_BREAK_OBJECTIVE_TYPE_ID + "' isn't a instance of the break blocks objective type class. (" + BlockBreakObjectiveType.class.getName() + ")");
        }
    }

    @Override
    public boolean isCompleted() {
        return getProgress() >= getMeta();
    }

    @Override
    protected void abstractComplete() {

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
