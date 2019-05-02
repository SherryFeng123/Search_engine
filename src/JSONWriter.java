import java.io.BufferedWriter;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Outputs certain Java objects to a file in a "pretty" JSON format using the
 * tab character to indent nested elements and the new line character to
 * separate multiple elements. Only a small number of objects are currently
 * supported.
 * 
 * @author sherryfeng
 */

public class JSONWriter {

	/** Tab character used for pretty JSON output. */
	public static final char TAB = '\t';

	/** End of line character used for pretty JSON output. */
	public static final char END = '\n';

	/**
	 * Returns a quoted version of the provided text.
	 * 
	 * @param text
	 * @return "text" in quotes
	 */
	public static String quote(String text) {
		return String.format("\"%s\"", text);
	}

	/**
	 * Returns n tab characters.
	 * 
	 * @param n
	 *            number of tab characters
	 * @return n tab characters concatenated
	 */
	public static String tab(int n) {
		char[] tabs = new char[n];
		Arrays.fill(tabs, TAB);
		return String.valueOf(tabs);
	}

	/**
	 * Writes the position/ integer elements of the nested map to a file
	 * 
	 * @param elements
	 *            the elements to write
	 * @param writer
	 *            buffered writer to write to file
	 * @throws IOException
	 */

	public static void innerNestedWriter(TreeSet<Integer> elements, BufferedWriter writer) throws IOException {
		writer.write("[");
		writer.write("\n");
		for (Integer i : elements) {
			writer.write(tab(3));
			writer.write(i.toString());
			// if it is not the last element, do not put comma
			if (i != elements.last()) {
				writer.write(",");
			}
			writer.write("\n");
		}
		writer.write(tab(2));
		writer.write("]");
	}

	/**
	 * Writes the treemap of the nested treemap into a file
	 * 
	 * @param elements
	 *            the elements to write
	 * @param writer
	 *            buffered writer to write to file
	 * @throws IOException
	 */
	public static void outerNestedWriter(TreeMap<String, TreeSet<Integer>> elements, BufferedWriter writer)
			throws IOException {
		writer.write("{");
		writer.write("\n");
		if (!elements.isEmpty()) {
			for (Map.Entry<String, TreeSet<Integer>> entry : elements.entrySet()) {
				String key = entry.getKey();
				TreeSet<Integer> value = entry.getValue();
				writer.write(tab(2));
				writer.write(quote(key) + ": ");
				innerNestedWriter(value, writer);
				if (key != elements.lastKey()) {
					writer.write(",");
				}
				writer.write("\n");
			}
		}
		writer.write(tab(1));
		writer.write("}");
	}

