package codes.laivy.quests.events.quest;

import codes.laivy.quests.quests.Quest;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class QuestEvent extends Event {

    private static final @NotNull HandlerList handlers = new HandlerList();

    private final @NotNull Quest quest;

    public QuestEvent(@NotNull Quest quest) {
        this.quest = quest;
    }

    public final @NotNull Quest getQuest() {
        return quest;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
