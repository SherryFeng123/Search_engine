import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ChangePasswordServlet extends LoginBaseServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		
		PrintWriter out = response.getWriter();
		response.setContentType("text/html;carset=utf-8");

		response.setStatus(HttpServletResponse.SC_OK);
		out.printf("<html>");
		out.printf("<form method=\"post\" action=\"%s\">", request.getServletPath());
		out.printf("<p>Old password:</p>");
		out.printf("<input type=\"text\" name=\"oldPassword\" size=\"30\">");
		out.printf("<p>New password:</p>");
		out.printf("<input type=\"text\" name=\"newPassword\" size=\"30\">");
		out.printf("<p><input type=\"submit\" value=\"submit\"></p>");
		out.printf("</form>");
		
		out.printf("</html>");
		
		
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		response.setContentType("text/html;carset=utf-8");

		
		LoginBaseServlet loginB = new LoginBaseServlet();
		
		String username = loginB.getUsername(request);
		
		String oldP = request.getParameter("oldPassword");
		String newP = request.getParameter("newPassword");
		
		Status status = dbhandler.authenticateUser(username, oldP);
		
		if(status == Status.OK){
			dbhandler.removeUser(username, oldP);
			dbhandler.registerUser(username, newP);
			
//			set cookie for new login
			response.addCookie(new Cookie("login", "true"));
			response.addCookie(new Cookie("name", username));
			
			response.sendRedirect(response.encodeRedirectURL("/login"));
		}
		
		else{
			response.sendRedirect(response.encodeRedirectURL("/changepassword"));
		}
		
		response.setStatus(HttpServletResponse.SC_OK);	
	}

}
