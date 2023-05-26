package codes.laivy.quests.api.provider.objectives.entities.mechanic.provider;

import codes.laivy.quests.api.provider.objectives.entities.mechanic.EntityType;
import codes.laivy.quests.api.provider.objectives.entities.mechanic.IEntity;
import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.utils.EntityUtils;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import static codes.laivy.quests.LaivyQuests.laivyQuests;
import static codes.laivy.quests.api.provider.objectives.entities.mechanic.provider.ObjectiveEntityType.ENTITY_PROVIDER_TYPE;

public class ObjectiveEntity implements IEntity {

    private final @NotNull org.bukkit.entity.EntityType entityType;

    public ObjectiveEntity(@NotNull org.bukkit.entity.EntityType entityType) {
        this.entityType = entityType;
    }

    public @NotNull org.bukkit.entity.EntityType getEntityType() {
        return entityType;
    }

    @Override
    public @NotNull <T extends IEntity> EntityType<T> getType() {
        //noinspection unchecked
        EntityType<T> type = (EntityType<T>) laivyQuests().getApi().getEntityType(ENTITY_PROVIDER_TYPE);

        if (type instanceof ObjectiveEntityType) {
            return type;
        } else {
            throw new IllegalStateException("This block type '" + ENTITY_PROVIDER_TYPE + "' isn't a instance of the objective entity type class. (" + ObjectiveEntityType.class.getName() + ")");
        }
    }

    @Override
    public @NotNull IMessage getName() {
        return IMessage.from("EN_US", EntityUtils.convertToBeautifulName(getEntityType()));
    }

    @Override
    public boolean equals(@NotNull Entity entity) {
        return entity.getType().equals(getEntityType());
    }
}
