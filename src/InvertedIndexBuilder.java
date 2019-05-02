import java.io.BufferedReader;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * Builds the inverted index, iterates through the files and parse it
 * 
 * @author sherryfeng
 *
 */

public class InvertedIndexBuilder {

	
	/** Regex for any punctuation */
	private static final String regex = "[\\p{Punct}+]";
	


	/**
	 * Converts the text into lower case, replaces special characters with an
	 * empty string, and trims whitespace at the start and end of the string.
	 * 
	 * @param text
	 *            the text given to clean up
	 * @return text
	 * 			 cleaned up text or empty if input was empty
	 */
	public static String[] clean(String text) {
		if (!text.isEmpty()) {
			text = text.toLowerCase();
			text = text.replaceAll(regex, "");
			text = text.trim();
			return text.split("\\p{Space}+");
		} else {
			String[] emptyString = {};
			return emptyString;
		}
	}
	
	/**
	 * Traverses through a directory and returns a file with the list of the
	 * path
	 * 
	 * @param directory
	 *            the directory given to traverse through
	 * @param files
	 *            the files that is found in the directory
	 */
	
	public void traverse(Path directory, InvertedIndex index) {

		try (DirectoryStream<Path> directories = Files.newDirectoryStream(directory)) {
			
			for (Path path : directories) {
				if (Files.isDirectory(path)) {
					traverse(path, index);
				}
				else if (path.toString().toLowerCase().endsWith(".txt")) {
	
					textParse(path, index);
					
				}
			}
		} catch (IOException e) {
			System.err.println("Unable to traverse the path " + directory + ".");
			return;
		}
	}


	/**
	 * Reads in the file, calls FileParser.clean to remove char and spaces, then
	 * adds the word into the index along with the position
	 * 
	 * @param inputFile
	 *            the given file to parse
	 * @param index
	 *            the index it is going to be added to
	 */
	public static void textParse(Path inputFile, InvertedIndex index) {
	
		String[] words;
		try (BufferedReader reader = Files.newBufferedReader(inputFile, Charset.forName("UTF-8"))) {
			String line = null;
			int position = 1;
			while ((line = reader.readLine()) != null) {
				words = InvertedIndexBuilder.clean(line);
				for (String i : words) {
					if (!i.isEmpty()) {
						index.addWord(i, position, inputFile.toString());
						position++;
					}

				}
			}
		
			
		} catch (IOException e) {
			System.out.println("Unable to text parse the file " + inputFile.toString());
		}
	}
}