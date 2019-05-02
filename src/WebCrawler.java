import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Crawls through the given URL by breadth first search and stores as an
 * inverted index
 * 
 * @author sherryfeng
 *
 */
public class WebCrawler {

	/** Hashset for url */
	private HashSet<String> urlSet;

	/** Queue for url */
	private Queue<String> urlQueue;

	/** Size of links to crawl */
	private int crawlLimit = 50;

	/** Index for url */
	private final InvertedIndex urlIndex;

	/** Initialize the web crawler */
	public WebCrawler(InvertedIndex urlIndex) {
		this.urlIndex = urlIndex;
		this.urlSet = new HashSet<String>();
		this.urlQueue = new LinkedList<String>();
	}

	/**
	 * Takes in a url and adds to hashset and queue
	 * 
	 * @param url
	 *            url to add
	 */
	private void addQueue(String url) {
		if (!urlSet.contains(url) && urlSet.size() < crawlLimit) {
			urlSet.add(url);
			urlQueue.add(url);
		}
	}

	/**
	 * Add the seed and dequeues
	 * 
	 * @param seed
	 *            the seed to add and crawl
	 */

	public void crawlSeed(String seed) {
		addQueue(seed);

		while (!urlQueue.isEmpty()) {
			
			crawl(urlQueue.remove(), urlIndex);
		}

	}

	public void addCrawlerIndex(InvertedIndex threadLocalIndex) {
		urlIndex.addIndex(threadLocalIndex);
	}

	/**
	 * Takes in a url and gets the words to add to the index and gets the url
	 * links from the page to crawl
	 * 
	 * @param url
	 *            the url to crawl
	 */
	public void crawl(String url, InvertedIndex urlIndex) {

		String html;
		try {
			URL base = new URL(url);
			html = HTTPFetcher.fetchHTML(url);
			ArrayList<String> innerUrl = LinkParser.listLinks(html);

			for (String i : innerUrl) {
				URL absolute = new URL(base, i);
				addQueue(cleanURL(absolute));
			}

			html = HTMLCleaner.cleanHTML(html);
			String[] results = HTMLCleaner.parseWords(html);

			int position = 0;

			for (String word : results) {
				position++;
				urlIndex.addWord(word, position, url);
			}
		} catch (IOException e) {
			System.out.println("Cannot get url: " + url);
		}
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
