import java.nio.file.Path;

import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Creates a thread safe inverted index by using the custom locks
 * 
 * @author sherryfeng
 */

public class SynchronizedInvertedIndex extends InvertedIndex {

	/** Logger to debug output */
	private static final Logger logger = LogManager.getLogger();

	/** Creates custom lock for threads */
	private ReadWriteLock lock;

	/**
	 * Initialize thread safe inverted index
	 */
	public SynchronizedInvertedIndex() {
		super();
		lock = new ReadWriteLock();
	}

	/**
	 * Calls addword method using custom locks
	 * 
	 * @param word
	 *            the word that is found in file
	 * @param position
	 *            the position the word is in
	 * @param location
	 *            where the word was found
	 */
	@Override
	public void addWord(String word, int position, String location) {
		lock.lockReadWrite();
		try {
			super.addWord(word, position, location);
		} finally {
			lock.unlockReadWrite();
		}
	}

	/**
	 * Calls exact search method using custom locks
	 * 
	 * @param queryLines
	 *            the exact phrase to search
	 * @return searchResult the results of the search
	 * 
	 */
	@Override
	public List<SearchResult> exactSearch(String[] queryLines) {
		lock.lockReadOnly();
		try {
			return super.exactSearch(queryLines);
		} finally {
			lock.unlockReadOnly();
		}

	}

	/**
	 * Calls partial search method using custom locks
	 * 
	 * @param queryLines
	 *            the partial phrase to search
	 * @return searchResult the results of the search
	 */

	@Override
	public List<SearchResult> partialSearch(String[] queryLines) {

		lock.lockReadOnly();
		try {
			return super.partialSearch(queryLines);
		} finally {
			lock.unlockReadOnly();
		}

	}

	/**
	 * Calls addindex method using custom locks
	 * 
	 * @param partialIndex
	 */
	public void addIndex(InvertedIndex partialIndex) {
		lock.lockReadWrite();
		try {
			super.addIndex(partialIndex);
		} finally {
			lock.unlockReadWrite();
		}
	}

	/**
	 * Calls writeJSON method using custom locks
	 * 
	 * @param output
	 *            writes the inverted index into the given path
	 */
	@Override
	public void writeJSON(Path output) {
		logger.debug("Gets into write JSON");
		lock.lockReadOnly();
		try {
			super.writeJSON(output);
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Calls toString method using custom locks
	 * 
	 */

	@Override
	public String toString() {
		lock.lockReadOnly();
		try {
			return super.toString();
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Calls containsWord method using custom locks
	 * 
	 * @param word
	 *            word to check if it contains
	 * @return true or false if it contains word
	 */

	@Override
	public boolean containsWord(String word) {

		lock.lockReadOnly();
		try {
			return super.containsWord(word);
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Calls containsLocation method using custom locks
	 * 
	 * @param location
	 *            location to check
	 * @return true or false if it contains location
	 */
	@Override
	public boolean containsLocation(String location) {
		lock.lockReadOnly();
		try {
			return super.containsLocation(location);
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Calls getMap method using custom locks
	 * 
	 * @return the map of the index
	 */
	@Override
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> getMap() {

		lock.lockReadOnly();
		try {
			return super.getMap();
		} finally {
			lock.unlockReadOnly();
		}
	}

}
