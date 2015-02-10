package com.programyourhome.huebridgesimulator.menu;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.programyourhome.huebridgesimulator.model.menu.Menu;
import com.programyourhome.huebridgesimulator.model.menu.MenuItem;
import com.programyourhome.huebridgesimulator.model.menu.SimColor;

//@Component
//TODO: ConditionalOnProperty
public class TestMenu implements Menu {

    private class Node extends MenuItem {
        private Node nextNode;
        private Node previousNode;

        public Node(final String name, final Color color) {
            super(name, new SimColor(color), false);
        }

    }

    private int currentIndex;
    private final List<Node> nodes;

    public TestMenu() {
        this.currentIndex = 0;
        this.nodes = new ArrayList<>();
        this.nodes.add(new Node("Node 1", Color.RED));
        this.nodes.add(new Node("Node 2", Color.RED));
        this.nodes.add(new Node("Node 3", Color.RED));
        this.nodes.add(new Node("Node 4", Color.RED));
    }

    @Override
    public MenuItem[] getCurrentMenu() {
        final List<MenuItem> menu = new ArrayList<>();
        menu.add(this.nodes.get(this.currentIndex));
        menu.add(new Node("Back", Color.WHITE));
        return menu.toArray(new MenuItem[0]);
    }

    @Override
    public void menuItemSelected(final MenuItem menuItem) {
        if (menuItem.getName().equals("Back") && this.currentIndex > 0) {
            this.currentIndex--;
        } else if (this.currentIndex < this.nodes.size() - 1) {
            this.currentIndex++;
        }
    }

}
