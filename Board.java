/**
 * This is board.
 * Class for generating a Board with n dimension.
 * Has methods for:
 * - filling in a board state from input
 * - checking if given board state is winning
 * @author kemble
 */

import java.util.InputMismatchException;
import java.util.Scanner;
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
        Board thisBoard = fillBoard();
        for (int i=0; i<thisBoard.rows.length; i++) {
            for (int j=0; j<thisBoard.rows[i].length; j++) {
                System.out.printf("%c ", thisBoard.rows[i][j].getColour());
            }
            System.out.println();
        }
        thisBoard.testWin(thisBoard);
    }

    /**
     * Constructor for making a Board with n rows
     * Max length of any row is 2*n-1
     * Max number of hexes is 3*n*n-3*n+1
     */

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

    /**
     * Method for filling in a board state into an empty board
     */

    public static Board fillBoard() {
        Scanner input = new Scanner(System.in);
        Board newBoard = null;
        String token;
        int i, j;
        try {
            newBoard = new Board(input.nextInt());
        }
        catch (InputMismatchException e) {
            System.err.println("Error: Improperly formatted board in input - program halted.");
            System.exit(1);
        }
        for (i=0; i<newBoard.rows.length; i++) {
            for (j=0; j<newBoard.rows[i].length; j++) {
                token = input.next();
                if (!(token.equals("-") || token.equals("B") || token.equals("W"))) {
                    System.err.println("Error: Improperly formatted board in input - program halted.");
                    System.exit(1);
                }
                newBoard.rows[i][j] = new Hex(j, i, token.charAt(0), newBoard);
            }
        }
        input.close();
        return newBoard;
    }

    /**
     * Method for checking if there is a winner
     * Uses a switch statement to switch between the ten different possible board states based on winning positions.
     *
     */
    public void testWin(Board board) {

        boolean draw = true;
        int i, j;
        for (i=0; i<board.rows.length; i++) {
            for (j=0; j<board.rows[i].length; j++) {
                if (board.rows[i][j].colour == '-') {
                    draw = false;
                }
            }
        }

        if (draw) {
            System.out.print("Draw\nNil");
        }

        int state=0;

        if (findLoop(board, 'B')) {
            state += 1;
        }
        if (findLoop(board, 'W')) {
            state += 2;
        }
        if (findTripod(board, 'B')) {
            state += 4;
        }
        if (findTripod(board, 'W')) {
            state += 7;
        }

        switch(state) {
            case 0: System.out.print("None\nNil");
                break;
            case 1: System.out.print("Black\nLoop");
                break;
            case 2: System.out.print("White\nLoop");
                break;
            case 3: System.out.print("Invalid board state: multiple winners");
                break;
            case 4: System.out.print("Black\nTripod");
                break;
            case 5: System.out.print("Black\nBoth");
                break;
            case 6: System.out.print("Invalid board state: multiple winners");
                break;
            case 7: System.out.print("White\nTripod");
                break;
            case 8: System.out.print("Invalid board state: multiple winners");
                break;
            case 9: System.out.print("White\nBoth");
        }

    }

    /**
     * Method for finding loops of a colour
     */

    public boolean findLoop(Board board, char colour) {
        // For each hex that is empty or opposite colour AND non-edge, add to queue.
        PriorityQueue<Hex> queue = new PriorityQueue<Hex>(board.numHexes);
        int i, j;
        for (i=0; i<board.rows.length; i++) {
            for (j=0; j<board.rows[i].length; j++) {
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
            if (explore(neighbours, queue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method for expanding nodes recursively until it either hits an edge returning false,
     * or finds itself surrounded by nodes not in the queue returning true.
     * Depth first search.
     */
    public boolean explore(Hex[] neighbours, PriorityQueue<Hex> queue) {

        int i;
        Hex currentHex;

        // Check each of current hexes neighbours
        for (i=0; i<neighbours.length; i++) {
            currentHex = neighbours[i];

            // If we are at an edge
            if (currentHex == null) {
                return false;
            }
            // This means the hex is empty/opposite colour and non-edge, so expand it (recursive)
            else if (queue.contains(currentHex)) {
                queue.remove(currentHex);
                if (!explore(currentHex.getAdjacent(), queue)) {
                    return false;
                }
            }
            // Otherwise, right coloured hex or already visited. This is good.

        }
        // This means that there's nothing more to explore and not at an edge; We're inside a loop!
        return true;
    }

    /**
     * Method for finding tripods of a colour
     *
     */

    public boolean findTripod(Board board, char colour) {
        // For each hex that has three or more connecting hexes with same colour, add to queue.
        PriorityQueue<Hex> queue = new PriorityQueue<Hex>(board.numHexes);
        PriorityQueue<Hex> visited = new PriorityQueue<Hex>(board.numHexes);
        int i, j, n, numAdj;
        Hex currentHex;
        Hex[] neighbours;

        for (i=0; i<board.rows.length; i++) {
            for (j=0; j<board.rows[i].length; j++) {

                // Do the following if on colour
                if (board.rows[i][j].getColour() == colour) {
                    numAdj = 0;
                    currentHex = board.rows[i][j];
                    neighbours = currentHex.getAdjacent();

                    // Look at each of its neighbours to see if they are also on colour
                    for (n=0; n<neighbours.length; n++) {
                        // null check first
                        if (neighbours[n] != null) {
                            if (neighbours[n].getColour() == colour) {
                                numAdj++;
                            }
                        }
                    }

                    // If three or more adjacent on colour hexes, add to queue
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
            if (trace(currentHex, neighbours, colour, board, visited) >= 3) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method for tracing linked hexes of the same colour until an edge is hit or the link is broken.
     * Depth first search.
     */
    public int trace(Hex origin, Hex[] neighbours, char colour, Board board, PriorityQueue<Hex> visited) {
        int i;
        int count = 0;
        Hex currentHex;

        for (i=0; i<neighbours.length; i++) {
            currentHex = neighbours[i];

            // Do the following if not at edge
            if (currentHex != null) {
                // Do the following if hex hasn't been visited
                if (!visited.contains(currentHex)) {
                    visited.add(currentHex);
                    if (currentHex.getColour() == colour) {
                        // Continue tracing along same coloured adjacent hexes.
                        count += trace(currentHex, currentHex.getAdjacent(), colour, board, visited);
                    }
                }
                // Not the same colour, move along.
            }
            else {
                // We have hit an edge!
                if (!isCorner(origin, board.dimension)) {
                    // Better yet, it's a non-corner edge!
                    count++;
                    return count;
                }
            }
        }
        return count;
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
