package com.programyourhome.huebridgesimulator.menu;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.programyourhome.huebridgesimulator.model.menu.Menu;
import com.programyourhome.huebridgesimulator.model.menu.MenuItem;

/**
 * A simple test menu to see if the hue bridge simulator 'lights' are correctly picked up by a voice based device that you want to 'fool'.
 */
@Component
@ConditionalOnProperty("backend.mode.test.voice")
public class VoiceTestMenu implements Menu {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final List<MenuItem> menuItems;

    public VoiceTestMenu() {
        this.menuItems = new ArrayList<>();
        this.menuItems.add(new MenuItem("Tryout"));
        this.menuItems.add(new MenuItem("Something specific"));
        this.menuItems.add(new MenuItem("Very long name with several words"));
        this.menuItems.add(new MenuItem("Television"));
    }

    @Override
    public MenuItem[] getCurrentMenu() {
        return this.menuItems.toArray(new MenuItem[0]);
    }

    @Override
    public void menuItemClicked(final String menuItemName, final boolean on) {
        this.log.info("Menu item 'clicked': " + menuItemName);
        this.getMenuItemByName(menuItemName).setOn(on);
    }

}
