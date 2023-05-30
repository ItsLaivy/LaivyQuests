package codes.laivy.quests.api.provider.objectives.items.mechanic.provider;

import codes.laivy.quests.api.Serializer;
import codes.laivy.quests.api.provider.objectives.items.mechanic.ItemType;
import com.cryptomorin.xseries.XMaterial;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ItemTypeProvider extends ItemType<ItemProvider> {

    public static final @NotNull String ITEM_PROVIDER_TYPE = "ITEM_PROVIDER";

    public ItemTypeProvider() {
        super(
                ITEM_PROVIDER_TYPE,
                new Serializer<ItemProvider>() {
                    @Override
                    public @NotNull JsonElement serialize(@NotNull ItemProvider item) {
                        JsonObject object = new JsonObject();
                        // TODO: 29/05/2023 Improve item serialization
                        object.addProperty("item", item.getItemStack().getType().name());
                        return object;
                    }

                    @Override
                    public @NotNull ItemProvider deserialize(@NotNull JsonElement item) {
                        JsonObject object = item.getAsJsonObject();
                        @NotNull Material material = Objects.requireNonNull(XMaterial.valueOf(object.get("item").getAsString()).parseMaterial());

                        return new ItemProvider(new ItemStack(material));
                    }
                }
        );
    }
}
