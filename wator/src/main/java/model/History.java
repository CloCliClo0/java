package model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public final class History {

    private static final int MAX_TURNS = 300;

    private final Deque<Census> censuses = new ArrayDeque<>();

    void add(Census census) {
        censuses.addLast(census);
        if (censuses.size() > MAX_TURNS) {
            censuses.removeFirst();
        }
    }

    public List<Census> getCensuses() {
        return List.copyOf(censuses);
    }

    public Census latest() {
        return censuses.getLast();
    }
}
