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
import codes.laivy.npc.types.NPC;
import codes.laivy.quests.LaivyQuests;
import codes.laivy.quests.api.QuestsApi;
import codes.laivy.quests.api.QuestsCommandApi;
import codes.laivy.quests.api.Serializer;
import codes.laivy.quests.api.provider.objectives.blocks.BlockBreakObjective;
import codes.laivy.quests.api.provider.objectives.CategoryObjective;
import codes.laivy.quests.api.provider.objectives.blocks.BlockPlaceObjective;
import codes.laivy.quests.api.provider.objectives.blocks.mechanic.BlockType;
import codes.laivy.quests.api.provider.objectives.blocks.mechanic.IBlock;
import codes.laivy.quests.api.provider.objectives.blocks.mechanic.material.MaterialBlock;
import codes.laivy.quests.api.provider.objectives.entities.fish.FishingObjective;
import codes.laivy.quests.api.provider.objectives.entities.kill.EntityKillObjective;
import codes.laivy.quests.api.provider.objectives.entities.mechanic.EntityType;
import codes.laivy.quests.api.provider.objectives.entities.mechanic.IEntity;
import codes.laivy.quests.api.provider.objectives.entities.mechanic.provider.ObjectiveEntity;
import codes.laivy.quests.api.provider.objectives.items.consume.ConsumeItemObjective;
import codes.laivy.quests.api.provider.objectives.items.craft.CraftItemObjective;
import codes.laivy.quests.api.provider.objectives.items.mechanic.Item;
import codes.laivy.quests.api.provider.objectives.items.mechanic.ItemType;
import codes.laivy.quests.api.provider.objectives.items.mechanic.provider.ItemProvider;
import codes.laivy.quests.api.provider.quest.QuestProvider;
import codes.laivy.quests.compatibility.laivynpc.LaivyNpcInteractObjective;
import codes.laivy.quests.compatibility.laivynpc.npc.LaivyNpcProvider;
import codes.laivy.quests.locale.IMessage;
import codes.laivy.quests.quests.*;
import codes.laivy.quests.quests.objectives.Objective;
import codes.laivy.quests.quests.objectives.ObjectiveType;
import codes.laivy.quests.quests.objectives.reward.Reward;
import codes.laivy.quests.quests.objectives.reward.RewardType;
import codes.laivy.quests.quests.objectives.reward.money.MoneyReward;
import com.cryptomorin.xseries.XMaterial;
import com.google.gson.*;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

import static codes.laivy.quests.LaivyQuests.laivyQuests;
import static org.bukkit.Bukkit.getServer;

public class QuestsApiProvider implements QuestsApi, Listener {

    private final @NotNull Set<ObjectiveType> objectiveTypes = new LinkedHashSet<>();

    private final @NotNull Set<RewardType<? extends Reward>> rewardTypes = new LinkedHashSet<>();
    private final @NotNull Set<BlockType<? extends IBlock>> blockTypes = new LinkedHashSet<>();
    private final @NotNull Set<EntityType<? extends IEntity>> entityTypes = new LinkedHashSet<>();
    private final @NotNull Set<ItemType<? extends Item>> itemTypes = new LinkedHashSet<>();

    private final @NotNull Map<UUID, QuestsPlayerData> data = new LinkedHashMap<>();

    private @Nullable SqlTable table;
    private @Nullable SqlVariable variable;

    private final @NotNull LaivyQuests plugin;

    private final @NotNull QuestsCommandApi commandApi;

    // Statistics
    private int created = 0; // Created data's
    // Statistics

    public QuestsApiProvider(@NotNull LaivyQuests plugin) {
        this(plugin, new QuestsCommandApiProvider());
    }
    protected QuestsApiProvider(@NotNull LaivyQuests plugin, @NotNull QuestsCommandApi commandApi) {
        this.plugin = plugin;
        this.commandApi = commandApi;
    }

    public @NotNull LaivyQuests getPlugin() {
        return plugin;
    }

    @Override
    public @NotNull Collection<ObjectiveType> getObjectiveTypes() {
        return objectiveTypes;
    }

