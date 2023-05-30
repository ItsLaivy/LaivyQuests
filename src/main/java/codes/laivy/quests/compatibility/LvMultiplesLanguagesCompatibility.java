package codes.laivy.quests.compatibility;

import codes.laivy.mlanguage.api.bukkit.BukkitMessage;
import codes.laivy.mlanguage.api.bukkit.BukkitMessageStorage;
import codes.laivy.mlanguage.lang.Locale;
import codes.laivy.mlanguage.main.BukkitMultiplesLanguages;
import codes.laivy.quests.LaivyQuests;
import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.locale.provider.MessageProvider;
import codes.laivy.quests.locale.provider.MessageStorageProvider;
import codes.laivy.quests.locale.IMessageStorage;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static codes.laivy.mlanguage.main.BukkitMultiplesLanguages.multiplesLanguagesBukkit;

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
    public boolean hook(@NotNull LaivyQuests plugin) {
        @NotNull BukkitMultiplesLanguages m = getPlugin();

        IMessageStorage oldStorage = plugin.getMessageStorage();

        BukkitMessageStorage storage = m.getApi().createStorage(
                plugin,
                "LaivyQuests general",
                convert(oldStorage.getDefaultLocale()),
                new LinkedHashSet<BukkitMessage>() {{
                    for (Map.Entry<String, Map<String, BaseComponent[]>> entry : oldStorage.getData().entrySet()) {
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

                        Set<Locale> legacies = new LinkedHashSet<>();
                        if (oldStorage.getLegacies().containsKey(id)) {
                            for (String localeStr : oldStorage.getLegacies().get(id)) {
                                legacies.add(convert(localeStr));
                            }
                        }

                        message.getLegacyTexts().addAll(legacies);
                        add(message);
                    }
                }}
        );

        for (BukkitMessage message : storage.getMessages()) {
            System.out.println("Key: '" + message.getId() + "', values: '" + message.getData().keySet() + "'");
        }

        plugin.setMessageStorage(new MessageStorageMultiplesLanguagesProvider(storage));
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

    public static class MessageMultiplesLanguagesProvider extends MessageProvider {

        private final @NotNull BukkitMessage message;

        public MessageMultiplesLanguagesProvider(@NotNull BukkitMessage message, @NotNull Object... replaces) {
            super(message.getId(), new HashMap<>());

            this.message = message.clone();
            getMessage().getReplacements().addAll(Arrays.asList(fixReplaces(replaces)));
        }

        @Override
        public @NotNull Map<String, BaseComponent[]> getData() {
            Map<String, BaseComponent[]> map = new LinkedHashMap<>();
            for (Map.Entry<@NotNull Locale, BaseComponent @NotNull []> entry : getMessage().getData().entrySet()) {
                map.put(entry.getKey().name(), entry.getValue());
            }
            return map;
        }

        @Override
        public @NotNull BaseComponent[] getText(@Nullable String locale, @NotNull Object... replaces) {
            Locale loc;
            if (locale == null) {
                loc = getMessage().getData().keySet().stream().findFirst().orElseThrow(() -> new NullPointerException("Message without data '" + getId() + "'"));
            } else {
                loc = convert(locale);
            }

            return getMessage().getText(loc, fixReplaces(replaces));
        }

        @Override
        public @NotNull List<BaseComponent[]> getArray(@Nullable String locale, @NotNull Object... replaces) {
            Locale loc;
            if (locale == null) {
                loc = getMessage().getData().keySet().stream().findFirst().orElseThrow(() -> new NullPointerException("Message without data '" + getId() + "'"));
            } else {
                loc = convert(locale);
            }

            return getMessage().getArray(loc, fixReplaces(replaces));
        }

        protected @NotNull BukkitMessage getMessage() {
            return message;
        }
    }
    public static class MessageStorageMultiplesLanguagesProvider extends MessageStorageProvider {

        private final @NotNull BukkitMessageStorage storage;

        public MessageStorageMultiplesLanguagesProvider(@NotNull BukkitMessageStorage storage) {
            super(storage.getDefaultLocale().name(), new HashMap<>());
            this.storage = storage;
        }

        public @NotNull BukkitMessageStorage getStorage() {
            return storage;
        }

        @Override
        public @NotNull Map<String, Map<String, BaseComponent[]>> getData() {
            Map<String, Map<String, BaseComponent[]>> map = new LinkedHashMap<>();

            for (BukkitMessage message : getStorage().getMessages()) {
                map.put(message.getId(), new LinkedHashMap<>());

                for (Map.Entry<@NotNull Locale, BaseComponent @NotNull []> entry : message.getData().entrySet()) {
                    map.get(message.getId()).put(entry.getKey().name(), entry.getValue());
                }
            }

            return map;
        }

        @Override
        public @NotNull BaseComponent[] get(@Nullable String locale, @NotNull String message, @NotNull Object... replaces) {
            return getMessage(message).getText(locale, fixReplaces(replaces));
        }

        @Override
        public @NotNull List<BaseComponent[]> getArray(@Nullable String locale, @NotNull String message, Object... replaces) {
            return getMessage(message).getArray(locale, fixReplaces(replaces));
        }

        @Override
        public @NotNull IMessage getMessage(@NotNull String id, @NotNull Object... replaces) {
            return new MessageMultiplesLanguagesProvider(getStorage().getMessage(id), replaces);
        }
    }

    private static @NotNull Object[] fixReplaces(@NotNull Object... replaces) {
        List<Object> replacesList = new LinkedList<>();
        for (Object replace : replaces) {
            if (replace instanceof IMessage) {
                IMessage message = (IMessage) replace;
                String id = message.getId();

                Map<Locale, BaseComponent[]> data = new LinkedHashMap<>();
                for (Map.Entry<String, BaseComponent[]> map : message.getData().entrySet()) {
                    data.put(convert(map.getKey()), map.getValue());
                }

                replace = multiplesLanguagesBukkit().getApi().createMessage(id, data);
            }
            replacesList.add(replace);
        }
        return replacesList.toArray(new Object[0]);
    }
}
