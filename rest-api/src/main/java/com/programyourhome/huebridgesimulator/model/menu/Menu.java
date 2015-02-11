package com.programyourhome.huebridgesimulator.model.menu;

import java.util.Arrays;

public interface Menu {

    public MenuItem[] getCurrentMenu();

    public void menuItemClicked(String menuItemName, boolean on);

    default MenuItem getMenuItemByName(final String name) {
        return Arrays.asList(getCurrentMenu()).stream()
                .filter(menuItem -> menuItem.getName().equals(name))
                .findFirst()
                .get();
    }

}
