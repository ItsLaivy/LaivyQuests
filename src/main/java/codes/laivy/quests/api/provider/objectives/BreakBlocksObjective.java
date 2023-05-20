package codes.laivy.quests.api.provider.objectives;

import codes.laivy.quests.quests.Objective;
import codes.laivy.quests.quests.QuestsPlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.stream.Collectors;

import static codes.laivy.quests.LaivyQuests.laivyQuests;

public class BreakBlocksObjective implements Objective {

    public static final class Events implements Listener {
        @EventHandler
        private void breakBlock(@NotNull BlockBreakEvent e) {
            QuestsPlayerData data = laivyQuests().getApi().getPlayerData(e.getPlayer().getUniqueId());

            Set<Objective> objectives = new HashSet<>();
            Arrays.asList(data.getQuests()).forEach(q -> objectives.addAll(q.getObjectives().stream().filter(o -> o instanceof BreakBlocksObjective).collect(Collectors.toSet())));

            for (Objective h : objectives) {
                if (h instanceof BreakBlocksObjective) {
                    final BreakBlocksObjective holder = (BreakBlocksObjective) h;
                    Material material = e.getBlock().getType();

                    if (holder.getMeta().containsKey(material)) {
                        int current = holder.getCurrent().getOrDefault(material, 0);
                        holder.getCurrent().put(material, current + 1);
                    }
                }
            }
        }
    }

    private static final @NotNull Events EVENTS = new Events();

    static {
        Bukkit.getPluginManager().registerEvents(EVENTS, laivyQuests());
    }

    private final @NotNull Map<Material, @Range(from = 1, to = Integer.MAX_VALUE) Integer> meta;
    private final @NotNull Map<Material, @Range(from = 0, to = Integer.MAX_VALUE) Integer> current;

    public BreakBlocksObjective(
            @NotNull Map<Material, @Range(from = 1, to = Integer.MAX_VALUE) Integer> meta,
            @NotNull Map<Material, @Range(from = 0, to = Integer.MAX_VALUE) Integer> current
    ) {
        this.meta = meta;
        this.current = current;

        // Security checks
        for (Material material : getMeta().keySet()) {
            int m = meta.get(material);
            if (m < 1) {
                throw new IllegalStateException("This meta material '" + material + "' objective is '" + m + "', the number needs to be higher than zero");
            }
        }
    }


    public @NotNull Map<Material, @Range(from = 1, to = Integer.MAX_VALUE) Integer> getMeta() {
        return meta;
    }

    public @NotNull Map<Material, @Range(from = 0, to = Integer.MAX_VALUE) Integer> getCurrent() {
        return current;
    }

    @Override
    public @NotNull String getId() {
        return "BREAK_BLOCKS_NATIVE";
    }

    @Override
    public boolean isCompleted() {
        boolean completed = true;

        for (Map.Entry<Material, Integer> targetEntry : getMeta().entrySet()) {
            Material material = targetEntry.getKey();
            int meta = targetEntry.getValue();

            if (getCurrent().getOrDefault(material, 0) < meta) {
                completed = false;
                break;
            }
        }

        return completed;
    }

    @Override
    public void complete() {

    }
}
