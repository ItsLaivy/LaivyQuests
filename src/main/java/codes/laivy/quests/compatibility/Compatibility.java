package codes.laivy.quests.compatibility;

import codes.laivy.quests.LaivyQuests;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class Compatibility {

    private final @NotNull String name;

    public Compatibility(@NotNull String name) {
        this.name = name;
    }

    public boolean isCompatible() {
        return Bukkit.getPluginManager().getPlugin(getName()) != null;
    }

    public abstract void hook(@NotNull LaivyQuests plugin);

    public final @NotNull String getName() {
        return name;
    }

    public <T extends JavaPlugin> @NotNull T getPlugin() {
        if (isCompatible()) {
            //noinspection unchecked
            return (T) Bukkit.getPluginManager().getPlugin(getName());
        } else {
            throw new UnsupportedOperationException("The " + getName() + " plugin isn't supported.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Compatibility that = (Compatibility) o;
        return getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
