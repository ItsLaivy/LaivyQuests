package codes.laivy.quests.compatibility.laivynpc.npc;

import codes.laivy.npc.types.NPC;
import codes.laivy.quests.api.provider.objectives.entities.mechanic.EntityType;
import codes.laivy.quests.api.provider.objectives.entities.mechanic.IEntity;
import codes.laivy.quests.api.provider.objectives.entities.mechanic.INPC;
import codes.laivy.quests.locale.IMessage;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import static codes.laivy.quests.LaivyQuests.laivyQuests;
import static codes.laivy.quests.compatibility.laivynpc.npc.LaivyNpcTypeProvider.LAIVYNPC_NPC_PROVIDER_TYPE;

public class LaivyNpcProvider implements INPC {

    private final @NotNull NPC npc;
    private final @NotNull IMessage name;

    public LaivyNpcProvider(@NotNull NPC npc, @NotNull IMessage name) {
        this.name = name;
        this.npc = npc;
    }
    public LaivyNpcProvider(@NotNull NPC npc, @NotNull String name) {
        this(npc, IMessage.from("EN_US", name));
    }

    @Override
    public @NotNull <T extends IEntity> EntityType<T> getType() {
        //noinspection unchecked
        return (EntityType<T>) laivyQuests().getApi().getEntityType(LAIVYNPC_NPC_PROVIDER_TYPE);
    }

    @Override
    public @NotNull IMessage getName() {
        return name;
    }

    public @NotNull NPC getNPC() {
        return npc;
    }

    @Override
    public boolean equals(@NotNull Entity entity) {
        return false;
    }
}
