package codes.laivy.quests.events;

import codes.laivy.quests.quests.QuestHolder;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public class QuestCompleteEvent extends QuestEvent implements Cancellable {

    private final @NotNull QuestHolder holder;
    private boolean cancel = false;

    public QuestCompleteEvent(@NotNull QuestHolder holder) {
        super(holder.getQuest());
        this.holder = holder;
    }

    public final @NotNull QuestHolder getHolder() {
        return holder;
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
