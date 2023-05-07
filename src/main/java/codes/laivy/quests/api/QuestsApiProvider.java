package codes.laivy.quests.api;

import codes.laivy.data.sql.SqlDatabase;
import codes.laivy.data.sql.SqlTable;
import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.mysql.MysqlDatabase;
import codes.laivy.data.sql.mysql.natives.MysqlDatabaseNative;
import codes.laivy.data.sql.mysql.natives.MysqlTableNative;
import codes.laivy.data.sql.mysql.natives.MysqlVariableNative;
import codes.laivy.data.sql.mysql.natives.manager.MysqlManagerNative;
import codes.laivy.data.sql.mysql.variable.type.MysqlIntVariableType;
import codes.laivy.data.sql.sqlite.SqliteDatabase;
import codes.laivy.data.sql.sqlite.natives.SqliteDatabaseNative;
import codes.laivy.data.sql.sqlite.natives.SqliteTableNative;
import codes.laivy.data.sql.sqlite.natives.SqliteVariableNative;
import codes.laivy.data.sql.sqlite.natives.manager.SqliteManagerNative;
import codes.laivy.data.sql.sqlite.variable.type.SqliteIntVariableType;
import codes.laivy.quests.quests.Quest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public final class QuestsApiProvider implements QuestsApi {

    private final @NotNull Set<Quest> quests = new LinkedHashSet<>();

    private @Nullable SqlTable table;
    private @Nullable SqlVariable variable;

    private final @NotNull Plugin plugin;

    public QuestsApiProvider(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    public @NotNull Plugin getPlugin() {
        return plugin;
    }

    @Override
    public @NotNull Set<Quest> getQuests() {
        return quests;
    }

    @Override
    public @NotNull QuestsPlayerData getPlayerData(@NotNull UUID uuid) {
        return null;
    }

    @Override
    public void load() {
        if (isLoaded()) {
            throw new IllegalStateException("The quests api is already loaded!");
        }

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
            } else if (Objects.equals(database.getString("type"), "SQLITE")) {
                ConfigurationSection info = database.getConfigurationSection("sqlite");

                @NotNull String path = info.getString("path");
                @NotNull String databaseName = info.getString("database");
                @NotNull String tableName = info.getString("table");
                @NotNull String columnName = info.getString("column");

                SqliteDatabase sqlite = new SqliteDatabaseNative(new SqliteManagerNative(new File(getPlugin().getDataFolder(), path)), databaseName);
                variable = new SqliteVariableNative(new SqliteTableNative(sqlite, tableName), columnName, new SqliteIntVariableType());
                table = variable.getTable();
            } else {
                throw new NullPointerException("Couldn't find this database type '" + database.getString("type") + "'");
            }

            this.variable = variable;
            this.table = table;
        } catch (Throwable e) {
            throw new RuntimeException("Default quests api loading", e);
        }
    }

    public @NotNull SqlDatabase getDatabase() {
        return getTable().getDatabase();
    }

    public @NotNull SqlTable getTable() {
        if (!isLoaded()) {
            throw new IllegalStateException("The quests api isn't loaded yet");
        }
        return table;
    }

    public @NotNull SqlVariable getVariable() {
        if (!isLoaded()) {
            throw new IllegalStateException("The quests api isn't loaded yet");
        }
        return variable;
    }

    @Override
    public boolean isLoaded() {
        return table != null && variable != null;
    }

    @Override
    public void unload() {
        if (!isLoaded()) {
            throw new IllegalStateException("The quests api isn't loaded!");
        }

        getDatabase().unload();
    }
}
