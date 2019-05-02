import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

/**
 * Takes in search phrases, sorts them and calls the corresponding flag and
 * returns the result of the search
 * 
 * @author sherryfeng
 *
 */

public class QueryHelper {

	/** Treemap to store the results in sorted order */
	private final TreeMap<String, ArrayList<SearchResult>> results;
	
	/** Index to search */
	private final InvertedIndex index;

	/**
	 * Initialize results and index
	 */
	public QueryHelper(InvertedIndex index) {
		results = new TreeMap<String, ArrayList<SearchResult>>();
		this.index = index;
		
		
	}

	/**
	 * Read the path file by line
	 * 
	 * @param file
	 *            file to read flag the input flag from command args
	 * @throws IOException
	 */
	public void parseQueryFiles(Path file, String flag) throws IOException {

		try (BufferedReader reader = new BufferedReader(Files.newBufferedReader(file, Charset.forName("UTF-8")))) {

			String line;
			while ((line = reader.readLine()) != null) {
				parseLine(flag, line);
			}
		} catch (FileNotFoundException e) {
			System.err.println("Can not find file/path: " + file);
		}
	}

	/**
	 * Takes in a line and the flag, parses and puts the search result in map
	 * 
	 * @param flag
	 *            the flag called for exact or partial search
	 * @param lines
	 *            the line to parse
	 */

	public void parseLine(String flag, String line) {
		
		String[] queryLines = InvertedIndexBuilder.clean(line);
		Arrays.sort(queryLines); // sort the search phrases itself

		if (flag.equals("EXACT_FLAG")) {
		
			List<SearchResult> sortedlist = index.exactSearch(queryLines);
			results.put(String.join(" ", queryLines), (ArrayList<SearchResult>) sortedlist);
			
		}

		if (flag.equals("QUERY_FLAG")) {
			
			
			List<SearchResult> sortedlist = index.partialSearch(queryLines);
			results.put(String.join(" ", queryLines), (ArrayList<SearchResult>) sortedlist);
			
		}
		

		
	}

	/**
	 * Takes in query and the sorted list of search result and put it in results
	 * map
	 * 
	 * @param queryLines
	 * @param sortedList
	 */

	public void putResults(String[] queryLines, List<SearchResult> sortedList) {
		results.put(String.join(" ", queryLines), (ArrayList<SearchResult>) sortedList);
	}

	/**
	 * Takes in query and the flag, calls and returns the search corresponding
	 * to the flag
	 * 
	 * @param line
	 * @param flag
	 * @return exact or partial search
	 */

	public List<SearchResult> searchIndex(String[] line, String flag) {

		if (flag == "EXACT_FLAG") {
			
			return index.exactSearch(line);
		}

		else if (flag == "QUERY_FLAG") {
			return index.partialSearch(line);
		}

		return null;
	}

	/**
	 * Writes the inverted index as JSON to a file.
	 * 
	 * @param output
	 */
	public void toJSON(Path output) {
		JSONWriter.searchResultWriter(output, results);
	}
	
	/**
	 * Returns the results of search
	 * @return
	 */
	
	public TreeMap<String, ArrayList<SearchResult>> getResult() {
		return results;	
	}
	
	public InvertedIndex getIndex(){
		return this.index;
	}

}
