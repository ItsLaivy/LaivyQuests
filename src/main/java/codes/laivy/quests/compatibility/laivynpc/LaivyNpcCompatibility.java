package codes.laivy.quests.compatibility.laivynpc;

import codes.laivy.quests.LaivyQuests;
import codes.laivy.quests.compatibility.Compatibility;
import codes.laivy.quests.compatibility.laivynpc.npc.LaivyNpcTypeProvider;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

import static codes.laivy.quests.LaivyQuests.laivyQuests;

public class LaivyNpcCompatibility extends Compatibility {

    public LaivyNpcCompatibility() {
        super("LaivyNPC");
    }

    @Override
    public boolean isCompatible() {
        return super.isCompatible() &&
                isClassPresent("codes.laivy.npc.types.NPC") &&
                isClassPresent("codes.laivy.npc.LaivyNPC") &&
                isClassPresent("codes.laivy.npc.types.NPC$ClickAction");
    }

    @Override
    public boolean hook(@NotNull LaivyQuests plugin) {
        plugin.getApi().getEntityTypes().add(new LaivyNpcTypeProvider());
        plugin.getApi().getObjectiveTypes().add(new LaivyNpcInteractObjectiveType());

        return true;
    }

    private boolean isClassPresent(@NotNull String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException ignore) {
            return false;
        }
    }
}
