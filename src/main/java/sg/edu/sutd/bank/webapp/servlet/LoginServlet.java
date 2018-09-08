/*
 * Copyright 2017 SUTD Licensed under the
	Educational Community License, Version 2.0 (the "License"); you may
	not use this file except in compliance with the License. You may
	obtain a copy of the License at

https://opensource.org/licenses/ECL-2.0

	Unless required by applicable law or agreed to in writing,
	software distributed under the License is distributed on an "AS IS"
	BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
	or implied. See the License for the specific language governing
	permissions and limitations under the License.
 */

package sg.edu.sutd.bank.webapp.servlet;

import java.io.IOException;
import java.util.Random;

import static sg.edu.sutd.bank.webapp.servlet.ServletPaths.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.*;

import sg.edu.sutd.bank.webapp.commons.ServiceException;
import sg.edu.sutd.bank.webapp.model.User;
import sg.edu.sutd.bank.webapp.model.UserStatus;
import sg.edu.sutd.bank.webapp.service.UserDAO;
import sg.edu.sutd.bank.webapp.service.UserDAOImpl;


@WebServlet(LOGIN)
public class LoginServlet extends DefaultServlet {
	private static final long serialVersionUID = 1L;
	private static final String RAND_CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	private UserDAO userDAO = new UserDAOImpl();
	
	private String generateRandomString() {
		StringBuffer rand = new StringBuffer();
		
		for(int i=0; i<10; i++) {
			int num = new Random().nextInt(RAND_CHAR_LIST.length());
			char rand_chr = RAND_CHAR_LIST.charAt(num);
			
			rand.append(rand_chr);
		}
		
		return rand.toString();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String userName = req.getParameter("username");
			
			User user = userDAO.loadUser(userName);
			if (user != null && (user.getStatus() == UserStatus.APPROVED)) {
				req.login(userName, req.getParameter("password"));
				HttpSession session = req.getSession(true);
				session.setAttribute("authenticatedUser", req.getRemoteUser());
				String randomToken = generateRandomString();
				session.setAttribute("csrfToken", randomToken);
				
				setUserId(req, user.getId());
				if (req.isUserInRole("client")) {
					redirect(resp, CLIENT_DASHBOARD_PAGE);
				} else if (req.isUserInRole("staff")) {
					redirect(resp, STAFF_DASHBOARD_PAGE);
				}
				return;
			}
			sendError(req, "Invalid username/password!");
		} catch(ServletException | ServiceException ex) {
			//sendError(req, ex.getMessage());
			sendError(req, "Invalid username/password!");
		}
		forward(req, resp);
	}

}
