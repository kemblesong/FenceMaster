/**
* This is a hex class. 
* It has a method for telling you what hexes are next to it.
* @author Nick
*/

public class Hex {
	private int x, y;
	private char colour;
	private Board board;
	
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
				// Do nothing. Man, there has to be a more elegant way to do this.
			}
		}
		return hexes;
	}
	
	public Hex(int y, int x, char colour, Board board) 
	{
		this.x = x;
		this.y = y;
		this.colour = colour;
		this.board = board;
	}

	/**
	 * The method used to convert the hex to a string when it's put into
	 * a print() method or similar. Currently not working, will debug later.
	 */
	public String toString() {
		String str = " ";
		str.replace(' ', this.getColour());
		return str;
	}
	
	public char getColour() {
		return colour;
	}

	public void setColour(char colour) {
		this.colour = colour;
	}

}
