package codes.laivy.quests.quests;

import codes.laivy.quests.api.QuestsApi;
import codes.laivy.quests.locale.IMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class Objective {

    private final @NotNull String typeId;

    private final @NotNull String id;
    private final @NotNull IMessage name;
    private final @NotNull IMessage description;

    public Objective(@NotNull String typeId, @NotNull String id, @NotNull IMessage name, @NotNull IMessage description) {
        this.typeId = typeId;

        this.id = id;
        this.name = name;
        this.description = description;
    }

    /**
     * This type id is used to serialize/deserialize the objective
     * @return the type id
     * @see QuestsApi#getObjectiveSerializers()
     */
    public final @NotNull String getTypeId() {
        return typeId;
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

    public abstract @NotNull ObjectiveHolder createHolder();

    public abstract @NotNull IMessage getProgress(@NotNull ObjectiveHolder holder);

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Objective objective = (Objective) o;
        return getId().equals(objective.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getId());
    }
}
