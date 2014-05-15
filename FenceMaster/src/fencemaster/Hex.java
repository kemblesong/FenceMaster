/*By Kemble Song (584999) & Nicholas Poulton (585075)*/

/**
* This is a hex class. 
* It has a method for telling you what hexes are next to it.
* @author Nick
*/

package fencemaster;

@SuppressWarnings("rawtypes")
public class Hex implements Comparable{
	int x, y;
	char colour;
	Board board;
	
	/** Fill an array with all adjacent hexes. Moves
	* clockwise from top left, so that the northwestern
	* neighbour is at hexes[0] and the western one is
	* at hexes[5].
	*/
	public Hex[] getAdjacent() {
		Hex[] hexes = new Hex[6];
		int adjx = this.x, adjy = this.y;
		int i;
		for (i=0;i<6;i++) {
		/* Choose a direction relative to the base hex to
		  grab another hex from. */
			switch (i) {
				case 0: adjx -= 1;
						adjy -= 1;
						break;
				case 1: adjx += 1;
						break;
				case 2: adjx += 1;
						adjy += 1;
						break;
				case 3: adjy += 1;
						break;
				case 4: adjx -= 1;
						break;
				case 5: adjx -= 1;
						adjy -= 1;
						break;
			}
			// If there exists a hex at the target location, store it.
			try {
				hexes[i] = board.rows[adjy][adjx];
			}
			catch (ArrayIndexOutOfBoundsException e) {
				// If you've gone outside the board, do nothing.
			}
		}
		return hexes;
	}
	
 /**
 * Hex class constructor. Y-coordinate goes first, like in the project spec.
 * @param y The hex's y-coordinate on the board.
 * @param x The hex's x-coordinate on the board.
 * @param colour This hex's allegiance.
 * @param board The board this hex is on.
 */
	public Hex(int y, int x, char colour, Board board) 
	{
		this.x = x;
		this.y = y;
		this.colour = colour;
		this.board = board;
	}

	/**
	 * The method used to convert the hex to a string when it's put into
	 * a print() method or similar. Returns the hex's colour.
	 */
	public String toString() {
		/* The primitive type char must be typecast to Character first
		 * before the toString() method can be called on it.
		 */
		return ((Character)(this.getColour())).toString();
	}
	
	public char getColour() {
		return colour;
	}

	public void setColour(char colour) {
		this.colour = colour;
	}
 
  // This method only exists so that java won't complain when we put hexes in priority queues.
	public int compareTo(Object arg0) {
		return 0;
	}

}
