package codes.laivy.quests.api.provider;

import codes.laivy.quests.api.QuestsCommandApi;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class QuestsCommandApiProvider implements QuestsCommandApi {
    @Override
    public boolean hasCommand() {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Bukkit.broadcastMessage("Command performed.");
        return true;
    }
}
