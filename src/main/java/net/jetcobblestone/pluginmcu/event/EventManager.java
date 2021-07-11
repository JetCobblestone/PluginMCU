package net.jetcobblestone.pluginmcu.event;

import net.jetcobblestone.pluginmcu.team.TeamManager;

public class EventManager {

    private MCUEvent event;
    private final TeamManager teamManager;

    public EventManager(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    public boolean eventActive() {
        return event != null;
    }

    public void startEvent() {
        event = new MCUEvent(teamManager);
    }
}
