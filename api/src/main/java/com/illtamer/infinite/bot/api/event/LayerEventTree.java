package com.illtamer.infinite.bot.api.event;

import com.illtamer.infinite.bot.api.annotation.Coordinates;
import com.illtamer.infinite.bot.api.util.Assert;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 一维层级 B 树 - construct for {@link Event}
 * */
class LayerEventTree<E> {

    private final Class<? extends E> root;

    private final Map<String, Node> nodes = new HashMap<>();

    /**
     * @param root Root class for Tree
     * */
    public LayerEventTree(Class<? extends E> root) {
        this.root = root;
    }

    /**
     * @param coordinates The coordinates for sub clazz
     * @param clazz Sub class
     * */
    public void add(Coordinates coordinates, Class<? extends E> clazz) {
        String postValue = coordinates.postType().getValue();
        String[] secType = coordinates.secType();
        String[] subType = coordinates.subType();
        if (isNull(secType)) {
            throw new InvalidParameterException("If you want to create a parent coordinate, use \"*\" on #secType");
        }
        if (isNull(subType)) {
            if ("*".equals(secType[0]))
                addFullMatchNode(postValue, clazz);
            else
                for (String sec : secType)
                    addFullMatchNode(postValue + '.' + sec, clazz);
            return;
        }
        for (String sec : secType) {
            addCommonNode(postValue + '.' + sec, subType, clazz);
        }
    }

    /**
     * Get the node clazz recursively
     * */
    public Class<? extends E> get(String index) {
        Node node = nodes.get(index);
        if (node == null) {
            int lastIndexOf = index.lastIndexOf('.');
            if (lastIndexOf != -1)
                return get(index.substring(0, lastIndexOf));
            else
                return root;
        }
        return node.clazz;
    }

    private void addCommonNode(String parentIndex, String[] subType, Class<? extends E> clazz) {
        for (String sub : subType) {
            Node node = nodes.put(parentIndex + '.' + sub, new Node(parentIndex, clazz));
            Assert.isNull(node, "Duplicate index value in(%s)%s", parentIndex, Arrays.toString(subType));
        }
    }

    private void addFullMatchNode(String index, Class<? extends E> clazz) {
        Node node = nodes.put(index, new Node(null, clazz));
        Assert.isNull(node, "Duplicate index value in(%s)", index);
    }

    private class Node {

        private final String parentIndex;

        private final Class<? extends E> clazz;

        private Node(String parentIndex, Class<? extends E> clazz) {
            this.parentIndex = parentIndex;
            this.clazz = clazz;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "parentIndex='" + parentIndex + '\'' +
                    ", clazz=" + clazz +
                    '}';
        }

    }

    /**
     * Only for annotation parameter
     * */
    private static boolean isNull(String[] s) {
        return s.length == 1 && s[0].length() == 0;
    }

    @Override
    public String toString() {
        return "LayerEventTree{" +
                "root=" + root +
                ", nodes=" + nodes +
                '}';
    }

}
