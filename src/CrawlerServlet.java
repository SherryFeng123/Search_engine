import java.io.IOException;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CrawlerServlet extends LoginUserServlet {

	private final WebCrawler crawler;
	private final InvertedIndex index;

	public CrawlerServlet(InvertedIndex index) {
		this.index = index;
		crawler = new WebCrawler(this.index);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setContentType("text/html;carset=utf-8");

		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();

		out.printf("<center>");

		out.printf("<form method=\"post\" action=\"%s\">", request.getServletPath());
		out.printf("<b><p>Crawl:  </b>");
		out.printf("<input type=\"text\"name=\"newCrawl\" size=\"30\"></p>");
		out.printf("<p><input type=\"submit\" value=\"submit\"></p>");

		out.printf("</body>");

		out.printf("</center>");
		out.printf("</html>");

	}
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (request.getParameter("newCrawl") != null) {
			String lastFive1 = request.getParameter("LAST5");
			crawler.crawlSeed(request.getParameter("newCrawl"));
			response.sendRedirect(response.encodeRedirectURL("/home" + "?5=" + lastFive1));
		}

	}
}
