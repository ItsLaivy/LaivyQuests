package codes.laivy.quests;

import codes.laivy.quests.api.QuestsApi;
import codes.laivy.quests.api.provider.QuestsApiProvider;
import codes.laivy.quests.api.provider.objectives.blocks.BlockBreakObjectiveType;
import codes.laivy.quests.api.provider.objectives.CategoryObjectiveType;
import codes.laivy.quests.api.provider.objectives.blocks.BlockPlaceObjectiveType;
import codes.laivy.quests.api.provider.objectives.blocks.mechanic.located.LocatedBlockType;
import codes.laivy.quests.api.provider.objectives.blocks.mechanic.material.MaterialBlockType;
import codes.laivy.quests.api.provider.objectives.entities.fish.FishingObjectiveType;
import codes.laivy.quests.api.provider.objectives.entities.kill.EntityKillObjectiveType;
import codes.laivy.quests.api.provider.objectives.entities.mechanic.provider.ObjectiveEntityType;
import codes.laivy.quests.api.provider.objectives.items.consume.ConsumeItemObjectiveType;
import codes.laivy.quests.api.provider.objectives.items.craft.CraftItemObjectiveType;
import codes.laivy.quests.api.provider.objectives.items.mechanic.provider.ItemTypeProvider;
import codes.laivy.quests.compatibility.Compatibility;
import codes.laivy.quests.compatibility.LvMultiplesLanguagesCompatibility;
import codes.laivy.quests.compatibility.VaultCompatibility;
import codes.laivy.quests.internal.UpdateManager;
import codes.laivy.quests.internal.UpdateManagerProvider;
import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.locale.IMessageStorage;
import codes.laivy.quests.locale.provider.MessageStorageProvider;
import codes.laivy.quests.quests.objectives.reward.money.MoneyReward;
import codes.laivy.quests.utils.JsonUtils;
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
        add(new VaultCompatibility());
    }};

    public static @NotNull Set<Compatibility> getCompatibilities() {
        return compatibilities;
    }
    public static <T extends Compatibility> @Nullable T getCompatibility(@NotNull String name) {
        //noinspection unchecked
        return (T) compatibilities.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
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
        try {
            this.updateManager = new UpdateManagerProvider(this, getDescription().getVersion(), getConfig().getBoolean("check-updates", true));
            // Version manager
            @NotNull String versionName = getConfig().getString("version", getDescription().getVersion());

            @Nullable UpdateManager.Update update = getUpdateManager().getUpdate(versionName);
            @NotNull UpdateManager.Update currentUpdate = getUpdateManager().getUpdate();

            if (update == null) {
                throw new NullPointerException("Couldn't find this version properties '" + versionName + "'");
            } else if (!update.equals(currentUpdate)) {
                getConfig().set("version", currentUpdate.getName());
            }
        } catch (Throwable e) {
            e.printStackTrace();
            log(TextComponent.fromLegacyText("§cCouldn't look for updates."));
        }

        // Load block types
        getApi().getBlockTypes().add(new LocatedBlockType());
        getApi().getBlockTypes().add(new MaterialBlockType());
        // Load entity types
        getApi().getEntityTypes().add(new ObjectiveEntityType());
        // Load item types
        getApi().getItemTypes().add(new ItemTypeProvider());
        // Load objective types
        getApi().getObjectiveTypes().add(new BlockBreakObjectiveType());
        getApi().getObjectiveTypes().add(new BlockPlaceObjectiveType());

        getApi().getObjectiveTypes().add(new EntityKillObjectiveType());
        getApi().getObjectiveTypes().add(new FishingObjectiveType());

        getApi().getObjectiveTypes().add(new ConsumeItemObjectiveType());
        getApi().getObjectiveTypes().add(new CraftItemObjectiveType());

        getApi().getObjectiveTypes().add(new CategoryObjectiveType());
        // Load reward types
        getApi().getRewardTypes().add(new MoneyReward.Type());
        // Load api
        getApi().load();

        for (Compatibility compatibility : getCompatibilities()) {
            if (compatibility.isCompatible()) {
                log(TextComponent.fromLegacyText("§9Trying to enable compatibility with §e" + compatibility.getName() + " " + compatibility.getPlugin().getDescription().getVersion() + "§9."));
                try {
                    if (compatibility.hook(this)) {
                        log(TextComponent.fromLegacyText("§9Successfully hooked §e" + compatibility.getName() + " " + compatibility.getPlugin().getDescription().getVersion() + "§9!"));
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    log(TextComponent.fromLegacyText("§cCouldn't hook §6" + compatibility.getName() + " §cwith the §6LaivyQuests §cplugin"));
                }
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
        @NotNull Map<String, Set<String>> legacies = new HashMap<>();

        ConfigurationSection section = yaml.getConfigurationSection("locales");
        for (String message : Objects.requireNonNull(section).getKeys(false)) {
            component.putIfAbsent(message, new LinkedHashMap<>());
            for (Map.Entry<String, Object> entry : Objects.requireNonNull(section.getConfigurationSection(message)).getValues(false).entrySet()) {
                String locale = entry.getKey().toUpperCase();

                if (entry.getValue() instanceof List) {
                    List<BaseComponent> components = new LinkedList<>();

                    //noinspection unchecked
                    for (Object object : (List<Object>) entry.getValue()) {
                        @NotNull String string = String.valueOf(object);

                        if (JsonUtils.isJson(string)) { // Check if is not legacy
                            components.add(new TextComponent(ComponentSerializer.parse(string)));
                        } else {
                            components.add(new TextComponent(string));

                            legacies.putIfAbsent(message, new LinkedHashSet<>());
                            legacies.get(message).add(locale);
                        }
                    }

                    component.get(message).put(locale, components.toArray(new BaseComponent[0]));

                    arrays.putIfAbsent(message, new HashSet<>());
                    arrays.get(message).add(locale);
                } else {
                    @NotNull String string = String.valueOf(entry.getValue());
                    BaseComponent[] v;

                    if (JsonUtils.isJson(string)) { // Check if is not legacy
                        v = ComponentSerializer.parse(string);
                    } else {
                        v = new BaseComponent[] { new TextComponent(string) };

                        // Declare legacy text
                        legacies.putIfAbsent(message, new LinkedHashSet<>());
                        legacies.get(message).add(locale);
                    }

                    component.get(message).put(locale, v);
                }
            }
        }

        MessageStorageProvider provider = new MessageStorageProvider(defaultLocale, component);
        provider.getArrays().putAll(arrays);
        provider.getLegacies().putAll(legacies);
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
