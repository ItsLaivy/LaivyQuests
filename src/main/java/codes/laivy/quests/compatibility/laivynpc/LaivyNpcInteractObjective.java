package codes.laivy.quests.compatibility.laivynpc;

import codes.laivy.quests.api.provider.quest.ObjectiveProvider;
import codes.laivy.quests.compatibility.laivynpc.npc.LaivyNpcProvider;
import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.quests.objectives.ObjectiveType;
import codes.laivy.quests.quests.objectives.complements.Rewardable;
import codes.laivy.quests.quests.objectives.reward.Reward;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static codes.laivy.quests.LaivyQuests.laivyQuests;
import static codes.laivy.quests.compatibility.laivynpc.LaivyNpcInteractObjectiveType.LAIVYNPC_INTERACT_OBJECTIVE_TYPE_ID;

public class LaivyNpcInteractObjective extends ObjectiveProvider implements Rewardable {

    private final @NotNull LaivyNpcProvider npc;
    private final @Nullable Reward reward;
    private boolean completed;

    public LaivyNpcInteractObjective(
            @NotNull IMessage name,
            @NotNull IMessage description,

            @NotNull LaivyNpcProvider npc,
            @Nullable Reward reward
    ) {
        this(name, description, false, npc, reward);
    }

    public LaivyNpcInteractObjective(
            @NotNull IMessage name,
            @NotNull IMessage description,

            boolean completed,

            @NotNull LaivyNpcProvider npc,
            @Nullable Reward reward
    ) {
        super(name, description);

        this.completed = completed;

        this.npc = npc;
        this.reward = reward;
    }

    public @NotNull LaivyNpcProvider getNPC() {
        return npc;
    }

    @Override
    protected void abstractComplete() {

    }

    @Override
    public @NotNull LaivyNpcInteractObjectiveType getType() {
        ObjectiveType type = laivyQuests().getApi().getObjectiveType(LAIVYNPC_INTERACT_OBJECTIVE_TYPE_ID);

        if (type instanceof LaivyNpcInteractObjectiveType) {
            return (LaivyNpcInteractObjectiveType) type;
        } else {
            throw new IllegalStateException("This objective type '" + LAIVYNPC_INTERACT_OBJECTIVE_TYPE_ID + "' isn't a instance of the laivynpc interact objective type class. (" + LaivyNpcInteractObjectiveType.class.getName() + ")");
        }
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
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
