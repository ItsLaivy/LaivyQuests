package codes.laivy.quests.compatibility;

import codes.laivy.quests.LaivyQuests;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class VaultCompatibility extends Compatibility {

    public VaultCompatibility() {
        super("Vault");
    }

    public @NotNull Economy getEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            throw new NullPointerException("Couldn't find a valid economy plugin");
        }
        return rsp.getProvider();
    }

    @Override
    public boolean hook(@NotNull LaivyQuests plugin) {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            plugin.log(TextComponent.fromLegacyText("§cCouldn't hook §6" + getName() + " §cwith the §dLaivyQuests §cplugin because the server doesn't have a economy plugin for §6Vault§c."));
            return false;
        }

        return true;
    }
}
