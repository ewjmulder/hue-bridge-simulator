package com.programyourhome.huebridgesimulator.model.menu;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class TestMenu implements Menu {

    private class Node implements MenuItem {
        private final String name;
        private final Color color;
        private Node nextNode;
        private Node previousNode;

        public Node(final String name, final Color color) {
            this.name = name;
            this.color = color;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public Color getColor() {
            return this.color;
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
    public List<MenuItem> getCurrentMenu() {
        final List<MenuItem> menu = new ArrayList<>();
        menu.add(this.nodes.get(this.currentIndex));
        menu.add(new Node("Back", Color.WHITE));
        return menu;
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
