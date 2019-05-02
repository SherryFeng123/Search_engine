import java.io.IOException;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HomePageServlet extends LoginBaseServlet {

	LinkedList<String> totalQuery = new LinkedList<String>();

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String user = "guest";

		response.setContentType("text/html;carset=utf-8");

		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
		out.printf("<html>");
		out.printf("<center>");
		out.printf("<form method=\"post\" action=\"%s\">", request.getServletPath());
		out.printf(
				"<a href='https://postimg.org/image/ea4frw095/'target='_blank'><img src='https://s28.postimg.org/tvlrbuc7h/search_logo_md.png' border='0' alt='search-logo-md'/><br/></a><br /><br/>");
		out.printf("<b><p>Enter query:  </b>");
		out.printf("<input type=\"text\"name=\"userinput\" size=\"30\"></p>");
		out.printf("<p><input type=\"submit\" value=\"Search \"></p>");

		out.printf("<p><input type=\"submit\" name=\"showHistory\" value=\"Show search history \">");
		out.printf("<input type=\"submit\" name=\"clearHistory\" value=\"Clear history\">");

		out.printf("<form method=\"post\" action=\"form.asp\">");
		out.printf("<label for=\"switch\">  Turn off partial: </label>");
		out.printf("<input type=\"checkbox\" name=\"searchType\" id=\"switch\"></p>");

		out.printf("</form>");

		if (getUsername(request) != null) {
			user = getUsername(request);

			out.printf("<p>Hello " + user + "!  ");
			out.println("<a href=\"/login?logout\" class=\"btn btn-primary\" role=\"button\">Logout</a></p>");
			out.printf("<p><a href=\"/changepassword\">Change password </a><p>");
		
		}

		else {

			out.printf("<p><a href=\"/login\">Login</a></p>");
		}

		out.printf("<p><a href=\"/register\">Register a new account</a>");

		String lastFive = request.getParameter("5");
		out.printf("<p> Last 5 users logged in: " + lastFive + "</p>");
		out.printf("</body>");

		out.printf("</center>");
		out.printf("</html>");
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doGet(request, response);

		String flag = "QUERY_FLAG";

		if (request.getParameter("showHistory") != null) {
			response.sendRedirect(response.encodeRedirectURL("/history" + "?h=" + request.getParameter("userinput"))
					+ "&u=" + getUsername(request));
		}

		else if (request.getParameter("clearHistory") != null) {
			clearCookies(request, response);
		}

		else if (request.getParameter("suggest") != null) {
			response.sendRedirect(response.encodeRedirectURL("/suggest" + "?i=" + request.getParameter("userinput"))
					+ "&u=" + getUsername(request));
		}

		else {

			String userInput = request.getParameter("userinput");

			if (userInput != null) {

				Map<String, String> map = getCookieMap(request);
				response.addCookie(new Cookie(getUsername(request), userInput + ", " + map.get(getUsername(request))));

				// partial search toggle
				if (request.getParameter("searchType") != null) {
					flag = "EXACT_FLAG";
				}

				response.sendRedirect(response.encodeRedirectURL("/search" + "?s=" + userInput + "&f=" + flag));
			}
		}
	}

}