	/**
	 * Wrapper method - writes treemap of the nested treemap into a file
	 * 
	 * @param elements
	 *            the elements to write
	 * @param path
	 *            the output location
	 * @throws IOException
	 */
	public static void outerNestedWriter(TreeMap<String, TreeSet<Integer>> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"))) {
			outerNestedWriter(elements, writer);
		}
	}

	/**
	 * Writes the position/ integer elements of the nested map to a file
	 * 
	 * @param elements
	 *            the elements to write
	 * @param path
	 *            output location of the file
	 * @throws IOException
	 */

	public static void innerNestedWriter(TreeSet<Integer> elements, Path path) throws IOException {		
		try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"))) {
			innerNestedWriter(elements, writer);
		}
	}

	/**
	 * Writes the inverted index (treemap) to a file
	 * 
	 * @param output
	 *            the location to write to
	 * @param map
	 *            the tree map data to write
	 */
	public static void nestedMapWriter(Path output, TreeMap<String, TreeMap<String, TreeSet<Integer>>> map) {	
	
		try (BufferedWriter writer = Files.newBufferedWriter(output, Charset.forName("UTF-8"))) {
			writer.write("{");
			writer.newLine();
			if (!map.isEmpty()) {
				for (Map.Entry<String, TreeMap<String, TreeSet<Integer>>> entry : map.entrySet()) {
					String key = entry.getKey();
					TreeMap<String, TreeSet<Integer>> value = entry.getValue();
					writer.write(tab(1));
					writer.write(quote(key) + ": ");
					outerNestedWriter(value, writer);
					if (key != map.lastKey()) {
						writer.write(",");
					}
					writer.newLine();
				}
			}
			writer.write("}");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.out.println("Unable to write to " + output);
		}
	}

	/**
	 * Writes the middle part of search result to an output file
	 * 
	 * @param value
	 *            the results to write
	 * @param path
	 *            output location of the file
	 * @throws IOException
	 */
	public static void outerSearchWriter(ArrayList<SearchResult> value, Path output) throws IOException {	
		try (BufferedWriter writer = Files.newBufferedWriter(output, Charset.forName("UTF-8"))) {
			outerSearchWriter(value, writer);
		}
	}

	/**
	 * Writes the inner search result to an output file
	 * 
	 * @param innerResult
	 *            the results to write
	 * @param output
	 *            output location of the file
	 * @throws IOException
	 */

	public static void innerResultWriter(SearchResult innerResult, Path output) throws IOException {	
		try (BufferedWriter writer = Files.newBufferedWriter(output, Charset.forName("UTF-8"))) {
			innerResultWriter(innerResult, writer);
		}
	}

	/**
	 * Writes the search result into an output file
	 * 
	 * @param output
	 *            output location of the file
	 * @param results
	 *            the search results to write
	 */

	public static void searchResultWriter(Path output, Map<String, ArrayList<SearchResult>> results) {
		
		int lastKey = results.size();
		int counter = 0;
		try (BufferedWriter writer = Files.newBufferedWriter(output, Charset.forName("UTF-8"))) {
			writer.write("{");
			writer.write("\n");
			if (!results.isEmpty()) {
				for (Entry<String, ArrayList<SearchResult>> entry : results.entrySet()) {
					counter++;
					String key = entry.getKey();
					ArrayList<SearchResult> value = entry.getValue();
					writer.write(tab(1));
					writer.write(quote(key) + ": ");
					
					outerSearchWriter(value, writer);
					if (counter != lastKey) {
						writer.write(",");
					}
					writer.newLine();
				}
			}
			writer.write("}");
		} catch (IOException e) {
			System.out.println("Unable to write to " + output);
		}
	}

	/**
	 * Writes the values of the search results
	 * 
	 * @param value
	 *            the elements to write
	 * @param writer
	 *            the writer that is passed in
	 * @throws IOException
	 */

	public static void outerSearchWriter(ArrayList<SearchResult> value, BufferedWriter writer) throws IOException {
		writer.write("[");
		
		if (value != null && !value.isEmpty()) {
			
			writer.newLine();
			SearchResult firstResult = value.get(0);
			innerResultWriter(firstResult, writer);
			value.remove(0);
			if (value != null && !value.isEmpty()) { 
				
				for (SearchResult inside : value) {
			     	writer.write(",");
					writer.newLine();
					innerResultWriter(inside, writer);
				}
			}
		}
		writer.newLine();
		writer.write(tab(1));
		writer.write("]");
	}

	/**
	 * Writes the location, frequency and initial position of the results
	 * 
	 * @param innerResult
	 *            the result to write
	 * @param writer
	 *            the writer that is passed in
	 * @throws IOException
	 */

	public static void innerResultWriter(SearchResult innerResult, BufferedWriter writer) throws IOException {
		writer.write(tab(2));
		writer.write("{");
		writer.newLine();
		writer.write(tab(3));
		writer.write(quote("where") + ": " + quote(innerResult.getLocation()) + ",");
		writer.newLine();
		writer.write(tab(3));
		writer.write(quote("count") + ": " + String.valueOf(innerResult.getFrequency()) + ",");
		writer.newLine();
		writer.write(tab(3));
		writer.write(quote("index") + ": " + String.valueOf(innerResult.getInitialPosition()));
		writer.newLine();
		writer.write(tab(2));
		writer.write("}");
	}
}
