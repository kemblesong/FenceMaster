/**
 * This is board.
 * Class for generating a Board with n dimension.
 * Has methods for:
 * - filling in a board state from input
 * - checking if given board state is winning
 * @author kemble
 */

import java.util.PriorityQueue;

public class Board {

    /* A board has rows. Each row has a variable length based on
       hexagon pattern.
     */

    Hex[][] rows;
    int dimension;
    int numHexes;

    // Just to test if board was successfully made
    public static void main(String[] args) {
        int n = 4;
        Board thisBoard = new Board(n);
        for (int i=0; i<thisBoard.rows.length; i++) {
            for (int j=0; j<thisBoard.rows[i].length; j++) {
                System.out.printf("%c ", thisBoard.rows[i][j].getColour());
            }
            System.out.println();
        }

        thisBoard.rows[0][0].setColour('B');
        System.out.println(thisBoard.rows[1][0].getAdjacent()[1].getColour());
    }

    // Constructor for making a Board with n rows
    // Max length of any row is 2*n-1
    public Board(int n) {
        dimension = n;
        rows = new Hex[2*n-1][];
        numHexes = 3*n*n-3*n+1;
        int i, j;
        for (i=0; i<n; i++) {
            rows[i] = new Hex[n+i];
            rows[2*n-(i+2)] = new Hex[n+i];
            for (j=0; j < this.rows[i].length; j++) {
                rows[i][j] = new Hex(i, j, '-', this);
                rows[2*n-(i+2)][j] = new Hex(i, j, '-', this);
            }
        }
    }

    // Method for filling in a boardstate into an empty board
    public void fillBoard(String file) {

    }

    // Method for checking if there is a winner
    public void testWin(Board board) {
        findLoop(board, 'b');
        findLoop(board, 'w');
        findTripod(board, 'b');
        findTripod(board, 'w');
    }

    /**
     * Method for finding loops of a colour, prints out name of colour and "loop" if found
     */

    public void findLoop(Board board, char colour) {
        // For each hex that is empty or opposite colour AND non-edge, add to queue.
        PriorityQueue<Hex> queue = new PriorityQueue<Hex>(board.numHexes);
        int i, j;
        for (i=1; i<board.rows.length-1; i++) {
            for (j=1; j<board.rows[i].length-1; j++) {
                if (board.rows[i][j].getColour() != colour) {
                    queue.add(board.rows[i][j]);
                }
            }
        }
        Hex currentHex;
        Hex[] neighbours;
        // Loop as long as queue is not empty
        while (queue.size() != 0) {
            currentHex = queue.poll();
            neighbours = currentHex.getAdjacent();
            if (explore(currentHex, neighbours, queue)) {
                System.out.printf("%c\nLoop", colour);
                break;
            }
        }
    }

    /**
     * Method for expanding nodes recursively until it either hits an edge returning false,
     * or finds itself surrounded by nodes not in the queue returning true.
     * Breadth first search.
     */
    public boolean explore(Hex origin, Hex[] neighbours, PriorityQueue<Hex> queue) {
        int i;
        Hex currentHex;
        Hex previousHex;
        // Check each of current hexes neighbours
        for (i=0; i<neighbours.length; i++) {
            previousHex = origin;
            currentHex = neighbours[i];
            if (queue.contains(currentHex)) {
                // This means the hex is empty/opposite colour and non-edge, so expand it (recursive)
                if (explore(currentHex, currentHex.getAdjacent(), queue)){
                    queue.remove(currentHex);
                    return true;
                }
            } else if (currentHex == null) {
                // This means we are at an edge
                queue.remove(previousHex);
                return false;
            }
        }
        // This means that there's nothing more to explore and not at an edge
        return true;
    }

    /**
     * Method for finding tripods of a colour
     *
     */

    public void findTripod(Board board, char colour) {
        // For each hex that has three or more connecting hexes with same colour, add to queue.
        PriorityQueue<Hex> queue = new PriorityQueue<Hex>(board.numHexes);
        int i, j, n, numAdj;
        Hex currentHex;
        Hex[] neighbours;
        for (i=0; i<board.rows.length; i++) {
            for (j=0; j<board.rows[i].length; j++) {
                if (board.rows[i][j].getColour() == colour) {
                    numAdj = 0;
                    currentHex = board.rows[i][j];
                    neighbours = currentHex.getAdjacent();
                    for (n=0; n<neighbours.length; n++) {
                        if (neighbours[n].getColour() == colour) {
                            numAdj++;
                        }
                    }
                    if (numAdj >= 3) {
                        queue.add(currentHex);
                    }
                }
            }
        }
        // For each hex in the queue, we trace connecting hexes until we hit an edge or no more connectors.
        while (queue.size() != 0) {
            currentHex = queue.poll();
            neighbours = currentHex.getAdjacent();
            if (trace(currentHex, neighbours, queue, colour, board, 0) >= 3) {
                System.out.printf("%c\nTripod", colour);
                break;
            }
        }
    }

    /**
     * Method for tracing linked hexes of the same colour until an edge is hit or the link is broken.
     * Depth first search.
     */
    public int trace(Hex origin, Hex[] neighbours, PriorityQueue<Hex> queue, char colour, Board board, int count) {
        int i;
        Hex currentHex;
        for (i=0; i<neighbours.length; i++) {
            currentHex = neighbours[i];
            if (currentHex != null) {
                if (currentHex.getColour() == colour) {
                    // Continue tracing along same coloured adjacent hexes.
                    count = trace(currentHex, currentHex.getAdjacent(), queue, colour, board, count);
                }
                // Not the same colour, move along.
            } else {
                // We have hit an edge!
                if (!isCorner(origin, board.dimension)) {
                    // Better yet, it's a non-corner edge!
                    count++;
                    return count;
                }
            }
        }
        //queue.remove(origin);
        return 0;
    }

    /**
     * Method for testing if a hex is a corner.
     */
    public boolean isCorner(Hex hex, int boardDimension) {
        return (hex.x == 0 && hex.y == 0) ||
                (hex.x == boardDimension - 1 && hex.y == 0) ||
                (hex.x == 0 && hex.y == boardDimension - 1) ||
                (hex.x == 2 * boardDimension - 2 && hex.y == boardDimension - 1) ||
                (hex.x == 0 && hex.y == 2 * boardDimension - 2) ||
                (hex.x == boardDimension - 1 && hex.y == 2 * boardDimension - 2);
    }
}
