package codes.laivy.quests.api.provider.objectives.entities.kill;

import codes.laivy.quests.api.provider.objectives.entities.mechanic.IEntity;
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
import static codes.laivy.quests.api.provider.objectives.entities.kill.EntityKillObjectiveType.ENTITY_KILL_OBJECTIVE_TYPE_ID;

public class EntityKillObjective extends ObjectiveProvider implements Progressable, Rewardable {

    private final @NotNull IEntity entity;

    private final @Range(from = 1, to = Integer.MAX_VALUE) int meta;
    private @Range(from = 0, to = Integer.MAX_VALUE) int progress;

    private final @Nullable Reward reward;

    public EntityKillObjective(
            @NotNull IMessage name,
            @NotNull IMessage description,

            @NotNull IEntity entity,
            @Range(from = 1, to = Integer.MAX_VALUE) int meta,
            @Range(from = 0, to = Integer.MAX_VALUE) int progress,

            @Nullable Reward reward
    ) {
        super(name, description);

        this.entity = entity;

        this.meta = meta;
        this.progress = progress;

        this.reward = reward;
    }

    public @NotNull IEntity getEntity() {
        return entity;
    }

    public @Range(from = 1, to = Integer.MAX_VALUE) int getMeta() {
        return meta;
    }

    public @Range(from = 0, to = Integer.MAX_VALUE) int getProgress() {
        return progress;
    }
    public void setProgress(@Range(from = 0, to = Integer.MAX_VALUE) int progress) {
        this.progress = Math.min(progress, getMeta());
    }

    @Override
    protected void abstractComplete() {

    }

    @Override
    public @NotNull EntityKillObjectiveType getType() {
        ObjectiveType type = laivyQuests().getApi().getObjectiveType(ENTITY_KILL_OBJECTIVE_TYPE_ID);

        if (type instanceof EntityKillObjectiveType) {
            return (EntityKillObjectiveType) type;
        } else {
            throw new IllegalStateException("This objective type '" + ENTITY_KILL_OBJECTIVE_TYPE_ID + "' isn't a instance of the entity kill objective type class. (" + EntityKillObjectiveType.class.getName() + ")");
        }
    }

    @Override
    public boolean isCompleted() {
        return getProgress() >= getMeta();
    }

    @Override
    public @Nullable IMessage getProgressMessage() {
        return laivyQuests().getMessageStorage().getMessage("Objectives: progress message", getProgress(), getMeta());
    }

    @Override
    public @Nullable IMessage getRewardMessage() {
        return reward != null ? reward.getMessage() : null;
    }

    @Override
    public @Nullable Reward getReward() {
        return reward;
    }
}
