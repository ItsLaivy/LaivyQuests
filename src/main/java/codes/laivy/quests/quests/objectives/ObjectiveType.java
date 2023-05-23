package codes.laivy.quests.quests.objectives;

import codes.laivy.quests.api.Serializer;
import codes.laivy.quests.locale.IMessage;
import org.jetbrains.annotations.NotNull;

public class ObjectiveType {

    private final @NotNull String id;

    private final @NotNull Serializer<Objective> objectiveSerializer;

    private final @NotNull IMessage name;
    private final @NotNull IMessage description;

    public ObjectiveType(@NotNull String id, @NotNull IMessage name, @NotNull IMessage description, @NotNull Serializer<Objective> objectiveSerializer) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.objectiveSerializer = objectiveSerializer;
    }

    public final @NotNull String getId() {
        return id;
    }

    public final @NotNull IMessage getName() {
        return name;
    }

    public final @NotNull IMessage getDescription() {
        return description;
    }

    public final @NotNull Serializer<Objective> getSerializer() {
        return objectiveSerializer;
    }
}
