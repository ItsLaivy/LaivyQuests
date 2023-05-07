package codes.laivy.quests;

import codes.laivy.quests.api.QuestsApi;
import codes.laivy.quests.api.QuestsApiProvider;
import codes.laivy.quests.internal.UpdateManager;
import codes.laivy.quests.internal.UpdateManagerProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class LaivyQuests extends JavaPlugin {

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

    public @NotNull QuestsApi getApi() {
        return api;
    }
    public void setApi(@NotNull QuestsApi api) {
        this.api = api;
    }

    public @NotNull UpdateManager getUpdateManager() {
        return updateManager;
    }
}
