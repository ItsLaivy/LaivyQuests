package codes.laivy.quests.inventory;

import codes.laivy.quests.utils.GuiUtils;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static codes.laivy.quests.LaivyQuests.laivyQuests;

/**
 * Temporary, will be removed later to a newer version
 */
public class PagedInventory {

    public interface PagedInventoryClickAction {
        default void click(@NotNull InventoryClickEvent e, @NotNull Player player) {
        }
        default void click(@NotNull Player player, int clickedSlot, int page) {
        }
    }

    public static final @NotNull PagedInventoryClickAction DEFAULT_PAGED_CLICK_ACTION = new PagedInventoryClickAction() {
        @Override
        public void click(@NotNull InventoryClickEvent e, @NotNull Player player) {
        }
        @Override
        public void click(@NotNull Player player, int clickedSlot, int page) {
        }
    };

    public static int getClickSlot(int page, int clickedSlot, int boundMax) {
        return (page * boundMax) + clickedSlot;
    }
    public static int getBoundMaxFrom(@NotNull PagedInventory pagedInventory) {
        int top = 0;
        for (Integer b : pagedInventory.bounds) {
            if (b > top) {
                top = b;
            }
        }
        return top + 1;
    }

    private static final @NotNull Map<@NotNull Inventory, @NotNull PagedInventory> PAGED_INVENTORIES = new HashMap<>();

    @SuppressWarnings("unused")
    private static final class Events implements Listener {
        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        private void clickEvent(@NotNull InventoryClickEvent e) {
            if (PAGED_INVENTORIES.containsKey(e.getView().getTopInventory())) {
                e.setCancelled(true);
                Player player = (Player) e.getWhoClicked();

                PagedInventory i = PAGED_INVENTORIES.get(e.getView().getTopInventory());
                int page = i.page;

                if (e.getSlot() == i.getPreviousSlot()) {
                    if (page > 0) {
                        i.setPage(page - 1);
                    } else {
                        player.spigot().sendMessage(laivyQuests().getMessageStorage().get(player.getUniqueId(), "Paged inventory: Cannot go previous page"));
                    }
                } else if (e.getSlot() == i.getNextSlot()) {
                    if ((i.page + 1) * i.bounds.size() < i.list.size()) {
                        i.setPage(page + 1);
                    } else {
                        player.spigot().sendMessage(laivyQuests().getMessageStorage().get(player.getUniqueId(), "Paged inventory: Cannot go next page"));
                    }
                } else if (e.getCurrentItem() != null && i.bounds.contains(e.getSlot())) {
                    i.getClickAction().click((Player) e.getWhoClicked(), getClickSlot(page, e.getSlot(), getBoundMaxFrom(i)), page);
                }

                i.getClickAction().click(e, player);
            }
        }
    }

    static {
        Bukkit.getPluginManager().registerEvents(new Events(), laivyQuests());

        Bukkit.getScheduler().runTaskTimer(laivyQuests(), () -> {
            for (Map.Entry<Inventory, PagedInventory> m : new HashSet<>(PAGED_INVENTORIES.entrySet())) {
                if (m.getKey().getViewers().size() == 0) {
                    m.getValue().remove();
                }
            }
        }, 100, 100);
    }

    private @NotNull PagedInventoryClickAction clickAction;

    private final @NotNull Inventory inventory;
    private final @NotNull List<@NotNull ItemStack> list;
    private int page;

    private final @NotNull List<@NotNull Integer> bounds = new ArrayList<>();

    private final int previousSlot;
    private final int nextSlot;

    private final @Nullable String locale;

    private @Nullable Integer blankListItemSlot;
    private @Nullable ItemStack blankListItem;

