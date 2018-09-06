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
import java.sql.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sg.edu.sutd.bank.webapp.commons.ServiceException;
import sg.edu.sutd.bank.webapp.model.ClientInfo;
import sg.edu.sutd.bank.webapp.model.Role;
import sg.edu.sutd.bank.webapp.model.User;
import sg.edu.sutd.bank.webapp.model.UserRole;
import sg.edu.sutd.bank.webapp.service.ClientInfoDAO;
import sg.edu.sutd.bank.webapp.service.ClientInfoDAOImpl;
import sg.edu.sutd.bank.webapp.service.EmailService;
import sg.edu.sutd.bank.webapp.service.EmailServiceImp;
import sg.edu.sutd.bank.webapp.service.UserDAO;
import sg.edu.sutd.bank.webapp.service.UserDAOImpl;
import sg.edu.sutd.bank.webapp.service.UserRoleDAO;
import sg.edu.sutd.bank.webapp.service.UserRoleDAOImpl;
import sg.edu.sutd.bank.webapp.service.XSSCompliant;

/**
 * @author SUTD
 */
@WebServlet("/register")
public class RegisterServlet extends DefaultServlet {
	private static final long serialVersionUID = 1L;
	private ClientInfoDAO clientAccountDAO = new ClientInfoDAOImpl();
	private UserDAO userDAO = new UserDAOImpl();
	private UserRoleDAO userRoleDAO = new UserRoleDAOImpl();
	private EmailService emailService = new EmailServiceImp();

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		User user = new User();
		ClientInfo clientAccount = new ClientInfo();
		
		try {
			String fullName = XSSCompliant.getInstance().convertXSSComplianceString(request.getParameter("fullName"));
			String fin = XSSCompliant.getInstance().convertXSSComplianceString(request.getParameter("fin"));
			String dateOfBirth = XSSCompliant.getInstance().convertXSSComplianceString(request.getParameter("dateOfBirth"));
			String occupation = XSSCompliant.getInstance().convertXSSComplianceString(request.getParameter("occupation"));
			String mobileNumber = XSSCompliant.getInstance().convertXSSComplianceString(request.getParameter("mobileNumber"));
			String address = XSSCompliant.getInstance().convertXSSComplianceString(request.getParameter("address"));
			String email = XSSCompliant.getInstance().convertXSSComplianceString(request.getParameter("email"));
			String username = XSSCompliant.getInstance().convertXSSComplianceString(request.getParameter("username"));
			String password = XSSCompliant.getInstance().convertXSSComplianceString(request.getParameter("password"));
			
			clientAccount.setFullName(fullName);
			clientAccount.setFin(fin);
			clientAccount.setDateOfBirth(Date.valueOf(dateOfBirth));
			clientAccount.setOccupation(occupation);
			clientAccount.setMobileNumber(mobileNumber);
			clientAccount.setAddress(address);
			clientAccount.setEmail(email);
			
			user.setUserName(username);
			user.setPassword(password);
			clientAccount.setUser(user);
			
			userDAO.create(user);
			clientAccountDAO.create(clientAccount);
			UserRole userRole = new UserRole();
			userRole.setUser(user);
			userRole.setRole(Role.client);
			userRoleDAO.create(userRole );
			emailService.sendMail(clientAccount.getEmail(), "SutdBank registration", "Thank you for the registration!");
			sendMsg(request, "You are successfully registered...");
			redirect(response, ServletPaths.WELCOME);
		} catch (ServiceException e) {
			sendError(request, e.getMessage());
			forward(request, response);
		} catch(IOException e1) {
			sendError(request, "Invalid Input.");
			forward(request, response);
		}
	}
}
