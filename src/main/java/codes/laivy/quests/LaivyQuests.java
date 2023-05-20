package codes.laivy.quests;

import codes.laivy.quests.api.QuestsApi;
import codes.laivy.quests.api.provider.QuestsApiProvider;
import codes.laivy.quests.api.provider.objectives.BreakBlocksObjectiveType;
import codes.laivy.quests.compatibility.Compatibility;
import codes.laivy.quests.compatibility.LvMultiplesLanguagesCompatibility;
import codes.laivy.quests.internal.UpdateManager;
import codes.laivy.quests.internal.UpdateManagerProvider;
import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.locale.IMessageStorage;
import codes.laivy.quests.locale.provider.MessageStorageProvider;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    public static @NotNull LaivyQuests laivyQuests() {
        return JavaPlugin.getPlugin(LaivyQuests.class);
    }

    private @Nullable UpdateManager updateManager;
    private @NotNull QuestsApi api;
    private @NotNull IMessageStorage messageStorage;

    public LaivyQuests() {
        saveDefaultConfig();
        // Message
        messageStorage = readMessageFile("/language/general.yml");
        // Api
        this.api = new QuestsApiProvider(this);
    }

    @Override
    public void onEnable() {
        this.updateManager = new UpdateManagerProvider(this, getDescription().getVersion(), getConfig().getBoolean("check-updates", true));

        // Load objectives
        getApi().getObjectiveTypes().add(new BreakBlocksObjectiveType());
        // Load api
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

    @Override
    public void onDisable() {
        getApi().unload();
    }
    public @NotNull IMessageStorage getMessageStorage() {
        return messageStorage;
    }

    public void setMessageStorage(@NotNull IMessageStorage messageStorage) {
        Collection<IMessage> originalMessages = this.messageStorage.getMessages();
        Collection<IMessage> newMessages = messageStorage.getMessages();

        if (originalMessages.size() != newMessages.size() || !originalMessages.containsAll(newMessages)) {
            throw new IllegalArgumentException("This new message storage needs to contain the same messages as the old message storage (" + (originalMessages.size() != newMessages.size()) + " | " + !originalMessages.containsAll(newMessages) + ")");
        }

        this.messageStorage = messageStorage;
    }

    private @NotNull IMessageStorage readMessageFile(@NotNull String resource) {
        @NotNull InputStream stream = Objects.requireNonNull(LaivyQuests.class.getResourceAsStream(resource));
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));

        @NotNull String defaultLocale = Objects.requireNonNull(yaml.getString("default locale")).toUpperCase();
        @NotNull Map<String, Map<String, BaseComponent[]>> component = new LinkedHashMap<>();
        @NotNull Map<String, Set<String>> arrays = new HashMap<>();

        ConfigurationSection section = yaml.getConfigurationSection("locales");
        for (String message : Objects.requireNonNull(section).getKeys(false)) {
            component.putIfAbsent(message, new LinkedHashMap<>());
            for (Map.Entry<String, Object> entry : Objects.requireNonNull(section.getConfigurationSection(message)).getValues(false).entrySet()) {
                String locale = entry.getKey().toUpperCase();

                if (entry.getValue() instanceof List) {
                    Set<BaseComponent> components = new LinkedHashSet<>();

                    //noinspection unchecked
                    for (Object object : (List<Object>) entry.getValue()) {
                        components.add(new TextComponent(String.valueOf(object)));
                    }

                    component.get(message).put(locale, components.toArray(new BaseComponent[0]));

                    arrays.putIfAbsent(message, new HashSet<>());
                    arrays.get(message).add(locale);
                } else {
                    BaseComponent[] messageContent = new BaseComponent[] {
                            new TextComponent(String.valueOf(entry.getValue()))
                    };
                    component.get(message).put(locale, messageContent);
                }
            }
        }

        MessageStorageProvider provider = new MessageStorageProvider(defaultLocale, component);
        provider.getArrays().putAll(arrays);
        return provider;
    }

    public void log(@NotNull BaseComponent... component) {
        try {
            //noinspection JavaReflectionMemberAccess
            Method spigotMethod = CommandSender.class.getDeclaredMethod("spigot");
            spigotMethod.setAccessible(true);

            Object spigot = spigotMethod.invoke(getServer().getConsoleSender());

            BaseComponent[] prefix = ComponentSerializer.parse("[{\"text\":\"L\",\"color\":\"#949494\"},{\"text\":\"a\",\"color\":\"#A5A5A5\"},{\"text\":\"i\",\"color\":\"#B7B7B7\"},{\"text\":\"v\",\"color\":\"#CACACA\"},{\"text\":\"y\",\"color\":\"#DCDCDC\"},{\"text\":\"Q\",\"color\":\"#00B715\"},{\"text\":\"u\",\"color\":\"#00A716\"},{\"text\":\"e\",\"color\":\"#009717\"},{\"text\":\"s\",\"color\":\"#008816\"},{\"text\":\"t\",\"color\":\"#007915\"},{\"text\":\"s\",\"color\":\"#006A14\"}]");
            Method sendMessageMethod = spigot.getClass().getDeclaredMethod("sendMessage", BaseComponent[].class);
            sendMessageMethod.setAccessible(true);

            BaseComponent[] messageComponents = new BaseComponent[] {
                    new TextComponent(prefix),
                    new TextComponent(" §8|| "),
                    new TextComponent(component)
            };

            sendMessageMethod.invoke(spigot, new Object[] { messageComponents });
        } catch (NoSuchMethodException ignore) {
            getServer().getConsoleSender().sendMessage("§6" + getDescription().getName() + " §8|| " + TextComponent.toLegacyText(component));
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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
