package codes.laivy.quests.api.provider.objectives.entities.mechanic;

import codes.laivy.quests.api.Serializer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EntityType<T extends IEntity> {

    private final @NotNull String id;
    private final @NotNull Serializer<T> serializer;

    public EntityType(@NotNull String id, @NotNull Serializer<T> serializer) {
        this.id = id;
        this.serializer = serializer;
    }

    public @NotNull String getId() {
        return id;
    }

    public @NotNull Serializer<T> getSerializer() {
        return serializer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityType)) return false;
        EntityType<?> that = (EntityType<?>) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
