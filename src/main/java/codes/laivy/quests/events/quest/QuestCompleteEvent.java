package codes.laivy.quests.events.quest;

import codes.laivy.quests.quests.Quest;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public class QuestCompleteEvent extends QuestEvent implements Cancellable {

    private boolean cancel = false;

    public QuestCompleteEvent(@NotNull Quest quest) {
        super(quest);
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
