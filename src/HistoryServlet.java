import java.io.IOException;


import javax.servlet.http.Cookie;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HistoryServlet extends LoginBaseServlet{
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String username = request.getParameter("u");
		
		response.setContentType("text/html;carset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
		out.printf("<html>");
		out.printf("<head><title>History</title></head>");
		out.printf("<body>");
		out.printf("<center>");
		out.printf("<h1>Your search history: </h1>");
		
		
		Cookie[] cookies = request.getCookies();
		
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if(cookie.getName().equals(username)){
					for(String i: cookie.getValue().split(",")){
						i = i.replace("null", "");
						out.println("<p>" + i + "</p>");
					}
				}
			}
		}
		
		out.printf("</center>");
		out.printf("</body>");
		out.printf("</html>");
	
	}


}
