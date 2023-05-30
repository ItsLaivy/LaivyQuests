package codes.laivy.quests.api.provider.objectives.blocks.mechanic.located;

import codes.laivy.quests.api.provider.objectives.blocks.mechanic.BlockType;
import codes.laivy.quests.api.provider.objectives.blocks.mechanic.IBlock;
import codes.laivy.quests.locale.IMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import static codes.laivy.quests.LaivyQuests.laivyQuests;
import static codes.laivy.quests.api.provider.objectives.blocks.mechanic.located.LocatedBlockType.LOCATED_BLOCK_TYPE;

public class LocatedBlock implements IBlock {

    private final @NotNull Location location;

    public LocatedBlock(@NotNull Location location) {
        this.location = location;
    }

    public @NotNull Location getLocation() {
        return location;
    }

    @Override
    public @NotNull Material getMaterial() {
        return getLocation().getBlock().getType();
    }

    @Override
    public @NotNull IMessage getName() {
        return laivyQuests().getMessageStorage().getMessage("Block types: located block name", getLocation().getBlockX(), getLocation().getBlockY(), getLocation().getBlockZ(), getLocation().getWorld().getName());
    }

    @Override
    public <T extends IBlock> @NotNull BlockType<T> getType() {
        //noinspection unchecked
        return (BlockType<T>) laivyQuests().getApi().getBlockType(LOCATED_BLOCK_TYPE);
    }

    @Override
    public boolean equals(@NotNull Block block) {
        return block.getLocation().equals(getLocation());
    }
}
