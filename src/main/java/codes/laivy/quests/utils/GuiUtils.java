package codes.laivy.quests.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class GuiUtils {

    private GuiUtils() {
    }

    public static @NotNull ItemStack getItemStack(@NotNull Material material, @NotNull String name, @NotNull List<String> lore) {
        return getItemStack(new ItemStack(material), name, lore);
    }
    public static @NotNull ItemStack getItemStack(@NotNull ItemStack itemStack, @NotNull String name, @NotNull List<String> lore) {
        itemStack = itemStack.clone();
        ItemMeta meta = itemStack.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }

        return itemStack;
    }

}
