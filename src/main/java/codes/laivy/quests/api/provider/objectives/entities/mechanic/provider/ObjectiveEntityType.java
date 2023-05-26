package codes.laivy.quests.api.provider.objectives.entities.mechanic.provider;

import codes.laivy.quests.api.Serializer;
import codes.laivy.quests.api.provider.objectives.entities.mechanic.EntityType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public class ObjectiveEntityType extends EntityType<ObjectiveEntity> {

    public static final @NotNull String ENTITY_PROVIDER_TYPE = "ENTITY_PROVIDER";

    public ObjectiveEntityType() {
        super(
                ENTITY_PROVIDER_TYPE,
                new Serializer<ObjectiveEntity>() {
                    @Override
                    public @NotNull JsonElement serialize(@NotNull ObjectiveEntity entity) {
                        JsonObject object = new JsonObject();
                        object.addProperty("type", entity.getEntityType().name());
                        return object;
                    }

                    @Override
                    public @NotNull ObjectiveEntity deserialize(@NotNull JsonElement entity) {
                        JsonObject object = entity.getAsJsonObject();
                        org.bukkit.entity.EntityType uuid = org.bukkit.entity.EntityType.valueOf(object.get("type").getAsString());
                        return new ObjectiveEntity(uuid);
                    }
                }
        );
    }
}
