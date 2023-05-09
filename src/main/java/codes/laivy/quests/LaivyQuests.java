package codes.laivy.quests;

import codes.laivy.quests.api.QuestsApi;
import codes.laivy.quests.api.provider.QuestsApiProvider;
import codes.laivy.quests.internal.UpdateManager;
import codes.laivy.quests.internal.UpdateManagerProvider;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class LaivyQuests extends JavaPlugin {

    private final @NotNull UpdateManager updateManager;
    private @NotNull QuestsApi api;

    public LaivyQuests() {
        saveDefaultConfig();
        this.updateManager = new UpdateManagerProvider(getConfig().getString("version", "1.0"), getConfig().getBoolean("check-updates", true));
        this.api = new QuestsApiProvider(this);
    }

    @Override
    public void onEnable() {
        getApi().load();
    }
    @Override
    public void onDisable() {
        getApi().unload();
    }

    public void log(@NotNull BaseComponent[] component) {
        // BaseComponent[] prefix = ComponentSerializer.parse("[\"\",{\"text\":\"L\",\"color\":\"#949494\"},{\"text\":\"a\",\"color\":\"#A5A5A5\"},{\"text\":\"i\",\"color\":\"#B7B7B7\"},{\"text\":\"v\",\"color\":\"#CACACA\"},{\"text\":\"y\",\"color\":\"#DCDCDC\"},{\"text\":\"Q\",\"color\":\"#00B715\"},{\"text\":\"u\",\"color\":\"#00A716\"},{\"text\":\"e\",\"color\":\"#009717\"},{\"text\":\"s\",\"color\":\"#008816\"},{\"text\":\"t\",\"color\":\"#007915\"},{\"text\":\"s\",\"color\":\"#006A14\"}]");
        getServer().getConsoleSender().sendMessage("§8[§6" + getDescription().getName() + "§8]§7 " + TextComponent.toLegacyText(component));
    }

    public @NotNull QuestsApi getApi() {
        return api;
    }
    public void setApi(@NotNull QuestsApi api) {
        if (api == getApi()) {
            return;
        }

        boolean loaded = getApi().isLoaded();
        if (loaded) {
            getApi().unload();
            this.api = api;
            getApi().load();
        } else {
            this.api = api;
        }
    }

    public @NotNull UpdateManager getUpdateManager() {
        return updateManager;
    }
}
