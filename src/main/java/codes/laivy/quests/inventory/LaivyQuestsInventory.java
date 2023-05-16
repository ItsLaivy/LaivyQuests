package codes.laivy.quests.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static codes.laivy.quests.LaivyQuests.laivyQuests;

public abstract class LaivyQuestsInventory {

    public static final class Events implements Listener {

        @EventHandler
        private void invClick(@NotNull InventoryClickEvent e) {
            if (INVENTORIES.containsKey(e.getInventory())) {
                LaivyQuestsInventory inventory = INVENTORIES.get(e.getInventory());
                Player p = (Player) e.getWhoClicked();

                if (!e.isCancelled()) inventory.getClickAction().interact(e, p);
                if (!e.isCancelled()) inventory.getClickAction().click(e, p);
            }
        }
        @EventHandler
        private void invDrag(@NotNull InventoryDragEvent e) {
            if (INVENTORIES.containsKey(e.getInventory())) {
                LaivyQuestsInventory inventory = INVENTORIES.get(e.getInventory());
                Player p = (Player) e.getWhoClicked();

                if (!e.isCancelled()) inventory.getClickAction().interact(e, p);
                if (!e.isCancelled()) inventory.getClickAction().drag(e, p);
            }
        }
        @EventHandler
        private void invClose(@NotNull InventoryCloseEvent e) {
            if (INVENTORIES.containsKey(e.getInventory())) {
                LaivyQuestsInventory inventory = INVENTORIES.get(e.getInventory());

                inventory.getClickAction().close(e);

                Bukkit.getScheduler().runTaskLater(laivyQuests(), () -> {
                    if (inventory.inventory.getViewers().size() == 0) {
                        inventory.remove();
                    }
                }, 5);
            }
        }
    }

    static {
        Bukkit.getPluginManager().registerEvents(new Events(), laivyQuests());
    }

    public interface InventoryAction {
        default void second(@NotNull List<@NotNull HumanEntity> viewers) {
        }
        default void interact(@NotNull InventoryInteractEvent e, @NotNull Player p) {
        }
        default void click(@NotNull InventoryClickEvent e, @NotNull Player p) {
        }
        default void drag(@NotNull InventoryDragEvent e, @NotNull Player p) {
        }
        default void close(@NotNull InventoryCloseEvent e) {
        }
    }

    public static final @NotNull Map<@NotNull Inventory, @NotNull LaivyQuestsInventory> INVENTORIES = new HashMap<>();
    public static final @NotNull Map<@NotNull String, @NotNull List<LaivyQuestsInventory>> INVENTORIES_CODE = new HashMap<>();

    private static final @NotNull InventoryAction DEFAULT_INVENTORY_CLICK = new InventoryAction() { };

    public static @NotNull List<@NotNull HumanEntity> getViewers(@NotNull String id) {
        if (INVENTORIES_CODE.containsKey(id)) {
            List<HumanEntity> viewers = new ArrayList<>();
            for (LaivyQuestsInventory inv : INVENTORIES_CODE.get(id)) {
                viewers.addAll(inv.getViewInventory().getViewers());
            }
            return viewers;
        }
        throw new NullPointerException("Couldn't find this clans inventory with id '" + id + "'");
    }

    //
    // Inventory
    //

    private final @NotNull Inventory inventory;
    private final @NotNull String id;

    private @NotNull InventoryAction click = DEFAULT_INVENTORY_CLICK;

    // TODO: 13/05/2023 BaseComponent[] title
    public LaivyQuestsInventory(@NotNull String id, @NotNull String title, int size) {
        this(id, Bukkit.createInventory(null, size, title));
    }
    public LaivyQuestsInventory(@NotNull String id, @NotNull Inventory inventory) {
        this.id = id;
        this.inventory = inventory;

        if (!INVENTORIES_CODE.containsKey(id)) {
            INVENTORIES_CODE.put(id, new ArrayList<>());
        }

        if (INVENTORIES.containsKey(this.inventory)) {
            throw new IllegalArgumentException("This inventory is already registered");
        }

        INVENTORIES.put(this.inventory, this);
        INVENTORIES_CODE.get(id).add(this);
    }

    public @NotNull InventoryAction getClickAction() {
        return click;
    }
    public void setClickAction(@Nullable InventoryAction click) {
        if (click == null) this.click = DEFAULT_INVENTORY_CLICK;
        else this.click = click;
    }

    public @NotNull String getId() {
        return id;
    }

    public void setItem(int row, @NotNull Material material) {
        this.setItem(row, new ItemStack(material));
    }
    public void setItem(int[] rows, @NotNull ItemStack item) {
        for (int row : rows) {
            this.setItem(row, item);
        }
    }
    public void setItem(int row, @NotNull ItemStack item) {
        getViewInventory().setItem(row, item);
    }

    public @NotNull Inventory getViewInventory() {
        return inventory;
    }

    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }

    public boolean isOpened() {
        return getViewInventory().getViewers().size() > 0;
    }

    public void remove() {
        INVENTORIES.remove(inventory);
        INVENTORIES_CODE.get(id).remove(this);
    }

}