    @Override
    public @NotNull ObjectiveType getObjectiveType(@NotNull String id) {
        Optional<ObjectiveType> optional = laivyQuests().getApi().getObjectiveTypes().stream().filter(t -> t.getId().equals(id)).findFirst();
        return optional.orElseThrow(() -> new NullPointerException("Couldn't find this objective type '" + id + "'"));
    }

    @Override
    public @NotNull Collection<RewardType<? extends Reward>> getRewardTypes() {
        return rewardTypes;
    }

    @Override
    public @NotNull RewardType<? extends Reward> getRewardType(@NotNull String id) {
        Optional<RewardType<? extends Reward>> optional = laivyQuests().getApi().getRewardTypes().stream().filter(t -> t.getId().equals(id)).findFirst();
        return optional.orElseThrow(() -> new NullPointerException("Couldn't find this reward type '" + id + "'"));
    }

    @Override
    public @NotNull Collection<BlockType<? extends IBlock>> getBlockTypes() {
        return blockTypes;
    }

    @Override
    public @NotNull BlockType<? extends IBlock> getBlockType(@NotNull String id) {
        Optional<BlockType<? extends IBlock>> optional = laivyQuests().getApi().getBlockTypes().stream().filter(t -> t.getId().equals(id)).findFirst();
        return optional.orElseThrow(() -> new NullPointerException("Couldn't find this block type '" + id + "'"));
    }

    @Override
    public @NotNull Collection<EntityType<? extends IEntity>> getEntityTypes() {
        return entityTypes;
    }

    @Override
    public @NotNull EntityType<? extends IEntity> getEntityType(@NotNull String id) {
        Optional<EntityType<? extends IEntity>> optional = laivyQuests().getApi().getEntityTypes().stream().filter(t -> t.getId().equals(id)).findFirst();
        return optional.orElseThrow(() -> new NullPointerException("Couldn't find this entity type '" + id + "'"));
    }

    @Override
    public @NotNull Collection<ItemType<? extends Item>> getItemTypes() {
        return itemTypes;
    }

