package codes.laivy.quests.api.provider;

import codes.laivy.data.sql.SqlDatabase;
import codes.laivy.data.sql.SqlReceptor;
import codes.laivy.data.sql.SqlTable;
import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.mysql.MysqlDatabase;
import codes.laivy.data.sql.mysql.MysqlTable;
import codes.laivy.data.sql.mysql.natives.MysqlDatabaseNative;
import codes.laivy.data.sql.mysql.natives.MysqlReceptorNative;
import codes.laivy.data.sql.mysql.natives.MysqlTableNative;
import codes.laivy.data.sql.mysql.natives.MysqlVariableNative;
import codes.laivy.data.sql.mysql.natives.manager.MysqlManagerNative;
import codes.laivy.data.sql.mysql.variable.type.MysqlTextVariableType;
import codes.laivy.data.sql.sqlite.SqliteDatabase;
import codes.laivy.data.sql.sqlite.SqliteTable;
import codes.laivy.data.sql.sqlite.natives.SqliteDatabaseNative;
import codes.laivy.data.sql.sqlite.natives.SqliteReceptorNative;
import codes.laivy.data.sql.sqlite.natives.SqliteTableNative;
import codes.laivy.data.sql.sqlite.natives.SqliteVariableNative;
import codes.laivy.data.sql.sqlite.natives.manager.SqliteManagerNative;
import codes.laivy.data.sql.sqlite.variable.type.SqliteTextVariableType;
import codes.laivy.quests.LaivyQuests;
import codes.laivy.quests.api.QuestsApi;
import codes.laivy.quests.quests.QuestsPlayerData;
import codes.laivy.quests.quests.Quest;
import codes.laivy.quests.quests.QuestHolder;
import com.google.gson.*;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class QuestsApiProvider implements QuestsApi, Listener {

    private final @NotNull Set<Quest> quests = new LinkedHashSet<>();
    private final @NotNull Map<UUID, QuestsPlayerData> data = new LinkedHashMap<>();

    private @Nullable SqlTable table;
    private @Nullable SqlVariable variable;

    private final @NotNull LaivyQuests plugin;

    // Statistics
    private int created = 0; // Created data's
    // Statistics

    public QuestsApiProvider(@NotNull LaivyQuests plugin) {
        this.plugin = plugin;
    }

    public @NotNull LaivyQuests getPlugin() {
        return plugin;
    }

    @Override
    public @NotNull Set<Quest> getQuests() {
        if (!isLoaded()) {
            throw new IllegalStateException("The quests api isn't loaded yet");
        }

        return quests;
    }

    @Override
    public @NotNull QuestsPlayerData getPlayerData(@NotNull UUID uuid) {
        if (getData().containsKey(uuid)) { // Get existent
            return getData().get(uuid);
        } else { // Create
            QuestsPlayerData data = new QuestsPlayerDataProvider(uuid, new LinkedHashSet<>());
            getData().put(uuid, data);

            created++;
            return data;
        }
    }
 
    public @NotNull Map<UUID, QuestsPlayerData> getData() {
        if (!isLoaded()) {
            throw new IllegalStateException("The quests api isn't loaded yet");
        }

        return data;
    }

    protected void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, getPlugin());
    }
    protected void unregisterEvents() {
        HandlerList.unregisterAll(this);
    }

    public @NotNull SqlReceptor getReceptor(@NotNull UUID uuid) {
        if (getDatabase() instanceof MysqlDatabase) {
            return new MysqlReceptorNative((MysqlTable) getTable(), uuid.toString());
        } else if (getDatabase() instanceof SqliteDatabase) {
            return new SqliteReceptorNative((SqliteTable) getTable(), uuid.toString());
        } else {
            throw new UnsupportedOperationException("Unsupported database type '" + getDatabase().getClass().getName() + "'");
        }
    }

    public @NotNull SqlDatabase getDatabase() {
        return getTable().getDatabase();
    }

    public @NotNull SqlTable getTable() {
        if (!isLoaded()) {
            throw new IllegalStateException("The quests api isn't loaded yet");
        } else if (table == null) {
            throw new NullPointerException("Couldn't retrieve default api provider table");
        }

        return table;
    }

    public @NotNull SqlVariable getVariable() {
        if (!isLoaded()) {
            throw new IllegalStateException("The quests api isn't loaded yet");
        } else if (variable == null) {
            throw new NullPointerException("Couldn't retrieve default api provider variable");
        }

        return variable;
    }

    @Override
    public boolean isLoaded() {
        return table != null && variable != null;
    }

    @Override
    public void load() {
        if (isLoaded()) {
            throw new IllegalStateException("The quests api is already loaded!");
        }

        getServer().getConsoleSender().sendMessage("");
        getPlugin().log(TextComponent.fromLegacyText("§7Loading §eLaivyQuests §7default api provider..."));

        try {
            // Database
            ConfigurationSection database = getPlugin().getConfig().getConfigurationSection("database");
            SqlTable table;
            SqlVariable variable;

            Set<SqlReceptor> receptors;

            if (Objects.equals(database.getString("type"), "MYSQL")) {
                ConfigurationSection info = database.getConfigurationSection("mysql");

                @NotNull String databaseName = info.getString("database");
                @NotNull String user = info.getString("user");
                @NotNull String password = info.getString("password");
                @NotNull String address = info.getString("address");
                int port = info.getInt("port");
                @NotNull String tableName = info.getString("table");
                @NotNull String columnName = info.getString("column");

                MysqlDatabase mysql = new MysqlDatabaseNative(new MysqlManagerNative(address, user, password, port), databaseName);
                variable = new MysqlVariableNative(new MysqlTableNative(mysql, tableName), columnName, new MysqlTextVariableType(MysqlTextVariableType.Size.MEDIUMTEXT), null);
                table = variable.getTable();

                getPlugin().log(TextComponent.fromLegacyText("§7Connected to §eMySQL §7database."));
                receptors = new HashSet<>(Arrays.asList(((MysqlTableNative) table).getDatabase().getManager().getStored((MysqlTable) table)));
            } else if (Objects.equals(database.getString("type"), "SQLITE")) {
                ConfigurationSection info = database.getConfigurationSection("sqlite");

                @NotNull String path = info.getString("path");
                @NotNull String databaseName = info.getString("database");
                @NotNull String tableName = info.getString("table");
                @NotNull String columnName = info.getString("column");

                SqliteDatabase sqlite = new SqliteDatabaseNative(new SqliteManagerNative(new File(getPlugin().getDataFolder(), path)), databaseName);
                variable = new SqliteVariableNative(new SqliteTableNative(sqlite, tableName), columnName, new SqliteTextVariableType());
                table = variable.getTable();

                getPlugin().log(TextComponent.fromLegacyText("§7Connected to §eSQLite §7database."));
                receptors = new HashSet<>(Arrays.asList(((SqliteTableNative) table).getDatabase().getManager().getStored((SqliteTableNative) table)));
            } else {
                throw new NullPointerException("Couldn't find this database type '" + database.getString("type") + "'");
            }

            this.variable = variable;
            this.table = table;

            getPlugin().log(TextComponent.fromLegacyText("§7Registering listeners..."));
            registerEvents();

            getPlugin().log(TextComponent.fromLegacyText("§7Loading user data..."));

            // Statistics
            int loaded = 0;
            int error = 0;

            for (SqlReceptor receptor : receptors) {
                try {
                    receptor.load();

                    QuestsPlayerData data;

                    String json = receptor.get(variable.getId());
                    if (json != null) {
                        data = deserializeQuestData(new JsonParser().parse(json));
                    } else {
                        UUID uuid = UUID.fromString(receptor.getId());
                        data = new QuestsPlayerDataProvider(uuid, new LinkedHashSet<>());
                    }

                    getData().put(data.getUniqueId(), data);
                    loaded++;

                    receptor.unload(false);
                } catch (Throwable e) {
                    e.printStackTrace();
                    getPlugin().log(TextComponent.fromLegacyText("§cFailed on load data of user '" + receptor.getId() + "'"));
                    error++;
                }
            }

            getPlugin().log(TextComponent.fromLegacyText("§aSuccessfully loaded plugin, enjoy! :)"));
            getPlugin().log(TextComponent.fromLegacyText("§7Loading statistics: §2" + loaded + " player data's §7with §c"+ error + " error(s)"));

            getServer().getConsoleSender().sendMessage("");
        } catch (Throwable e) {
            throw new RuntimeException("Default quests api loading", e);
        }
    }

    @Override
    public void unload() {
        if (!isLoaded()) {
            throw new IllegalStateException("The quests api isn't loaded!");
        }

        getPlugin().log(TextComponent.fromLegacyText("§7Unloading §eLaivyQuests §7default api provider..."));

        int unloaded = 0;
        int errors = 0;

        for (QuestsPlayerData data : getData().values()) {
            try {
                SqlReceptor receptor = getReceptor(data.getUniqueId());

                boolean loaded = receptor.isLoaded();
                if (!loaded) receptor.load();
                receptor.set(getVariable().getId(), serializeQuestData(data));
                if (!loaded) receptor.unload(true);

                unloaded++;
            } catch (Throwable e) {
                e.printStackTrace();
                getPlugin().log(TextComponent.fromLegacyText("§cFailed unload data of user '" + data.getUniqueId() + "'"));
                errors++;
            }
        }

        getPlugin().log(TextComponent.fromLegacyText("§7Unregistering listeners..."));
        unregisterEvents();
        getPlugin().log(TextComponent.fromLegacyText("§aSuccessfully unloaded §6LaivyQuests §adefault api provider"));
        getPlugin().log(TextComponent.fromLegacyText("§7Unloading statistics: §2" + unloaded + " success(es) §7(§6" + created + " created§7) §7with §c" + errors + " error(s)"));

        getDatabase().unload();
    }

    protected @NotNull QuestHolder deserializeQuestHolder(@NotNull JsonElement element) {
        JsonObject object = element.getAsJsonObject();

        UUID uuid = UUID.fromString(object.get("uuid").getAsString());
        Quest quest;

        Optional<Quest> questOpt = getQuests().stream().filter(q -> q.getId().equals(object.get("quest").getAsString())).findFirst();
        if (questOpt.isPresent()) {
            quest = questOpt.get();
        } else {
            throw new NullPointerException("Couldn't find quest with id '" + object.get("quest").getAsString() + "' at user '" + uuid + "'");
        }

        Date start = new Date(object.get("start").getAsLong());
        @Nullable Date finish = (object.has("finish") ? new Date(object.get("finish").getAsLong()) : null);

        return new QuestHolderProvider(uuid, quest, start, finish);
    }
    protected @NotNull JsonElement serializeQuestHolder(@NotNull QuestHolder holder) {
        JsonObject object = new JsonObject();

        object.addProperty("uuid", holder.getUniqueId().toString());
        object.addProperty("quest", holder.getQuest().getId());
        object.addProperty("start", holder.getStartDate().getTime());

        if (holder.getFinishDate() != null) {
            object.addProperty("finish", holder.getFinishDate().getTime());
        }

        return object;
    }

    protected @NotNull QuestsPlayerData deserializeQuestData(@NotNull JsonElement element) {
        JsonObject object = element.getAsJsonObject();

        UUID uuid = UUID.fromString(object.get("uuid").getAsString());
        Set<QuestHolder> quests = new LinkedHashSet<>();

        for (JsonElement questElement : object.getAsJsonArray("quests")) {
            JsonObject serializedHolder = questElement.getAsJsonObject();
            quests.add(deserializeQuestHolder(serializedHolder));
        }

        return new QuestsPlayerDataProvider(uuid, quests);
    }
    protected @NotNull JsonElement serializeQuestData(@NotNull QuestsPlayerData data) {
        JsonObject object = new JsonObject();

        JsonArray quests = new JsonArray();
        for (QuestHolder holder : data.getQuests()) {
            quests.add(serializeQuestHolder(holder));
        }

        object.addProperty("uuid", data.getUniqueId().toString());
        object.add("quests", quests);

        return object;
    }

    @EventHandler
    private void join(@NotNull PlayerJoinEvent e) {
        QuestsPlayerData data = getPlayerData(e.getPlayer().getUniqueId());
    }
}