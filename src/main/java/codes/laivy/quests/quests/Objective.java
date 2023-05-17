package codes.laivy.quests.quests;

import codes.laivy.quests.api.QuestsApi;
import codes.laivy.quests.locale.IMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class Objective {

    private final @NotNull String typeId;

    private final @NotNull IMessage name;
    private final @NotNull IMessage description;

    public Objective(@NotNull String typeId, @NotNull IMessage name, @NotNull IMessage description) {
        this.typeId = typeId;

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
        return getTypeId().equals(objective.getTypeId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getTypeId());
    }
}
