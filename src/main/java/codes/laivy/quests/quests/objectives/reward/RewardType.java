package codes.laivy.quests.quests.objectives.reward;

import codes.laivy.quests.api.Serializer;
import org.jetbrains.annotations.NotNull;

public abstract class RewardType<T extends Reward> {

    private final @NotNull String id;

    public RewardType(@NotNull String id) {
        this.id = id;
    }

    public final @NotNull String getId() {
        return id;
    }

    public abstract @NotNull Serializer<T> getSerializer();
}
