package codes.laivy.quests;

import codes.laivy.quests.api.QuestsApi;
import codes.laivy.quests.api.provider.QuestsApiProvider;
import codes.laivy.quests.compatibility.Compatibility;
import codes.laivy.quests.compatibility.LvMultiplesLanguagesCompatibility;
import codes.laivy.quests.internal.UpdateManager;
import codes.laivy.quests.internal.UpdateManagerProvider;
import codes.laivy.quests.locale.IMessageStorage;
import codes.laivy.quests.locale.MessageStorageProvider;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public final class LaivyQuests extends JavaPlugin {

    // Compatibilities
    private static final @NotNull Set<Compatibility> compatibilities = new LinkedHashSet<Compatibility>() {{
        add(new LvMultiplesLanguagesCompatibility());
    }};

    public static @NotNull Set<Compatibility> getCompatibilities() {
        return compatibilities;
    }
    //

    private @Nullable UpdateManager updateManager;
    private @NotNull QuestsApi api;
    private @NotNull IMessageStorage messageStorage;

    public LaivyQuests() {
        saveDefaultConfig();
        // Message
        messageStorage = readMessageFile();
        // Api
        this.api = new QuestsApiProvider(this);
    }

    @Override
    public void onEnable() {
        this.updateManager = new UpdateManagerProvider(this, getDescription().getVersion(), getConfig().getBoolean("check-updates", true));
        getApi().load();

        for (Compatibility compatibility : getCompatibilities()) {
            if (compatibility.isCompatible()) {
                log(TextComponent.fromLegacyText("§9Trying to enable compatibility with §e" + compatibility.getName() + " " + compatibility.getPlugin().getDescription().getVersion() + "§9."));
                try {
                    compatibility.hook(this);
                } catch (Throwable e) {
                    e.printStackTrace();
                    log(TextComponent.fromLegacyText("§cCouldn't hook §6LvMultiplesLanguages §cwith the §6LaivyQuests §cplugin"));
                }
                log(TextComponent.fromLegacyText("§9Successfully hooked §eLvMultiplesLanguages " + compatibility.getPlugin().getDescription().getVersion() + "§9!"));
            }
        }
    }

    public @NotNull IMessageStorage getMessageStorage() {
        return messageStorage;
    }
    public void setMessageStorage(@NotNull IMessageStorage messageStorage) {
        // TODO: 09/05/2023 The new message storage needs to have the same messages from the older
        this.messageStorage = messageStorage;
    }

    private @NotNull IMessageStorage readMessageFile() {
        @NotNull InputStream stream = Objects.requireNonNull(LaivyQuests.class.getResourceAsStream("/language/general.yml"));
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));

        @NotNull String defaultLocale = yaml.getString("default locale").toUpperCase();
        @NotNull Map<String, Map<String, BaseComponent[]>> component = new LinkedHashMap<>();

        ConfigurationSection section = yaml.getConfigurationSection("locales");
        for (String message : section.getKeys(false)) {
            component.putIfAbsent(message, new LinkedHashMap<>());
            for (Map.Entry<String, Object> entry : section.getConfigurationSection(message).getValues(false).entrySet()) {
                String locale = entry.getKey().toUpperCase();
                BaseComponent[] messageContent = TextComponent.fromLegacyText(String.valueOf(entry.getValue()));
                component.get(message).put(locale, messageContent);
            }
        }

        return new MessageStorageProvider(defaultLocale, component);
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
        if (updateManager == null) {
            throw new NullPointerException("Couldn't retrieve updates manager");
        }
        return updateManager;
    }
}
