package codes.laivy.quests.quests.objectives.reward;

import codes.laivy.quests.api.Serializer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class RewardType<T extends Reward> {

    private final @NotNull String id;

    public RewardType(@NotNull String id) {
        this.id = id;
    }

    public final @NotNull String getId() {
        return id;
    }

    public abstract @NotNull Serializer<T> getSerializer();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RewardType)) return false;
        RewardType<?> that = (RewardType<?>) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
