package codes.laivy.quests.quests.objectives;

import codes.laivy.quests.api.Serializer;
import codes.laivy.quests.locale.IMessage;
import org.jetbrains.annotations.NotNull;

public abstract class ObjectiveType {

    private final @NotNull String id;

    private final @NotNull Serializer<Objective> objectiveSerializer;

    public ObjectiveType(@NotNull String id, @NotNull Serializer<Objective> objectiveSerializer) {
        this.id = id;
        this.objectiveSerializer = objectiveSerializer;
    }

    public final @NotNull String getId() {
        return id;
    }

    public abstract @NotNull IMessage getName(@NotNull Objective objective);

    public abstract @NotNull IMessage getDescription(@NotNull Objective objective);

    public final @NotNull Serializer<Objective> getSerializer() {
        return objectiveSerializer;
    }
}
