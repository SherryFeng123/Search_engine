/**
 * Stores and sorts a single search result
 * 
 * @author sherryfeng
 *
 */

public class SearchResult implements Comparable<SearchResult> {

	/** The location the string was found in */
	private final String location;
	/** Number of times the string has been found */
	private int frequency;
	/** The initial position the string appeared */
	private int initialPosition;

	/**
	 * Initialize a search result
	 * 
	 * @param frequency
	 *            number of the search result
	 * @param initialPosition
	 *            the first position of the search result
	 * @param location
	 *            location of the search result
	 */
	public SearchResult(int initialPosition, int frequency, String location) {
		this.frequency = frequency;
		this.initialPosition = initialPosition;
		this.location = location;
	}

	/**
	 * Sort the arrayList by frequency, then by index, then by the name of the
	 * location
	 * 
	 * @param input
	 *            the input given
	 */
	@Override
	public int compareTo(SearchResult input) {
		
		int frequencyResult = Integer.compare(input.frequency, this.frequency);
		
		if (frequencyResult == 0) {
			int positionResult = Integer.compare(this.initialPosition, input.initialPosition);
			if (positionResult == 0) {
				//int locationResult = String.CASE_INSENSITIVE_ORDER.compare(this.location, input.location);
				int locationResult = this.location.compareTo(input.location);
				return locationResult;
			}
			return positionResult;
		}
		return frequencyResult;
	}

	/**
	 * Prints by overriding toString
	 */
	@Override
	public String toString() {
		return this.location + this.frequency + this.initialPosition;
	}

	/**
	 * Returns frequency
	 * 
	 * @return frequency
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * Returns location
	 * 
	 * @return location
	 */
	public String getLocation() {
		return location;

	}

	/**
	 * Updates the frequency
	 * 
	 * @param frequency
	 */
	public void updateFrequency(int frequency) {
		this.frequency += frequency;
	}

	/**
	 * Compares input position and updates to earlier position
	 * 
	 * @param initialPosition
	 */
	public void updateInitialPosition(int position) {
		if (position < this.initialPosition) {
			this.initialPosition = position;
		}
	}

	/**
	 * Returns initialPosition
	 * 
	 * @return initialPosition
	 */
	public int getInitialPosition() {
		return initialPosition;
	}

}