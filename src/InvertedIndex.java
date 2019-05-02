import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Creates the inverted index data structure and calls inverted index builder
 * 
 * @author sherryfeng
 */

public class InvertedIndex {

	/** Tripled Nested Treemap of index */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;

	/**
	 * Initialize inverted index
	 */
	public InvertedIndex() {
		index = new TreeMap<>();
	}

	/**
	 * Adds the cleaned up words into the nested map
	 * 
	 * @param word
	 *            the word that is found in file
	 * @param position
	 *            the position the word is in
	 * @param location
	 *            where the word was found
	 */
	public void addWord(String word, int position, String location) {
		if (index.containsKey(word)) {
			// if the word is in a different file
			if (!index.get(word).containsKey(location)) {
				TreeSet<Integer> positionSet1 = new TreeSet<Integer>();
				positionSet1.add(position);
				index.get(word).put(location, positionSet1);
			} else {
				index.get(word).get(location).add(position); // middle
			}
		} else { // to add this new word, create middle map here
			TreeSet<Integer> positionSet2 = new TreeSet<Integer>();
			positionSet2.add(position);
			// create new middle map, middle one is value of outerword map
			TreeMap<String, TreeSet<Integer>> middleFileMap = new TreeMap<String, TreeSet<Integer>>();
			// add it in the middle fileMap first
			middleFileMap.put(location, positionSet2);
			index.put(word, middleFileMap);
		}
	}

	/**
	 * Searches through the index for the exact phrase
	 * 
	 * @param queryLines
	 *            the exact phrase to search
	 * @return searchResult 
	 *            the results of the search
	 * 
	 */

	public List<SearchResult> exactSearch(String[] queryLines) {
		

		Map<String, SearchResult> tempMap = new HashMap<>();
		List<SearchResult> searchResult = new ArrayList<>();

		for (String q : queryLines) {
			
			TreeMap<String, TreeSet<Integer>> wordIndex = index.get(q);
			if (wordIndex != null) {
				for (Map.Entry<String, TreeSet<Integer>> wordValues : wordIndex.entrySet()) {
					String path = wordValues.getKey();
					int frequency = wordValues.getValue().size();
					int initialPosition = wordValues.getValue().first();
					if (!tempMap.containsKey(path)) {
						SearchResult singleSearchResult = new SearchResult(initialPosition, frequency, path);
						// path is the key for the search result
						tempMap.put(path, singleSearchResult);
						searchResult.add(singleSearchResult);
					} else { // if it is already there
						tempMap.get(path).updateFrequency(frequency);
						tempMap.get(path).updateInitialPosition(initialPosition);
					}
				}
			}
		}

		Collections.sort(searchResult);
		return searchResult;
	}

	/**
	 * Searches through the index for partial phrase
	 * 
	 * @param queryLines
	 *            the partial phrase to search
	 * @return searchResult 
	 * 			  the results of the search
	 */

	public List<SearchResult> partialSearch(String[] queryLines) {
		
		
		String path;
		int frequency;
		int initialPosition;

		
		HashMap<String, SearchResult> tempMap = new HashMap<>();
		List<SearchResult> searchResult = new ArrayList<>();

		for (String q : queryLines) {		

			for (String word = index.ceilingKey(q); word != null && word.startsWith(q); word = index.higherKey(word)) {
			
				for (Map.Entry<String, TreeSet<Integer>> wordValues : index.get(word).entrySet()) {
					
					path = wordValues.getKey();
					frequency = wordValues.getValue().size();
					initialPosition = wordValues.getValue().first();
					

					
					if (!tempMap.containsKey(path)) {
						SearchResult singleSearchResult = new SearchResult(initialPosition, frequency, path);
						tempMap.put(path, singleSearchResult);
						searchResult.add(singleSearchResult);
					} else {
						tempMap.get(path).updateInitialPosition(initialPosition);
						tempMap.get(path).updateFrequency(frequency);
					}
				}
			}
		}
		
		Collections.sort(searchResult);
		
		return searchResult;
	}

	/**
	 * adds the partial inverted index to another inverted index
	 * 
	 * @param partialIndex
	 */

	public void addIndex(InvertedIndex partialIndex) {
		for (Map.Entry<String, TreeMap<String, TreeSet<Integer>>> entry : partialIndex.index.entrySet()) {
			String key = entry.getKey();

			if (!index.containsKey(key)) {
				index.put(key, entry.getValue());
			} else {
				for (Map.Entry<String, TreeSet<Integer>> innerEntry : partialIndex.index.get(key).entrySet()) {
					String path = innerEntry.getKey();
					if (!index.get(key).containsKey(path)) {
						index.get(key).put(path, innerEntry.getValue());
					} else {
						index.get(key).get(path).addAll(partialIndex.index.get(key).get(path));
					}
				}
			}
		}
	}


	/**
	 * Writes the inverted index as JSON to a file.
	 * 
	 * @param output
	 *            writes the inverted index into the given path
	 */

	public void writeJSON(Path output) {
		JSONWriter.nestedMapWriter(output, index);
	}

	/**
	 * Returns a toString of the index
	 * 
	 */
	@Override
	public String toString() {
		return index.toString();
	}

	/**
	 * Checks if map contains the word
	 * 
	 * @param word
	 *            word to check if it contains
	 * @return true or false if it contains word
	 */

	public boolean containsWord(String word) {
		return index.containsKey(word);
	}

	/**
	 * Checks if map contains the location
	 * 
	 * @param location
	 *            location to check
	 * @return true or false if it contains location
	 */

	public boolean containsLocation(String location) {
		return index.containsValue(location);
	}

	/**
	 * Returns the map of the inverted index
	 * 
	 * @return nested treemap of inverted index
	 */
	TreeMap<String, TreeMap<String, TreeSet<Integer>>> getMap() {
		return index;
	}
	
	/**
	 * Checks if the index is empty
	 * @return true or false whether the index is empty or not
	 */
	
	public boolean isEmpty() {
		return index.isEmpty();
	}
	
}