    public PagedInventory(@NotNull Inventory inventory, @NotNull List<@NotNull ItemStack> list, int previous, int next, @Nullable String locale) {
        this(inventory, list, previous, next, locale, null);
    }
    public PagedInventory(@NotNull Inventory inventory, @NotNull List<@NotNull ItemStack> list, int previous, int next, @Nullable String locale, @Nullable Integer blankListItemSlot) {
        this.locale = locale;
        this.blankListItemSlot = blankListItemSlot;

        this.blankListItem = GuiUtils.getItemStack(Objects.requireNonNull(XMaterial.COBWEB.parseMaterial()),
                laivyQuests().getMessageStorage().get(locale, "Paged inventory: Blank list item name"),
                laivyQuests().getMessageStorage().getArray(locale, "Paged inventory: Blank list item lore")
        );

        this.inventory = inventory;
        this.list = list;

        this.previousSlot = previous;
        this.nextSlot = next;

        this.clickAction = DEFAULT_PAGED_CLICK_ACTION;

        PAGED_INVENTORIES.put(inventory, this);
    }

    public @NotNull PagedInventoryClickAction getClickAction() {
        return clickAction;
    }

    public void setClickAction(@Nullable PagedInventoryClickAction clickAction) {
        if (clickAction == null) clickAction = DEFAULT_PAGED_CLICK_ACTION;
        this.clickAction = clickAction;
    }

    public @Nullable Integer getBlankListItemSlot() {
        return blankListItemSlot;
    }

    public void setBlankListItemSlot(@Nullable Integer blankListItemSlot) {
        this.blankListItemSlot = blankListItemSlot;
    }

    public @Nullable ItemStack getBlankListItem() {
        return blankListItem;
    }

    public void setBlankListItem(@Nullable ItemStack blankListItem) {
        this.blankListItem = blankListItem;
    }

    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public @NotNull List<@NotNull ItemStack> getList() {
        return list;
    }

    public int getPage() {
        return page;
    }

    public void setBounds(int zeroTo) {
        for (int row = 0; row < zeroTo; row++) {
            bounds.add(row);
        }

        setPage(0);
    }
    public void setBounds(int... onlyAt) {
        for (Integer n : onlyAt) {
            bounds.add(n);
        }

        setPage(0);
    }

    public @NotNull PagedInventory setPage(int newPage) {
        if (bounds.isEmpty()) {
            throw new NullPointerException("You need to set the bounds!");
        }

        this.page = newPage;

        int row = 0;
        for (Integer slot : bounds) {
            this.inventory.setItem(slot, new ItemStack(Material.AIR));

            int index = row + (page * bounds.size());
            if (list.size() > index) {
                this.inventory.setItem(slot, list.get(index));
            }

            row++;
        }

        inventory.setItem(nextSlot, getNextPageItem(page, locale));
        inventory.setItem(previousSlot, getPreviousPageItem(page, locale));

        if (blankListItem != null && blankListItemSlot != null) {
            if (list.size() == 0) {
                inventory.setItem(blankListItemSlot, blankListItem);
            }
        }

        return this;
    }

    public static @NotNull ItemStack getNextPageItem(int page, @Nullable String locale) {
        return GuiUtils.getItemStack(Objects.requireNonNull(XMaterial.LIME_STAINED_GLASS_PANE.parseMaterial()),
                laivyQuests().getMessageStorage().get(locale, "Paged inventory: next item name"),
                laivyQuests().getMessageStorage().getArray(locale, "Paged inventory: next item lore", page)
        );
    }
    public static @NotNull ItemStack getPreviousPageItem(int page, @Nullable String locale) {
        return GuiUtils.getItemStack(Objects.requireNonNull(XMaterial.RED_STAINED_GLASS_PANE.parseMaterial()),
                laivyQuests().getMessageStorage().get(locale, "Paged inventory: previous item name"),
                laivyQuests().getMessageStorage().getArray(locale, "Paged inventory: previous item lore", page)
        );
    }

    public @Nullable String getLocale() {
        return locale;
    }

    public @NotNull List<@NotNull Integer> getBounds() {
        return bounds;
    }

    public int getPreviousSlot() {
        return previousSlot;
    }

    public int getNextSlot() {
        return nextSlot;
    }

    public void remove() {
        PAGED_INVENTORIES.remove(inventory);
    }

}