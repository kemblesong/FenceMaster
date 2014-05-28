/*By Kemble Song (584999) & Nicholas Poulton (585075)*/

/**
 * This is board.
 * Class for generating a Board with n dimension.
 * Has methods for:
 * - filling in a board state from input
 * - checking if given board state is winning
 * @author kemble
 */

package fencemaster;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.PriorityQueue;

public class Board implements Piece, Cloneable{

    /* A board has rows. Each row has a variable length based on
       hexagon pattern. Thus, the hexes are contained in a two-dimensional
       array, and the arrays in the first array have variable length.
     */

    Hex[][] rows;
    int dimension;
    int numHexes;

    public static void main(String[] args) {
        Board thisBoard = fillBoard();
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
        /* Initialize all the arrays, then go through and initialize all the
           entries to be blank. */
        for (i=0; i<n; i++) {
            rows[i] = new Hex[n+i];
            rows[2*n-(i+2)] = new Hex[n+i];
            for (j=0; j < this.rows[i].length; j++) {
                rows[i][j] = new Hex(i, j, EMPTY, this);
                rows[2*n-(i+2)][j] = new Hex(i, j, EMPTY, this);
            }
        }
    }

    /**
     * Method for creating a new board from stdin. Input must contain one
     * integer for the dimension of the board, then a series of characters
     * for the tile colours, all separated by whitespace.
     */

    public static Board fillBoard() {
        Scanner input = new Scanner(System.in);
        Board newBoard = null;
        String token;
        int i, j;
        try {
            newBoard = new Board(input.nextInt());
        }
        // If board size is not specified, exit.
        catch (InputMismatchException e) {
            System.err.println("Error: Improperly formatted board in input - program halted.");
            System.exit(1);
        }
        for (i=0; i<newBoard.rows.length; i++) {
            for (j=0; j<newBoard.rows[i].length; j++) {
                token = input.next();
                // Check if input if input is as expected.
                if (!(token.equals("-") || token.equals("B") || token.equals("W"))) {
                    System.err.println("Error: Improperly formatted board in input - program halted.");
                    System.exit(1);
                }
                // Insert new hex into board. Remember that the y-coordinate goes first.
                newBoard.rows[i][j] = new Hex(j, i, token.charAt(0), newBoard);
            }
        }
        input.close();
        return newBoard;
    }

    /**
     * Method for checking if there is a winner.
     * Prints the information to stdout.
     */
    public void testWin(Board board) {

        boolean draw = true;
        int i, j;
        for (i=0; i<board.rows.length; i++) {
            for (j=0; j<board.rows[i].length; j++) {
                if (board.rows[i][j].colour == EMPTY) {
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

    boolean findLoop(Board board, int colour) {
        // For each hex that is empty or opposite colour AND non-edge, add to queue.
        PriorityQueue<Hex> queue = new PriorityQueue<Hex>(board.numHexes);
        PriorityQueue<Hex> visited = new PriorityQueue<Hex>(board.numHexes);
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
            visited.clear();
            currentHex = queue.poll();
            neighbours = currentHex.getAdjacent();
            if (explore(neighbours, queue, visited, colour)) {
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
    private boolean explore(Hex[] neighbours, PriorityQueue<Hex> queue, 
                                              PriorityQueue<Hex> visited, int colour) {

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
            else if (currentHex.getColour() != colour && !visited.contains(currentHex)) {
                visited.add(currentHex);
                if (!explore(currentHex.getAdjacent(), queue, visited, colour)) {
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

    boolean findTripod(Board board, int colour) {
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
    private int trace(Hex origin, Hex[] neighbours, int colour, Board board, PriorityQueue<Hex> visited) {
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
    boolean isCorner(Hex hex, int boardDimension) {
        return (hex.x == 0 && hex.y == 0) ||
                (hex.x == this.dimension - 1 && hex.y == 0) ||
                (hex.x == 0 && hex.y == this.dimension - 1) ||
                (hex.x == 2 * this.dimension - 2 && hex.y == this.dimension - 1) ||
                (hex.x == 0 && hex.y == 2 * this.dimension - 2) ||
                (hex.x == this.dimension - 1 && hex.y == 2 * this.dimension - 2);
    }
    
    /**
     * Checks whether a given set of coordinates is part of this board.
     * @param row
     * @param col
     * @return True if the coordinates are valid, false if not
     */
    boolean isLegal(int row, int col) 
    {
    	if (row < 0 || col < 0) {
    		return false;
    	}
    	if (row >= this.rows.length) {
    		return false;
    	}
    	if (col >= this.rows[row].length) {
    		return false;
    	}
    	return true;
    }
    
    Board applyMove(int row, int col, int colour) {
    	if (!isLegal(row, col)) {
    		System.err.println("Illegal move in applyMove function, sort that out on the quickfast.");
    	}
    	this.rows[row][col] = new Hex(row,col,colour,this);
    	return this;
    }
    
    @Override
    protected Board clone() {
    	Board newboard = new Board(this.dimension);
    	int i, j;
    	for (i = 0; i < this.rows.length; i++) {
    		for (j = 0; j < this.rows[i].length; j++) {
    			if (this.rows[i][j].colour != EMPTY) {
    				newboard.rows[i][j].colour = this.rows[i][j].colour;
    			}
    		}
    	}
		return newboard;
    }
    
    // debug function
    public void output() {
		int i, j;
		for (i = 0; i < this.rows.length; i++) {
			for (j = 0; j < this.rows[i].length; j++) {
				System.out.print(this.rows[i][j] + " ");
			}
			System.out.println();
		}
	}
}