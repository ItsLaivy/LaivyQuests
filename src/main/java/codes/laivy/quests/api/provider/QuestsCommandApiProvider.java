package codes.laivy.quests.api.provider;

import codes.laivy.quests.api.QuestsCommandApi;
import codes.laivy.quests.inventory.QuestMainInventory;
import codes.laivy.quests.utils.ComponentUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static codes.laivy.quests.LaivyQuests.laivyQuests;

public class QuestsCommandApiProvider implements QuestsCommandApi {
    @Override
    public boolean hasCommand() {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            QuestMainInventory inventory = new QuestMainInventory(player);
            player.openInventory(inventory.getViewInventory());
        } else {
            sender.sendMessage(ComponentUtils.getText(laivyQuests().getMessageStorage().get("en_us", "Commands: Can only be performed by players")));
        }

        return true;
    }
}
