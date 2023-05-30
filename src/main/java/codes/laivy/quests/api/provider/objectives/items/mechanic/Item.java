package codes.laivy.quests.api.provider.objectives.items.mechanic;

import codes.laivy.quests.api.QuestsApi;
import codes.laivy.quests.locale.IMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface Item {

    /**
     * This is used to serialize or deserialize this block using the api
     * @return the block type
     * @see QuestsApi#getItemTypes()
     */
    <T extends Item> @NotNull ItemType<T> getType();

    @NotNull Material getMaterial();

    /**
     * The name of the entities at the menus
     * @return the entity name
     */
    @NotNull IMessage getName();

    /**
     * @see ItemStack#isSimilar(ItemStack)
     * @param itemStack
     * @return
     */
    boolean isSimilar(@NotNull ItemStack itemStack);

    boolean equals(@NotNull ItemStack itemStack);
}
