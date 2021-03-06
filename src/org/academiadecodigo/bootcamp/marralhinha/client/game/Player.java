package org.academiadecodigo.bootcamp.marralhinha.client.game;

import org.academiadecodigo.bootcamp.marralhinha.client.game.spot.Spot;
import org.academiadecodigo.bootcamp.marralhinha.client.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private Color color;
    private GameState game;
    private List<Spot> queue;
    private List<Spot> finalPath;
    private List<Spot> cursors;

    public Player(GameState game, Color color) {
        this.game = game;
        this.color = color;
    }

    public void move(Spot cursor) {
        if (!game.isMyTurn() || !game.isActivePlayer(this)) {
            return;
        }

        int moves = game.getDiceValue();
        Spot aux = cursor.getNextSpot(this, moves);

        if (aux == null) {
            return;
        }

        game.sendMoveMessage(cursor.getCol(), cursor.getRow(), moves);
        game.setMyTurn(false);
    }

    public void move(int col, int row, int times) {
        Spot old = getCursorAt(col, row);
        Spot dest = old.getNextSpot(this, times);
        swapCursors(old, dest);
    }

    private void swapCursors(Spot old, Spot dest) {
        cursors.remove(old);
        cursors.add(dest);
        old.changeResident(null);
        dest.changeResident(this);
    }

    private Spot getCursorAt(int col, int row) {
        for (Spot cursor : cursors) {
            if (cursor.isAt(col, row)) {
                return cursor;
            }
        }

        return null;
    }

    public void reset(Spot spot) {
        for (Spot place : queue) {

            if (!place.isOccupied()) {
                place.changeResident(this);
                swapCursors(spot, place);
                break;
            }
        }
    }

    public boolean hasValidMoves() {
        for (Spot spot : cursors) {
            if (spot.getNextSpot(this, game.getDiceValue()) != null) {
                return true;
            }
        }

        return false;
    }

    public boolean hasWon() {
        return finalPath.containsAll(cursors);
    }

    public Color getColor() {
        return color;
    }

    public void setQueue(List<Spot> queue) {
        this.queue = queue;
        cursors = new ArrayList<>(queue.size());
        cursors.addAll(queue);
    }

    public void setFinalPath(List<Spot> finalPath) {
        this.finalPath = finalPath;
    }
}
