package codes.laivy.quests.api;

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
import codes.laivy.data.sql.mysql.variable.type.MysqlIntVariableType;
import codes.laivy.data.sql.sqlite.SqliteDatabase;
import codes.laivy.data.sql.sqlite.SqliteTable;
import codes.laivy.data.sql.sqlite.natives.SqliteDatabaseNative;
import codes.laivy.data.sql.sqlite.natives.SqliteReceptorNative;
import codes.laivy.data.sql.sqlite.natives.SqliteTableNative;
import codes.laivy.data.sql.sqlite.natives.SqliteVariableNative;
import codes.laivy.data.sql.sqlite.natives.manager.SqliteManagerNative;
import codes.laivy.data.sql.sqlite.variable.type.SqliteIntVariableType;
import codes.laivy.quests.LaivyQuests;
import codes.laivy.quests.quests.Quest;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

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

        getPlugin().log(TextComponent.fromLegacyText("§7Loading §eLaivyQuests §7default api provider..."));

        try {
            // Database
            ConfigurationSection database = getPlugin().getConfig().getConfigurationSection("database");
            SqlTable table;
            SqlVariable variable;

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
                variable = new MysqlVariableNative(new MysqlTableNative(mysql, tableName), columnName, new MysqlIntVariableType(), -1);
                table = variable.getTable();

                getPlugin().log(TextComponent.fromLegacyText("§7Connected to §eMySQL §7database."));
            } else if (Objects.equals(database.getString("type"), "SQLITE")) {
                ConfigurationSection info = database.getConfigurationSection("sqlite");

                @NotNull String path = info.getString("path");
                @NotNull String databaseName = info.getString("database");
                @NotNull String tableName = info.getString("table");
                @NotNull String columnName = info.getString("column");

                SqliteDatabase sqlite = new SqliteDatabaseNative(new SqliteManagerNative(new File(getPlugin().getDataFolder(), path)), databaseName);
                variable = new SqliteVariableNative(new SqliteTableNative(sqlite, tableName), columnName, new SqliteIntVariableType());
                table = variable.getTable();

                getPlugin().log(TextComponent.fromLegacyText("§7Connected to §eSQLite §7database."));
            } else {
                throw new NullPointerException("Couldn't find this database type '" + database.getString("type") + "'");
            }

            this.variable = variable;
            this.table = table;
        } catch (Throwable e) {
            throw new RuntimeException("Default quests api loading", e);
        }

        getPlugin().log(TextComponent.fromLegacyText("§7Registering listeners..."));
        registerEvents();
        getPlugin().log(TextComponent.fromLegacyText("§7Loading user data..."));
        getPlugin().log(TextComponent.fromLegacyText("§aSuccessfully loaded plugin, enjoy! :)"));
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
                // TODO: 09/05/2023 unloading
                //receptor.set(getVariable().getId(), );
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
        getPlugin().log(TextComponent.fromLegacyText("§7Unloading statistics: §2" + unloaded + " success(es), §6" + created + " created, §4" + errors + " error(s)"));

        getDatabase().unload();
    }
}
