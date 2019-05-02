import java.io.IOException;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;


public class Driver {

	public static final String INDEX_FLAG = "-index";
	public static final String DIR_FLAG = "-dir";
	public static final String QUERY_FLAG = "-query";
	public static final String EXACT_FLAG = "-exact";
	public static final String RESULTS_FLAG = "-results";
	public static final String URL_FLAG = "-url";
	public static final String MULTI_FLAG = "-multi";
	public static final String INDEXFILE = "index.json";
	public static final String RESULTSFILE = "results.json";
	public static final String QFLAG = "QUERY_FLAG";
	public static final String EFLAG = "EXACT_FLAG";
	public static final String PORT_FLAG = "-port";
	

	/**
	 * Identifies the flag/command line input and acts accordingly If it is a
	 * directory flag, it will call directory traverser and store the path into
	 * text if it is index flag, it will call build the inverted index and into
	 * an output file
	 * 
	 * @author sherryfeng
	 * 
	 * @param args
	 *            the given argument from the command line
	 * @throws Exception 
	 */

	public static void main(String[] args) throws Exception {

		ArgumentParser argumentParser = new ArgumentParser(args);
		InvertedIndex index;
		QueryHelper queryHelper;
		WebCrawler crawler;
		InvertedIndexBuilder builder;
		WorkQueue workers = null;

		if (argumentParser.hasFlag(MULTI_FLAG) && argumentParser.getValue(MULTI_FLAG) != null) {

			int threads = 5;
			try {
				if (Integer.parseInt(argumentParser.getValue(MULTI_FLAG)) > 0) {
					threads = argumentParser.getValue(MULTI_FLAG, 5);
				}
			} catch (Exception e) {
				System.out.println("Multi flag exception " + e);
			}
			workers = new WorkQueue(threads);
			SynchronizedInvertedIndex threadSafeIndex = new SynchronizedInvertedIndex();
			index = threadSafeIndex;
			builder = new MultithreadedInvertedIndexBuilder(threadSafeIndex, workers);
			crawler = new MultithreadedWebCrawler(threadSafeIndex, workers);
			queryHelper = new MultithreadedQueryHelper(threadSafeIndex, workers);

		} else {
			index = new InvertedIndex();
			queryHelper = new QueryHelper(index);
			crawler = new WebCrawler(index);
			builder = new InvertedIndexBuilder();
		}
		if (argumentParser.hasFlag(DIR_FLAG) && argumentParser.hasValue(DIR_FLAG)) {
			if (Files.isDirectory(Paths.get(argumentParser.getValue(DIR_FLAG)))) {
				builder.traverse(Paths.get(argumentParser.getValue(DIR_FLAG)), index);
			}
		}
		if (argumentParser.hasFlag(URL_FLAG) && argumentParser.hasValue(URL_FLAG)) {
			crawler.crawlSeed(argumentParser.getValue(URL_FLAG));
		}
		if (argumentParser.hasFlag(QUERY_FLAG) && argumentParser.hasValue(QUERY_FLAG)) {
			Path queryFile = Paths.get(argumentParser.getValue(QUERY_FLAG));
			if (Files.exists(queryFile)) {
				try {
					queryHelper.parseQueryFiles(queryFile, QFLAG);
				} catch (IOException e) {
					System.out.println("Cannot parse file: " + queryFile);
				}
			}
		}
		if (argumentParser.hasFlag(EXACT_FLAG) && argumentParser.hasValue(EXACT_FLAG)) {
			Path queryFile = Paths.get(argumentParser.getValue(EXACT_FLAG));
			if (Files.exists(queryFile)) {
				try {
					queryHelper.parseQueryFiles(queryFile, EFLAG);
				} catch (IOException e) {
					System.out.println("Cannot parse file: " + queryFile);
				}
			}
		}
		if (argumentParser.hasFlag(RESULTS_FLAG)) {
			Path queryOutput = Paths.get(argumentParser.getValue(RESULTS_FLAG, RESULTSFILE));
			queryHelper.toJSON(queryOutput);
		}
		if (argumentParser.hasFlag(INDEX_FLAG)) {
			Path outputPath = Paths.get(argumentParser.getValue(INDEX_FLAG, INDEXFILE));
			index.writeJSON(outputPath);
		}
		
		if (workers != null) {
			workers.shutdown();
		}

		if (argumentParser.hasFlag(PORT_FLAG)) {
		
			int portNum = argumentParser.getValue(PORT_FLAG, 8080);
			
			Server server = new Server(portNum);
			ServletContextHandler handler = new ServletContextHandler();
			handler.addServlet(new ServletHolder(new SearchServlet(index)), "/search");
			handler.addServlet(new ServletHolder(new CrawlerServlet(index)), "/crawl");
			handler.addServlet(LoginUserServlet.class, "/login");
			handler.addServlet(LoginRegisterServlet.class, "/register");
			handler.addServlet(HomePageServlet.class, "/home");
			handler.addServlet(HistoryServlet.class, "/history");
			handler.addServlet(ChangePasswordServlet.class, "/changepassword");
			handler.addServlet(SuggestServlet.class, "/suggest");
			
			server.setHandler(handler);
			server.start();
			server.join();
		}
	}
}
