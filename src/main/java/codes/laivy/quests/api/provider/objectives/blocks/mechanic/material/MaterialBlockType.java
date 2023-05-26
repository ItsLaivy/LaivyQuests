package codes.laivy.quests.api.provider.objectives.blocks.mechanic.material;

import codes.laivy.quests.api.Serializer;
import codes.laivy.quests.api.provider.objectives.blocks.mechanic.BlockType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class MaterialBlockType extends BlockType<MaterialBlock> {

    public static final @NotNull String MATERIAL_BLOCK_TYPE = "MATERIAL_BLOCK";

    public MaterialBlockType() {
        super(
                MATERIAL_BLOCK_TYPE,
                new Serializer<MaterialBlock>() {
                    @Override
                    public @NotNull JsonElement serialize(@NotNull MaterialBlock block) {
                        JsonObject object = new JsonObject();
                        object.addProperty("material", block.getMaterial().name());
                        return object;
                    }

                    @Override
                    public @NotNull MaterialBlock deserialize(@NotNull JsonElement block) {
                        JsonObject object = block.getAsJsonObject();
                        Material material = Material.valueOf(object.get("material").getAsString());
                        return new MaterialBlock(material);
                    }
                }
        );
    }
}
