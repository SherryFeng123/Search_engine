import java.io.IOException;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Builds the inverted index, iterates through the files and parse it
 * 
 * @author sherryfeng
 *
 */
public class MultithreadedInvertedIndexBuilder extends InvertedIndexBuilder {

	/** Logger for debug */
	private static final Logger logger = LogManager.getLogger();

	/** Regex for any punctuation */
	private static final String regex = "[\\p{Punct}+]";

	/** Workers from the workqueue */
	private final WorkQueue workers;

	/** Thread safe inverted index */
	private final SynchronizedInvertedIndex multiIndex;

	/**
	 * Creates a multithreaded inverted index builder
	 * 
	 * @param threads
	 *            the number of threads to be passed in
	 * @param index
	 *            the thread safe index
	 * 
	 */
	public MultithreadedInvertedIndexBuilder(SynchronizedInvertedIndex index, WorkQueue workers) {

		super();
		multiIndex = index;
		this.workers = workers;
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

	private void traverseDir(Path directory, InvertedIndex index) {
		try (DirectoryStream<Path> directories = Files.newDirectoryStream(directory)) {
			for (Path path : directories) {
				if (Files.isDirectory(path)) {
					traverse(path, index);
				} else if (path.toString().toLowerCase().endsWith(".txt")) {
					workers.execute(new BuilderRun(path));
				}
			}
		} catch (IOException e) {
			System.err.println("Unable to traverse the path " + directory + ".");
			return;
		}
	}

	/**
	 * Helper method that calls traverseDir and finishes the worker
	 * 
	 * @param directory
	 *            the directory given to traverse through
	 * @param files
	 *            the files that is found in the directory
	 */

	public void traverse(Path directory, InvertedIndex index) {
		traverseDir(directory, index);
		workers.finish();
	}

	/**
	 * 
	 * The inner class constructed which implements runnable, it also creates a
	 * private index and adds it to the threadsafe index
	 *
	 */
	
	private class BuilderRun implements Runnable {

	
		private final InvertedIndex localIndex;
		private final Path file;

		BuilderRun(Path file) {
			this.file = file;
			localIndex = new InvertedIndex();
		}
		
		@Override
		public void run() {
			InvertedIndexBuilder.textParse(this.file, localIndex);
			logger.debug("In builderRun");
			multiIndex.addIndex(localIndex);
		}
	}
}