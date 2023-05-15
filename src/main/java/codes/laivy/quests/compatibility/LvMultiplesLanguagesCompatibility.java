package codes.laivy.quests.compatibility;

import codes.laivy.mlanguage.api.bukkit.BukkitMessage;
import codes.laivy.mlanguage.api.bukkit.BukkitMessageStorage;
import codes.laivy.mlanguage.lang.Locale;
import codes.laivy.mlanguage.main.BukkitMultiplesLanguages;
import codes.laivy.quests.LaivyQuests;
import codes.laivy.quests.locale.MessageStorageProvider;
import codes.laivy.quests.locale.IMessageStorage;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
                isClassPresent("codes.laivy.mlanguage.api.bukkit.BukkitMessageStorage") &&
                isClassPresent("codes.laivy.mlanguage.api.bukkit.BukkitMessage");
    }

    @Override
    public void hook(@NotNull LaivyQuests plugin) {
        @NotNull BukkitMultiplesLanguages m = getPlugin();

        IMessageStorage oldStorage = plugin.getMessageStorage();

        BukkitMessageStorage storage = m.getApi().createStorage(
                plugin,
                "LaivyQuests general",
                convert(oldStorage.getDefaultLocale()),
                new LinkedHashSet<BukkitMessage>() {{
                    for (Map.Entry<String, Map<String, BaseComponent[]>> entry : oldStorage.getMessages().entrySet()) {
                        String id = entry.getKey();
                        Map<Locale, BaseComponent[]> content = new LinkedHashMap<>();

                        for (Map.Entry<String, BaseComponent[]> entry2 : entry.getValue().entrySet()) {
                            Locale locale = convert(entry2.getKey());
                            BaseComponent[] messageContent = entry2.getValue();
                            content.put(locale, messageContent);
                        }

                        BukkitMessage message = m.getApi().createMessage(id, content);

                        if (oldStorage.getArrays().containsKey(id)) {
                            Set<Locale> locales = new HashSet<>();
                            for (String localeStr : oldStorage.getArrays().get(id)) {
                                locales.add(convert(localeStr));
                            }
                            message.getArrayTexts().addAll(locales);
                        }

                        message.getLegacyTexts().addAll(content.keySet());
                        add(message);
                    }
                }}
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

        private final @NotNull BukkitMessageStorage storage;

        public MessageStorageMultiplesLanguagesProvider(@NotNull BukkitMessageStorage storage) {
            super(storage.getDefaultLocale().name(), new LinkedHashMap<>());
            this.storage = storage;
        }

        public @NotNull BukkitMessageStorage getStorage() {
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

        @Override
        public @NotNull List<BaseComponent[]> getArray(@Nullable String locale, @NotNull String message, Object... replaces) {
            return getStorage().getTextArray(convert(locale != null ? locale : getDefaultLocale()), message, replaces);
        }
    }
}