    @Override
    public @NotNull ItemType<? extends Item> getItemType(@NotNull String id) {
        Optional<ItemType<? extends Item>> optional = laivyQuests().getApi().getItemTypes().stream().filter(t -> t.getId().equals(id)).findFirst();
        return optional.orElseThrow(() -> new NullPointerException("Couldn't find this item type '" + id + "'"));
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

    @Override
    public @NotNull QuestsCommandApi getCommandApi() {
        return commandApi;
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

        if (getCommandApi().hasCommand()) {
            getPlugin().getCommand("quests").setExecutor(getCommandApi());
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
                        data = getQuestsPlayerDataSerializer().deserialize(new JsonParser().parse(json));
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
                receptor.set(getVariable().getId(), getQuestsPlayerDataSerializer().serialize(data));
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

    protected @NotNull Serializer<QuestsPlayerData> getQuestsPlayerDataSerializer() {
        return new Serializer<QuestsPlayerData>() {
            @Override
            public @NotNull JsonElement serialize(@NotNull QuestsPlayerData data) {
                JsonObject object = new JsonObject();

                JsonArray quests = new JsonArray();
                for (Quest holder : data.getQuests()) {
                    quests.add(getQuestSerializer().serialize(holder));
                }

                object.addProperty("uuid", data.getUniqueId().toString());
                object.add("quests", quests);

                return object;
            }

            @Override
            public @NotNull QuestsPlayerData deserialize(@NotNull JsonElement data) {
                JsonObject object = data.getAsJsonObject();

                UUID uuid = UUID.fromString(object.get("uuid").getAsString());
                Set<Quest> quests = new LinkedHashSet<>();

                for (JsonElement questElement : object.getAsJsonArray("quests")) {
                    quests.add(getQuestSerializer().deserialize(questElement));
                }

                return new QuestsPlayerDataProvider(uuid, quests);
            }
        };
    }

    protected @NotNull Serializer<Quest> getQuestSerializer() {
        return new Serializer<Quest>() {
            @Override
            public @NotNull JsonElement serialize(@NotNull Quest holder) {
                JsonObject object = new JsonObject();
                JsonArray objectives = new JsonArray();

                object.addProperty("id", holder.getId());

                object.addProperty("name", holder.getName().getId());
                object.addProperty("description", holder.getDescription().getId());

                object.addProperty("uuid", holder.getUniqueId().toString());
                object.addProperty("start", holder.getStartDate().getTime());

                if (holder.getCompletedDate() != null) {
                    object.addProperty("completed", holder.getCompletedDate().getTime());
                }

                for (Objective objective : holder.getObjectives(false)) {
                    String objectiveTypeId = objective.getType().getId();

                    JsonObject objectiveObj = new JsonObject();
                    objectiveObj.addProperty("type id", objectiveTypeId);
                    objectiveObj.add("data", objective.getType().getSerializer().serialize(objective));

                    objectives.add(objectiveObj);
                }

                object.add("objectives", objectives);

                return object;
            }

            @Override
            public @NotNull Quest deserialize(@NotNull JsonElement holder) {
                JsonObject object = holder.getAsJsonObject();
                LinkedHashSet<Objective> objectives = new LinkedHashSet<>();

                String id = object.get("id").getAsString();
                UUID uuid = UUID.fromString(object.get("uuid").getAsString());

                IMessage name = laivyQuests().getMessageStorage().getMessage(object.get("name").getAsString());
                IMessage description = laivyQuests().getMessageStorage().getMessage(object.get("description").getAsString());

                Date start = new Date(object.get("start").getAsLong());
                @Nullable Date finish = (object.has("finish") ? new Date(object.get("finish").getAsLong()) : null);

                for (JsonElement objectiveElement : object.getAsJsonArray("objectives")) {
                    JsonObject objectiveObject = objectiveElement.getAsJsonObject();

                    String objectiveId = objectiveObject.get("type id").getAsString();
                    JsonElement data = objectiveObject.get("data");
                    ObjectiveType type = getObjectiveType(objectiveId);
                    Objective objective = type.getSerializer().deserialize(data);
                    objectives.add(objective);
                }

                return new QuestProvider(id, objectives, name, description, uuid, start, finish);
            }
        };
    }

    @EventHandler
    private void join(@NotNull PlayerJoinEvent e) {
        QuestsPlayerData data = getPlayerData(e.getPlayer().getUniqueId());
    }

    @EventHandler
    private void quest(@NotNull AsyncPlayerChatEvent e) {
        if (e.getMessage().equals("get")) {
            LinkedHashSet<Objective> objectives = new LinkedHashSet<>();

            objectives.add(new BlockBreakObjective(
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 7 name"),
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 7 name"),

                    new MaterialBlock(Objects.requireNonNull(XMaterial.WHEAT.parseMaterial())), 30, 0, new MoneyReward(200)
            ));
            objectives.add(new BlockPlaceObjective(
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 8 name"),
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 8 name"),

                    new MaterialBlock(Objects.requireNonNull(XMaterial.DRAGON_EGG.parseMaterial())), 1, 0, new MoneyReward(100000)
            ));

            objectives.add(new CategoryObjective(
                    laivyQuests().getMessageStorage().getMessage("Test (remove): Category name"),
                    laivyQuests().getMessageStorage().getMessage("Test (remove): Category name"),

                    new BlockBreakObjective(
                            laivyQuests().getMessageStorage().getMessage("Test (remove): 1 name"),
                            laivyQuests().getMessageStorage().getMessage("Test (remove): 1 name"),

                            new MaterialBlock(Material.COAL_ORE), 5, 0, null
                    ),
                    new BlockBreakObjective(
                            laivyQuests().getMessageStorage().getMessage("Test (remove): 2 name"),
                            laivyQuests().getMessageStorage().getMessage("Test (remove): 2 name"),

                            new MaterialBlock(Material.IRON_ORE), 5, 0, null
                    ),
                    new BlockBreakObjective(
                            laivyQuests().getMessageStorage().getMessage("Test (remove): 3 name"),
                            laivyQuests().getMessageStorage().getMessage("Test (remove): 3 name"),

                            new MaterialBlock(Material.DIAMOND_ORE), 5, 0, new MoneyReward(1500)
                    ),
                    new BlockBreakObjective(
                            laivyQuests().getMessageStorage().getMessage("Test (remove): 6 name"),
                            laivyQuests().getMessageStorage().getMessage("Test (remove): 6 name"),

                            new MaterialBlock(Material.DIAMOND_BLOCK), 1, 0, new MoneyReward(7000)
                    )
            ));

            objectives.add(new BlockBreakObjective(
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 4 name"),
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 4 name"),

                    new MaterialBlock(Objects.requireNonNull(XMaterial.GRASS_BLOCK.parseMaterial())), 5, 0, new MoneyReward(100)
            ));

            objectives.add(new BlockPlaceObjective(
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 5 name"),
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 5 name"),

                    new MaterialBlock(Objects.requireNonNull(XMaterial.DIRT.parseMaterial())), 5, 0, new MoneyReward(200)
            ));

            objectives.add(new BlockPlaceObjective(
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 5 name"),
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 5 name"),

                    new MaterialBlock(Objects.requireNonNull(XMaterial.DIRT.parseMaterial())), 5, 0, new MoneyReward(200)
            ));

            Quest quest = new QuestProvider(
                    "test-" + new Random().nextInt(10000),
                    objectives,
                    laivyQuests().getMessageStorage().getMessage("Test quest name"),
                    laivyQuests().getMessageStorage().getMessage("Test quest description"),
                    e.getPlayer().getUniqueId(),
                    new Date(),
                    null
            );

            laivyQuests().getApi().getPlayerData(e.getPlayer().getUniqueId()).getQuests().add(quest);
            Bukkit.broadcastMessage("Added quest");
        } else if (e.getMessage().equals("get2")) {
            LinkedHashSet<Objective> objectives = new LinkedHashSet<>();

            objectives.add(new BlockBreakObjective(
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 7 name"),
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 7 name"),

                    new MaterialBlock(Objects.requireNonNull(XMaterial.WHEAT.parseMaterial())), 30, 0, new MoneyReward(200)
            ));
            objectives.add(new BlockPlaceObjective(
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 8 name"),
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 8 name"),

                    new MaterialBlock(Objects.requireNonNull(XMaterial.DRAGON_EGG.parseMaterial())), 1, 0, new MoneyReward(100000)
            ));
            objectives.add(new BlockBreakObjective(
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 4 name"),
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 4 name"),

                    new MaterialBlock(Objects.requireNonNull(XMaterial.GRASS_BLOCK.parseMaterial())), 5, 0, new MoneyReward(100)
            ));

            objectives.add(new CategoryObjective(
                    laivyQuests().getMessageStorage().getMessage("Test (remove): Category name"),
                    laivyQuests().getMessageStorage().getMessage("Test (remove): Category name"),

                    new BlockBreakObjective(
                            laivyQuests().getMessageStorage().getMessage("Test (remove): 1 name"),
                            laivyQuests().getMessageStorage().getMessage("Test (remove): 1 name"),

                            new MaterialBlock(Material.COAL_ORE), 5, 0, null
                    ),
                    new BlockBreakObjective(
                            laivyQuests().getMessageStorage().getMessage("Test (remove): 2 name"),
                            laivyQuests().getMessageStorage().getMessage("Test (remove): 2 name"),

                            new MaterialBlock(Material.IRON_ORE), 5, 0, null
                    ),
                    new BlockBreakObjective(
                            laivyQuests().getMessageStorage().getMessage("Test (remove): 3 name"),
                            laivyQuests().getMessageStorage().getMessage("Test (remove): 3 name"),

                            new MaterialBlock(Material.DIAMOND_ORE), 5, 0, new MoneyReward(1500)
                    ),
                    new BlockBreakObjective(
                            laivyQuests().getMessageStorage().getMessage("Test (remove): 6 name"),
                            laivyQuests().getMessageStorage().getMessage("Test (remove): 6 name"),

                            new MaterialBlock(Material.DIAMOND_BLOCK), 1, 0, new MoneyReward(7000)
                    )
            ));

            objectives.add(new BlockPlaceObjective(
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 5 name"),
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 5 name"),

                    new MaterialBlock(Objects.requireNonNull(XMaterial.DIRT.parseMaterial())), 5, 0, new MoneyReward(200)
            ));

            objectives.add(new BlockPlaceObjective(
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 5 name"),
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 5 name"),

                    new MaterialBlock(Objects.requireNonNull(XMaterial.DIRT.parseMaterial())), 5, 0, new MoneyReward(200)
            ));

            Quest quest = new QuestProvider(
                    "test-" + new Random().nextInt(10000),
                    objectives,
                    laivyQuests().getMessageStorage().getMessage("Test quest name"),
                    laivyQuests().getMessageStorage().getMessage("Test quest description"),
                    e.getPlayer().getUniqueId(),
                    new Date(),
                    null
            );

            laivyQuests().getApi().getPlayerData(e.getPlayer().getUniqueId()).getQuests().add(quest);
            Bukkit.broadcastMessage("Added quest");
        } else if (e.getMessage().equals("get3")) {
            LinkedHashSet<Objective> objectives = new LinkedHashSet<>();

            objectives.add(new BlockBreakObjective(
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 7 name"),
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 7 name"),

                    new MaterialBlock(Objects.requireNonNull(XMaterial.WHEAT.parseMaterial())), 30, 0, new MoneyReward(200)
            ));
            objectives.add(new BlockPlaceObjective(
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 8 name"),
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 8 name"),

                    new MaterialBlock(Objects.requireNonNull(XMaterial.DRAGON_EGG.parseMaterial())), 1, 0, new MoneyReward(100000)
            ));
            objectives.add(new EntityKillObjective(
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 9 name"),
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 9 name"),

                    new ObjectiveEntity(org.bukkit.entity.EntityType.CHICKEN), 30, 0, new MoneyReward(500)
            ));
            objectives.add(new ConsumeItemObjective(
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 10 name"),
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 10 name"),

                    new ItemProvider(new ItemStack(Material.COOKED_BEEF)), 5, 0, new MoneyReward(1500)
            ));
            objectives.add(new CraftItemObjective(
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 11 name"),
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 11 name"),

                    new ItemProvider(new ItemStack(Objects.requireNonNull(XMaterial.CRAFTING_TABLE.parseMaterial()))), 1, 0, new MoneyReward(20)
            ));
            objectives.add(new FishingObjective(
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 12 name"),
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 12 name"),

                    new ItemProvider(new ItemStack(XMaterial.COD.parseMaterial())), 1, 0, new MoneyReward(500)
            ));

            Quest quest = new QuestProvider(
                    "test-" + new Random().nextInt(10000),
                    objectives,
                    laivyQuests().getMessageStorage().getMessage("Test quest name"),
                    laivyQuests().getMessageStorage().getMessage("Test quest description"),
                    e.getPlayer().getUniqueId(),
                    new Date(),
                    null
            );

            laivyQuests().getApi().getPlayerData(e.getPlayer().getUniqueId()).getQuests().add(quest);
            Bukkit.broadcastMessage("Added quest");
        } else if (e.getMessage().equals("get4")) {
            LinkedHashSet<Objective> objectives = new LinkedHashSet<>();

            objectives.add(new LaivyNpcInteractObjective(
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 13 name"),
                    laivyQuests().getMessageStorage().getMessage("Test (remove): 13 name"),

                    new LaivyNpcProvider(NPC.NPCS_ID.get(0), laivyQuests().getMessageStorage().getMessage("Test (remove): ItsLaivy")), new MoneyReward(8000)
            ));

            Quest quest = new QuestProvider(
                    "test-" + new Random().nextInt(10000),
                    objectives,
                    laivyQuests().getMessageStorage().getMessage("Test quest name"),
                    laivyQuests().getMessageStorage().getMessage("Test quest description"),
                    e.getPlayer().getUniqueId(),
                    new Date(),
                    null
            );

            laivyQuests().getApi().getPlayerData(e.getPlayer().getUniqueId()).getQuests().add(quest);
            Bukkit.broadcastMessage("Added quest");
        }
    }
}
