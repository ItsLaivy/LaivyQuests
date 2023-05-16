package codes.laivy.quests.inventory;

import codes.laivy.quests.utils.ComponentUtils;
import codes.laivy.quests.utils.GuiUtils;
import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

import static codes.laivy.quests.LaivyQuests.laivyQuests;

public class QuestMainInventory extends LaivyQuestsInventory {

    private final @NotNull Player player;

    public QuestMainInventory(@NotNull Player player) {
        super("LaivyQuests main inventory", ComponentUtils.getText(laivyQuests().getMessageStorage().get(player.getUniqueId(), "Inventory title: Quests main menu")), 45);
        this.player = player;

        @NotNull UUID uuid = player.getUniqueId();

        setItem(
                new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 36, 37, 38, 42, 43, 44 },
                GuiUtils.getItemStack(Objects.requireNonNull(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial()), new BaseComponent[] { new TextComponent("§c---/-/---") }, null)
        );

        setItem(40, GuiUtils.getItemStack(Material.BARRIER, laivyQuests().getMessageStorage().get(uuid, "Menus: back item name"), laivyQuests().getMessageStorage().getArray(uuid, "Menus: back item lore")));

        setItem(19, GuiUtils.getItemStack(
                Objects.requireNonNull(XMaterial.BOOK.parseMaterial()),
                laivyQuests().getMessageStorage().get(uuid, "Quests main menus: quests list name"),
                laivyQuests().getMessageStorage().getArray(uuid, "Quests main menus: quests list lore")
        ));
        setItem(20, GuiUtils.getItemStack(
                Objects.requireNonNull(XMaterial.OAK_SIGN.parseMaterial()),
                laivyQuests().getMessageStorage().get(uuid, "Quests main menus: statistics name"),
                laivyQuests().getMessageStorage().getArray(uuid, "Quests main menus: statistics lore")
        ));

        setItem(22, GuiUtils.getItemStack(
                SkullUtils.getSkull(uuid),
                laivyQuests().getMessageStorage().get(uuid, "Quests main menus: information name"),
                laivyQuests().getMessageStorage().getArray(uuid, "Quests main menus: information lore")
        ));

        ItemStack fireworkStar = Objects.requireNonNull(XMaterial.FIREWORK_STAR.parseItem());
        ItemMeta meta = fireworkStar.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        fireworkStar.setItemMeta(meta);

        setItem(24, GuiUtils.getItemStack(
                fireworkStar,
                laivyQuests().getMessageStorage().get(uuid, "Quests main menus: leaderboard name"),
                laivyQuests().getMessageStorage().getArray(uuid, "Quests main menus: leaderboard lore")
        ));
        setItem(25, GuiUtils.getItemStack(
                Material.CHEST,
                laivyQuests().getMessageStorage().get(uuid, "Quests main menus: history name"),
                laivyQuests().getMessageStorage().getArray(uuid, "Quests main menus: history lore")
        ));

        setClickAction(new InventoryAction() {
            @Override
            public void click(@NotNull InventoryClickEvent e, @NotNull Player p) {
                e.setCancelled(true);

                if (e.getSlot() == 40) {
                    p.closeInventory();
                }
            }
        });
    }

    public @NotNull Player getPlayer() {
        return player;
    }
}
