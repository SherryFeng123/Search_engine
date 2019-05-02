import java.io.IOException;

import javax.servlet.http.Cookie;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SuggestServlet extends LoginBaseServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setContentType("text/html;carset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
		
		String suggestions = request.getParameter("s");
		
		out.printf("<html>");
		out.printf("<head><title>Suggested</title></head>");
		out.printf("<body>");
		out.printf("<center>");
		out.printf("<h1>Suggested queries: </h1>");
		out.printf("<p>(Based on other user's latest search)</p>");
		out.printf("<p>" + suggestions + "</p>");
		

		out.printf("</center>");
		out.printf("</body>");
		out.printf("</html>");
	}
}
