package com.programyourhome.huebridgesimulator.model.menu;

import java.util.Arrays;

/**
 * A menu that provides the available menu items and can act on menu items that are 'clicked' on or off.
 */
public interface Menu {

    /**
     * Get the array of current menu items. As the name of this method implies, this list can vary over time.
     * The names of the returned menu items should be unique in the context of that specific array.
     *
     * @return the array of current menu items
     */
    public MenuItem[] getCurrentMenu();

    /**
     * Notify the menu of the event that a menu item was 'clicked' and whether that item is supposed
     * to be in state on (true) or off (false).
     *
     * @param menuItemName the name of the menu item.
     * @param on on (true) or off (false)
     */
    public void menuItemClicked(String menuItemName, boolean on);

    /**
     * Default implementation of looking up a menu item by name in the array of current menu items.
     * This implementation assumes the searched item actually exists and will throw an exception otherwise.
     *
     * @param name the name of the menu item
     * @return the menu item
     */
    default MenuItem getMenuItemByName(final String name) {
        return Arrays.asList(getCurrentMenu()).stream()
                .filter(menuItem -> menuItem.getName().equals(name))
                .findFirst()
                .get();
    }

}
