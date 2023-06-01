package codes.laivy.quests.compatibility.laivynpc.npc;

import codes.laivy.npc.types.NPC;
import codes.laivy.quests.api.Serializer;
import codes.laivy.quests.api.provider.objectives.entities.mechanic.EntityType;
import codes.laivy.quests.locale.IMessage;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import static codes.laivy.quests.LaivyQuests.laivyQuests;

public class LaivyNpcTypeProvider extends EntityType<LaivyNpcProvider> {

    public static final @NotNull String LAIVYNPC_NPC_PROVIDER_TYPE = "LAIVYNPC_NPC";

    public LaivyNpcTypeProvider() {
        super(
                LAIVYNPC_NPC_PROVIDER_TYPE,
                new Serializer<LaivyNpcProvider>() {
                    @Override
                    public @NotNull JsonElement serialize(@NotNull LaivyNpcProvider entity) {
                        JsonObject object = new JsonObject();

                        object.addProperty("name", entity.getName().getId());
                        object.addProperty("npc", entity.getNPC().getId());

                        return object;
                    }

                    @Override
                    public @NotNull LaivyNpcProvider deserialize(@NotNull JsonElement entity) {
                        JsonObject object = entity.getAsJsonObject();

                        IMessage name = laivyQuests().getMessageStorage().getMessage(object.get("name").getAsString());
                        int id = object.get("npc").getAsInt();

                        if (!NPC.NPCS_ID.containsKey(id)) {
                            throw new IllegalArgumentException("Couldn't find the NPC with id '" + id + "'");
                        }

                        return new LaivyNpcProvider(NPC.NPCS_ID.get(id), name);
                    }
                }
        );
    }
}
