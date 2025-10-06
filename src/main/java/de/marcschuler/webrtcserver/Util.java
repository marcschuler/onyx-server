package de.marcschuler.webrtcserver;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class Util {

    public static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Moves an element to a new order
     *
     * @param list         the list
     * @param currentOrder the current index
     * @param newOrder     the wanted index
     * @param <T>          any element
     */
    public static <T> void reorder(List<T> list, int currentOrder, int newOrder) {
        if (currentOrder < 0 || currentOrder >= list.size() || newOrder < 0 || newOrder >= list.size())
            throw new IndexOutOfBoundsException("Invalid indices [" + currentOrder + ", " + newOrder + "], must be in range [0," + (list.size() - 1) + "]");
        if (currentOrder == newOrder)
            return;
        var obj = list.remove(currentOrder);
        list.add(newOrder, obj);
    }
}
