package codes.laivy.quests.api.provider.objectives.blocks.mechanic.located;

import codes.laivy.quests.api.Serializer;
import codes.laivy.quests.api.provider.objectives.blocks.mechanic.BlockType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class LocatedBlockType extends BlockType<LocatedBlock> {

    public static final @NotNull String LOCATED_BLOCK_TYPE = "LOCATED_BLOCK";

    public LocatedBlockType() {
        super(
                LOCATED_BLOCK_TYPE,
                new Serializer<LocatedBlock>() {
                    @Override
                    public @NotNull JsonElement serialize(@NotNull LocatedBlock block) {
                        JsonObject object = new JsonObject();

                        Location l = block.getLocation();

                        object.addProperty("x", l.getBlockX());
                        object.addProperty("y", l.getBlockZ());
                        object.addProperty("z", l.getBlockZ());
                        object.addProperty("world", l.getWorld().getUID().toString());

                        return object;
                    }

                    @Override
                    public @NotNull LocatedBlock deserialize(@NotNull JsonElement block) {
                        JsonObject object = block.getAsJsonObject();

                        @Nullable World world = Bukkit.getWorld(UUID.fromString(object.get("world").getAsString()));

                        if (world == null) {
                            throw new NullPointerException("Couldn't find a world with unique id '" + object.get("world").getAsString() + "'");
                        }

                        Location location = new Location(
                                world,
                                object.get("x").getAsDouble(),
                                object.get("y").getAsDouble(),
                                object.get("z").getAsDouble()
                        );

                        return new LocatedBlock(location);
                    }
                }
        );
    }
}
