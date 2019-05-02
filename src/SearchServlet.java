
import java.io.IOException;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchServlet extends HttpServlet {

	private final InvertedIndex index;
	List<String> suggestions;

	public SearchServlet(InvertedIndex index) {
		this.index = index;
		suggestions = new ArrayList<String>();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;carset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
		out.printf("<html>%n");
		out.printf("<head><title>Search Results</title></head>%n");
		out.printf("<body>%n");
		String input = request.getParameter("s");
		String flag = request.getParameter("f");
		
		suggestions.add(input);
		if(suggestions.size()>5){
			suggestions.remove(0);
		}

		SearchResults(input, out, flag);
		out.printf("<form method=\"post\" action=\"%s\">", request.getServletPath());
		out.printf("<input type=\"submit\" name=\"suggested\" value=\"Suggested queries \">");
		out.printf("</form>");

		out.printf("<p><a href=\"/crawl\">Crawl</a></p>");

		out.printf("</body>");
		out.printf("</html>");
		response.setStatus(HttpServletResponse.SC_OK);
	}

	private void SearchResults(String input, PrintWriter out, String flag) {

		double start = 0;
		double time = 0;
		double end = 0;

		List<SearchResult> result = null;

		String[] queryLines = InvertedIndexBuilder.clean(input);
		Arrays.sort(queryLines); 

		out.printf("<p>You searched for: %s</p>", input);

		if (input.isEmpty()) {
			out.printf("Input is empty! Please enter a valid input.");
		}

		else {

			if (flag.equals("QUERY_FLAG")) {
				start = System.nanoTime();
				result = index.partialSearch(queryLines);
				end = System.nanoTime();
				time = end - start;
			}

			else if (flag.equals("EXACT_FLAG")) {
				start = System.nanoTime();
				result = index.exactSearch(queryLines);
				end = System.nanoTime();
				time = end - start;
			}

			if (!result.isEmpty()) {
				for (SearchResult i : result) {
					out.printf("<p><a href=\"" + i.getLocation() + "\">" + i.getLocation() + "</a></p>");
					
				}

			} else {
				out.printf("<p>Sorry! Your search did not return any results</p>");
			}
		}

		out.printf("It took " + time + " nanoseconds to search!");

	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		if(request.getParameter("suggested")!=null){
			response.sendRedirect(response.encodeRedirectURL("/suggest" + "?s=" + suggestions));
		
		}
	}
}
