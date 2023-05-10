package codes.laivy.quests.compatibility;

import codes.laivy.mlanguage.api.bukkit.IBukkitMessageStorage;
import codes.laivy.mlanguage.lang.Locale;
import codes.laivy.mlanguage.main.BukkitMultiplesLanguages;
import codes.laivy.quests.LaivyQuests;
import codes.laivy.quests.api.provider.MessageStorageProvider;
import codes.laivy.quests.locale.IMessageStorage;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class LvMultiplesLanguagesCompatibility extends Compatibility {

    public static @NotNull Locale convert(@NotNull String locale) {
        try {
            return Locale.valueOf(locale.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Couldn't find this locale '" + locale + "'", e);
        }
    }

    public LvMultiplesLanguagesCompatibility() {
        super("LvMultiplesLanguages");
    }

    @Override
    public boolean isCompatible() {
        return super.isCompatible() &&
                isClassPresent("codes.laivy.mlanguage.main.BukkitMultiplesLanguages") &&
                isClassPresent("codes.laivy.mlanguage.api.bukkit.IBukkitMultiplesLanguagesAPI") &&
                isClassPresent("codes.laivy.mlanguage.api.bukkit.natives.BukkitMessageStorage") &&
                isClassPresent("codes.laivy.mlanguage.api.bukkit.natives.BukkitMessage") &&
                isClassPresent("codes.laivy.mlanguage.api.bukkit.natives.BukkitMessageStorage");
    }

    @Override
    public void hook(@NotNull LaivyQuests plugin) {
        @NotNull BukkitMultiplesLanguages m = getPlugin();

        IMessageStorage oldStorage = plugin.getMessageStorage();

        Map<String, Map<Locale, BaseComponent[][]>> map = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, BaseComponent[]>> entry : oldStorage.getMessages().entrySet()) {
            String message = entry.getKey();
            map.putIfAbsent(message, new LinkedHashMap<>());

            for (Map.Entry<String, BaseComponent[]> entry2 : entry.getValue().entrySet()) {
                Locale locale = convert(entry2.getKey());
                BaseComponent[] messageContent = entry2.getValue();

                map.get(message).put(locale, new LinkedList<BaseComponent[]>() {{
                    add(messageContent);
                }}.toArray(new BaseComponent[0][]));
            }
        }

        IBukkitMessageStorage storage = m.getApi().create(
                plugin,
                "LaivyQuests general",
                convert(oldStorage.getDefaultLocale()),
                map
        );

        plugin.setMessageStorage(new MessageStorageMultiplesLanguagesProvider(storage));
    }

    private boolean isClassPresent(@NotNull String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException ignore) {
            return false;
        }
    }

    public static class MessageStorageMultiplesLanguagesProvider extends MessageStorageProvider {

        private final @NotNull IBukkitMessageStorage storage;

        public MessageStorageMultiplesLanguagesProvider(@NotNull IBukkitMessageStorage storage) {
            super(storage.getDefaultLocale().name(), new LinkedHashMap<>());
            this.storage = storage;
        }

        public @NotNull IBukkitMessageStorage getStorage() {
            return storage;
        }

        @Override
        public @NotNull Map<String, Map<String, BaseComponent[]>> getMessages() {
            throw new UnsupportedOperationException();
        }

        @Override
        public @NotNull BaseComponent[] get(@Nullable String locale, @NotNull String message, @NotNull Object... replaces) {
            return getStorage().getText(convert(locale != null ? locale : getDefaultLocale()), message, replaces);
        }
    }
}
