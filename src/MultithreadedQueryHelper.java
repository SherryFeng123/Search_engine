import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A multi threaded version of query helper
 * 
 * @author sherryfeng
 *
 */

public class MultithreadedQueryHelper extends QueryHelper {

	/** Logger for debug */
	private static final Logger logger = LogManager.getLogger();

	/** Creates workers from workqueue */
	private final WorkQueue workers;

	/** Creates custom lock for threads */
	private final ReadWriteLock lock;

	/**
	 * Initializes the multithreaded query helper
	 * 
	 * @param threadsafeIndex
	 *            index that is thread safe
	 * @param threads
	 *            number of threads to initialize
	 * 
	 * 
	 */
	public MultithreadedQueryHelper(SynchronizedInvertedIndex index, WorkQueue workers) {
		super(index);
		this.workers = workers;
		lock = new ReadWriteLock();
	}

	/**
	 * Read the path file by line
	 * 
	 * @param file
	 *            file to read flag the input flag from command args
	 * @throws IOException
	 */

	public void parseQueryFiles(Path file, String flag) throws IOException {
		logger.debug("In parseQueryFiles");

		try (BufferedReader reader = new BufferedReader(Files.newBufferedReader(file, Charset.forName("UTF-8")))) {
			String line;
			while ((line = reader.readLine()) != null) {
				workers.execute(new QueryRun(line, flag));
			}
			workers.finish();
		} catch (FileNotFoundException e) {
			System.err.println("Can not find file/path: " + file);
		}
	}

	/**
	 * The inner class constructed which implements runnable, it also creates a
	 * private query helper and calls the searches that corresponds to the flag,
	 * then searches through the index then returns the result
	 */

	private class QueryRun implements Runnable {

		private final String line;
		private final String flag;

		public QueryRun(String line, String flag) {
			this.line = line;
			this.flag = flag;
		}

		@Override
		public void run() {
			logger.debug("In queryRunner");
			String[] queryLines = InvertedIndexBuilder.clean(line);
			Arrays.sort(queryLines);

			List<SearchResult> sortedList = null;

			sortedList = searchIndexHelper(flag, queryLines);

			lock.lockReadWrite();

			try {
				putResultsHelper(queryLines, sortedList);
			} finally {
				lock.unlockReadWrite();
			}
		}
	}
	
	/**
	 * Helper method that calls  putsResults from super class
	 * @param queryLines
	 * @param sortedList
	 */

	public void putResultsHelper(String[] queryLines, List<SearchResult> sortedList) {
		super.putResults(queryLines, sortedList);
	}

	/**
	 * Helper method that calls searchIndex from super class
	 * @param flag
	 * @param line
	 * @return
	 */
	public List<SearchResult> searchIndexHelper(String flag, String[] line) {
		return super.searchIndex(line, flag);
	}

}