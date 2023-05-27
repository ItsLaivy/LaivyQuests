package codes.laivy.quests.utils;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class GuiUtils {

    private GuiUtils() {
    }

    public static @NotNull ItemStack getItemStack(@NotNull Material material, @NotNull BaseComponent[] name, @Nullable List<BaseComponent[]> lore) {
        return getItemStack(new ItemStack(material), name, lore);
    }
    public static @NotNull ItemStack getItemStack(@NotNull ItemStack itemStack, @NotNull BaseComponent[] name, @Nullable List<BaseComponent[]> lore) {
        itemStack = itemStack.clone();
        ItemMeta meta = itemStack.getItemMeta();

        if (meta != null) {
            if (Bukkit.getPluginManager().getPlugin("LvMultiplesLanguages") != null) {
                codes.laivy.mlanguage.main.BukkitMultiplesLanguages.multiplesLanguagesBukkit().getVersion().setItemBukkitDisplayName(itemStack, name);
                codes.laivy.mlanguage.main.BukkitMultiplesLanguages.multiplesLanguagesBukkit().getVersion().setItemBukkitLore(itemStack, lore);
            } else {
                meta.setDisplayName(ComponentUtils.getText(name));

                if (lore == null) {
                    meta.setLore(null);
                } else {
                    meta.setLore(ComponentUtils.getText(lore));
                }

                itemStack.setItemMeta(meta);
            }
        }

        return itemStack;
    }

}
