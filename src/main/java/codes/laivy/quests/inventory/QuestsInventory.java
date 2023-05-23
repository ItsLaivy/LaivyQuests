package codes.laivy.quests.inventory;

import codes.laivy.quests.api.provider.objectives.CategoryObjective;
import codes.laivy.quests.quests.objectives.Objective;
import codes.laivy.quests.quests.Quest;
import codes.laivy.quests.quests.QuestsPlayerData;
import codes.laivy.quests.utils.ComponentUtils;
import codes.laivy.quests.utils.GuiUtils;
import com.cryptomorin.xseries.XMaterial;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.*;

import static codes.laivy.quests.LaivyQuests.laivyQuests;

public class QuestsInventory extends PagedInventory {

    private final @NotNull Player player;

    /**
     * If true, only the available quests will be displayed, if false, all the quests will be displayed
     */
    private boolean onlyAvailable = false;

    private final @NotNull Set<Objective> filter = new HashSet<>();

    public QuestsInventory(@NotNull Player player) {
        super(Bukkit.createInventory(null, 54, ComponentUtils.getText(laivyQuests().getMessageStorage().get(player.getUniqueId(), "Inventory title: Quests menu"))), new ArrayList<>(), 45, 53, ComponentUtils.getLocale(player), 22);
        this.player = player;

        @NotNull String locale = ComponentUtils.getLocale(player);

        @NotNull UUID uuid = getPlayer().getUniqueId();
        @NotNull QuestsPlayerData data = laivyQuests().getApi().getPlayerData(uuid);

        Map<Quest, ItemStack> items = new HashMap<>();
        for (Quest quest : data.getQuests()) {
            List<BaseComponent> objectives = new LinkedList<>();

            int row = 1;
            for (Objective objective : quest.getObjectives()) {
                int indentLevel = 1;

                objectives.add(new TextComponent(
                        printIndent(indentLevel),
                        new TextComponent("§c" + row + ". "),
                        new TextComponent(objective.getName().getText(locale))
                ));

                if (objective instanceof CategoryObjective) {
                    indentLevel++;

                    int subRow = 1;
                    for (Objective extra : ((CategoryObjective) objective).getExtras()) {
                        objectives.add(new TextComponent(
                                printIndent(indentLevel),
                                new TextComponent("§c" + row + "." + subRow + "."),
                                new TextComponent(" "),
                                new TextComponent(extra.getName().getText(locale))
                        ));
                        subRow++;
                    }
                }

                row++;
            }

            for (BaseComponent component : objectives) {
                player.spigot().sendMessage(component);
            }
            player.sendMessage("------");

            items.put(quest, GuiUtils.getItemStack(
                    Objects.requireNonNull(XMaterial.BOOK.parseMaterial()),
                    quest.getName().getText(locale),
                    laivyQuests().getMessageStorage().getArray(locale, "Quests menu list: quest lore",
                            quest.getDescription().getText(locale),
                            objectives
                    )
            ));
        }

        getList().addAll(items.values());

        ItemStack item = GuiUtils.getItemStack(Objects.requireNonNull(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial()), new BaseComponent[] { new TextComponent("§c---/-/---") }, null);
        for (int slot : new int[] {
                0,  1,  2,  3,  4,  5,  6,  7,  8,
                9,                              17,
                18,                             26,
                27,                             35,
                36, 37, 38, 39,     41, 42, 43, 44,
                    46,                     52
        }) {
            getInventory().setItem(slot, item);
        }

        refreshAvailabilityToggle();

        getInventory().setItem(49, GuiUtils.getItemStack(Material.ARROW, laivyQuests().getMessageStorage().get(uuid, "Menus: back item name"), laivyQuests().getMessageStorage().getArray(uuid, "Menus: back item lore")));
        getInventory().setItem(50, GuiUtils.getItemStack(Material.HOPPER, laivyQuests().getMessageStorage().get(uuid, "Quests list: filter name"), laivyQuests().getMessageStorage().getArray(uuid, "Quests list: filter lore")));

        setClickAction(new PagedInventoryClickAction() {
            @Override
            public void click(@NotNull InventoryClickEvent e, @NotNull Player player) {
                e.setCancelled(true);

                if (e.getSlot() == 49) {
                    QuestMainInventory inventory = new QuestMainInventory(player);
                    player.openInventory(inventory.getViewInventory());
                } else if (e.getSlot() == 40) {
                    onlyAvailable = !onlyAvailable;
                    refreshAvailabilityToggle();
                }
            }
        });

        setBounds(
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34
        );
        setPage(0);
    }

    private @NotNull BaseComponent printIndent(@Range(from = 1, to = Integer.MAX_VALUE) int indentLevel) {
        return new TextComponent(StringUtils.repeat("  ", indentLevel));
    }

    public void refreshAvailabilityToggle() {
        @NotNull String locale = ComponentUtils.getLocale(player);

        getInventory().setItem(40, GuiUtils.getItemStack(
                Objects.requireNonNull((onlyAvailable ? XMaterial.LIME_DYE : XMaterial.GRAY_DYE).parseMaterial()),
                laivyQuests().getMessageStorage().get(locale, "Quests list: only available toggle (" + onlyAvailable + ") name"),
                laivyQuests().getMessageStorage().getArray(locale, "Quests list: only available toggle (" + onlyAvailable + ") lore")
        ));
    }

    public @NotNull Player getPlayer() {
        return player;
    }
}
