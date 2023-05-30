package codes.laivy.quests.inventory;

import codes.laivy.quests.api.provider.objectives.CategoryObjective;
import codes.laivy.quests.quests.objectives.Objective;
import codes.laivy.quests.quests.Quest;
import codes.laivy.quests.quests.QuestsPlayerData;
import codes.laivy.quests.quests.objectives.complements.Progressable;
import codes.laivy.quests.quests.objectives.complements.Rewardable;
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

    // TODO: 26/05/2023 Improved this shit lol
    protected int writeObjective(@NotNull List<BaseComponent> objectives, @NotNull Collection<Objective> allObjectives, int total, int sub, int row, int indent, @NotNull String locale, @NotNull Objective objective) {
        if (total >= 6) {
            int rest = allObjectives.size() - (new LinkedList<>(allObjectives).indexOf(objective));
            BaseComponent line = new TextComponent(
                    printIndent(indent),
                    new TextComponent(laivyQuests().getMessageStorage().get(locale, "Quests main menus: more quests", rest))
            );

            objectives.add(line);

            return -1;
        }

        if (!objective.isCompleted()) {
            objectives.add(new TextComponent(
                    printIndent(indent),
                    new TextComponent("§c" + row + (sub > 0 ? "." + sub : "") + ". "),
                    new TextComponent(objective.getName().getText(locale))
            ));
        } else {
            objectives.add(new TextComponent(
                    printIndent(indent),
                    new TextComponent("§c" + row + (sub > 0 ? "." + sub : "") + ". "),
                    new TextComponent(objective.getName().getText(locale)),
                    new TextComponent(" "),
                    new TextComponent(laivyQuests().getMessageStorage().get(locale, "Objective types: completed name"))
            ));
        }

        if (objective instanceof CategoryObjective) {
            CategoryObjective category = (CategoryObjective) objective;

            int subC = 1;
            for (Objective categoryObjective : category.getExtras()) {
                int value = writeObjective(objectives, category.getExtras(), total, subC, row, (indent + 1), locale, categoryObjective);

                if (value == -1) {
                    break;
                }

                total = value;
                subC++;
            }

            return total;
        }

        total++;

        int complements = 0;
        if (objective instanceof Progressable && ((Progressable) objective).getProgressMessage() != null) {
            complements++;
        }
        if (objective instanceof Rewardable && ((Rewardable) objective).getRewardMessage() != null) {
            complements++;
        }

        if (sub > 0) indent++;

        objectives.add(new TextComponent(
                printIndent(indent + 1),
                new TextComponent("§8" + getTreeString(complements) + "═ "),
                new TextComponent("§7"),
                new TextComponent(objective.getType().getName(objective).getText(locale))
        ));
        complements--;

        if (objective instanceof Progressable) {
            Progressable progressable = (Progressable) objective;
            if (progressable.getProgressMessage() != null) {
                objectives.add(new TextComponent(
                        printIndent(indent + 1),
                        new TextComponent("§8" + getTreeString(complements) + "═ "),
                        new TextComponent("§7"),
                        new TextComponent(progressable.getProgressMessage().getText(locale))
                ));
                complements--;
            }
        }
        if (objective instanceof Rewardable) {
            Rewardable rewardable = (Rewardable) objective;
            if (rewardable.getRewardMessage() != null) {
                objectives.add(new TextComponent(
                        printIndent(indent + 1),
                        new TextComponent("§8" + getTreeString(complements) + "═ "),
                        new TextComponent("§7"),
                        new TextComponent(rewardable.getRewardMessage().getText(locale))
                ));
                complements--;
            }
        }

        return total;
    }

    private @NotNull String getTreeString(int complements) {
        return (complements <= 0 ? "╚" : "╠");
    }

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
            int total = 0;

            for (Objective objective : quest.getObjectives(false)) {
                int indentLevel = 1;
                int value = writeObjective(objectives, quest.getObjectives(false), total, 0, row, indentLevel, locale, objective);

                if (value == -1) {
                    break;
                }

                total = value;
                row++;
            }

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
        return new TextComponent(StringUtils.repeat("   ", indentLevel));
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
