package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Sea {

    private static final int MIN_SIZE = 3;

    private final int rows;
    private final int columns;
    private final Animal[][] cells;

    public Sea(int rows, int columns) {
        if (rows < MIN_SIZE || columns < MIN_SIZE) {
            throw new IllegalArgumentException("the sea needs at least " + MIN_SIZE + " rows and columns");
        }
        this.rows = rows;
        this.columns = columns;
        this.cells = new Animal[rows][columns];
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getCellCount() {
        return rows * columns;
    }

    public boolean isEmpty(Position position) {
        return animalAt(position) == null;
    }

    /** Returns the animal in this cell, or null if the cell is empty. */
    public Animal animalAt(Position position) {
        requireInside(position);
        return cells[position.row()][position.column()];
    }

    void place(Animal animal, Position position) {
        Objects.requireNonNull(animal, "animal");
        if (!isEmpty(position)) {
            throw new IllegalStateException("cell " + position + " is already occupied");
        }
        cells[position.row()][position.column()] = animal;
    }

    void move(Position from, Position to) {
        if (isEmpty(from)) {
            throw new IllegalStateException("cell " + from + " is empty");
        }
        place(animalAt(from), to);
        cells[from.row()][from.column()] = null;
    }

    Animal remove(Position position) {
        Animal animal = animalAt(position);
        if (animal == null) {
            throw new IllegalStateException("cell " + position + " is empty");
        }
        cells[position.row()][position.column()] = null;
        return animal;
    }

    List<Position> neighbors(Position position) {
        requireInside(position);
        return List.of(
                new Position(Math.floorMod(position.row() - 1, rows), position.column()),
                new Position(Math.floorMod(position.row() + 1, rows), position.column()),
                new Position(position.row(), Math.floorMod(position.column() - 1, columns)),
                new Position(position.row(), Math.floorMod(position.column() + 1, columns)));
    }

    List<Position> emptyNeighbors(Position position) {
        return neighbors(position).stream().filter(this::isEmpty).toList();
    }

    List<Position> occupiedNeighbors(Position position) {
        return neighbors(position).stream().filter(p -> !isEmpty(p)).toList();
    }

    List<Position> allPositions() {
        List<Position> positions = new ArrayList<>(getCellCount());
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                positions.add(new Position(row, column));
            }
        }
        return positions;
    }

    List<Placement> placements() {
        List<Placement> placements = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (cells[row][column] != null) {
                    placements.add(new Placement(cells[row][column], new Position(row, column)));
                }
            }
        }
        return placements;
    }

    private void requireInside(Position position) {
        Objects.requireNonNull(position, "position");
        if (position.row() < 0 || position.row() >= rows || position.column() < 0 || position.column() >= columns) {
            throw new IllegalArgumentException("position " + position + " is outside the sea");
        }
    }

    record Placement(Animal animal, Position position) {
    }
}
