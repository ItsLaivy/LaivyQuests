package codes.laivy.quests.api.provider.objectives.entities.mechanic;

import codes.laivy.quests.api.Serializer;
import org.jetbrains.annotations.NotNull;

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

}
