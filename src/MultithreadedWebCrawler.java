import java.io.IOException;

import java.net.URL;
import java.util.HashSet;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Crawls through the given URL by breadth first search and stores as an
 * inverted index
 * 
 * @author sherryfeng
 *
 */

public class MultithreadedWebCrawler extends WebCrawler {

	/** Logger for debug */
	private static final Logger logger = LogManager.getLogger();

	/** Hashset for url */
	private final HashSet<String> urlSet;

	/** Workers from the workqueue */
	private final WorkQueue workers;

	/** Craw limit **/
	private volatile int limit = 0;

	/**
	 * Initialize the web crawler
	 * 
	 * @param urlIndex
	 *            thread safe index
	 * @param limit
	 *            the crawl limit
	 * @param threads
	 *            number of threads to use
	 * 
	 */
	public MultithreadedWebCrawler(SynchronizedInvertedIndex index, WorkQueue workers) {
		super(index);
		this.urlSet = new HashSet<String>();
		this.workers = workers;
	}

	/**
	 * Takes in a url and adds to hashset (to check duplicates) and queue
	 * 
	 * @param url
	 *            url to add
	 */
	private void addUrl(String url) {
		logger.debug("Url " + url);
		if (!urlSet.contains(url) && urlSet.size() < 50) {
			urlSet.add(url);
			workers.execute(new CrawlRun(url));
		}
	}

	/**
	 * Add the seed and dequeues using threads
	 * 
	 * @param seed
	 *            the seed to add and crawl
	 */
	public void crawlSeed(String seed) {
		limit += 50;
		addUrl(seed);
		workers.finish();
	}

	/**
	 * Takes in a url and gets the words to add to the index and gets the url
	 * links from the page to crawl
	 * 
	 * @param url
	 *            the url to crawl
	 */
	private class CrawlRun implements Runnable {

		private final String url;
		private final InvertedIndex index;

		public CrawlRun(String url) {
			this.url = url;
			this.index = new InvertedIndex();
		}

		@Override
		public void run() {
			if (limit > 0) {
				limit--;
			} else {
				return;
			}

			logger.debug("Gets inside run");
			String html;
			try {
				URL base = new URL(url);
				html = HTTPFetcher.fetchHTML(url);
				List<String> innerUrl = LinkParser.listLinks(html);
				for (String i : innerUrl) {
					URL absolute = new URL(base, i);
					addUrl(cleanURL(absolute));
				}
				html = HTMLCleaner.cleanHTML(html);
				String[] results = HTMLCleaner.parseWords(html);

				int position = 0;
				for (String word : results) {
					position++;
					index.addWord(word, position, url);
				}

				addIndexHelper(index);

			} catch (IOException e) {
				System.out.println("Cannot get url: " + url);
			}
		}
	}

	public void addIndexHelper(InvertedIndex index) {
		super.addCrawlerIndex(index);
	}

	/**
	 * Takes in a URL and cleans it
	 * 
	 * @param url
	 *            the input URL
	 * @return a string of the url
	 */
	public String cleanURL(URL url) {
		return url.getProtocol() + "://" + url.getHost() + url.getFile();
	}

}
