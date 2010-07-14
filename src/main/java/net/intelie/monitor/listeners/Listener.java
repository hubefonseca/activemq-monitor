package net.intelie.monitor.listeners;

import net.intelie.monitor.events.Event;

public interface Listener {

    public void notify(Event event);

}
