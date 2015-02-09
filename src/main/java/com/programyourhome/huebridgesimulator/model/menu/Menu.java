package com.programyourhome.huebridgesimulator.model.menu;

import java.util.List;

public interface Menu {

    public List<MenuItem> getCurrentMenu();

    public void menuItemSelected(MenuItem menuItem);

}
