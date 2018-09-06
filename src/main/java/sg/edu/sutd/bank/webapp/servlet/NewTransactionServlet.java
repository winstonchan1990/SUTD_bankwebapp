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

import static sg.edu.sutd.bank.webapp.servlet.ServletPaths.NEW_TRANSACTION;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import sg.edu.sutd.bank.webapp.commons.ServiceException;
import sg.edu.sutd.bank.webapp.model.ClientAccount;
import sg.edu.sutd.bank.webapp.model.ClientTransaction;
import sg.edu.sutd.bank.webapp.model.TransactionStatus;
import sg.edu.sutd.bank.webapp.model.User;
import sg.edu.sutd.bank.webapp.service.ClientAccountDAO;
import sg.edu.sutd.bank.webapp.service.ClientAccountDAOImpl;
import sg.edu.sutd.bank.webapp.service.ClientInfoDAO;
import sg.edu.sutd.bank.webapp.service.ClientInfoDAOImpl;
import sg.edu.sutd.bank.webapp.service.ClientTransactionDAO;
import sg.edu.sutd.bank.webapp.service.ClientTransactionDAOImpl;
import sg.edu.sutd.bank.webapp.service.TransactionCodesDAO;
import sg.edu.sutd.bank.webapp.service.TransactionCodesDAOImp;
import sg.edu.sutd.bank.webapp.service.AccountBalanceLock;

@WebServlet(NEW_TRANSACTION)
@MultipartConfig
public class NewTransactionServlet extends DefaultServlet {
	private static final long serialVersionUID = 1L;
	private ClientAccountDAO clientAccountDAO = new ClientAccountDAOImpl();
	private TransactionCodesDAO transactionCodesDAO = new TransactionCodesDAOImp();
	private ClientTransactionDAO clientTransactionDAO = new ClientTransactionDAOImpl();
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		AccountBalanceLock.getInstance().lock();
		try {
			ClientTransaction clientTransaction = new ClientTransaction();
			User user = new User(getUserId(req));
			clientTransaction.setUser(user);
			String transactionMode = req.getParameter("txnType");
			
			HttpSession session = req.getSession();
			String storedToken = (String)session.getAttribute("csrfToken");
			String token = req.getParameter("token");
			
			if ((transactionMode != null) && (storedToken.equals(token))) {
				if (transactionMode.equals("single")){
					Integer amt = Integer.parseInt(req.getParameter("amount"));
					if (amt < 0) {
						sendError(req, "Invalid transaction amount.");
						forward(req, resp);
						return;
					}
					
					clientTransaction.setAmount(new BigDecimal(req.getParameter("amount")));
					clientTransaction.setTransCode(req.getParameter("transcode"));
					clientTransaction.setToAccountNum(req.getParameter("toAccountNum"));
					
					// check if transaction code is valid
					if (!transactionCodesDAO.isTransationCodeUsed(clientTransaction.getTransCode(), clientTransaction.getUser().getId())) {
						// checks if balance are sufficient
						ClientAccount clientAccount = new ClientAccount();
						clientAccount.setUser(clientTransaction.getUser());
						
						double oldBalance = clientAccountDAO.getAccountBalance(clientAccount);
						double txnAmt = clientTransaction.getAmount().doubleValue();
						
						if(txnAmt <= oldBalance) {
							// consume transaction code
							transactionCodesDAO.updateTxnCodeStatus(clientTransaction.getTransCode(), clientTransaction.getUser().getId(), true);
							
							// generate transaction
							clientTransactionDAO.create(clientTransaction);
							
							// auto-approved txn amount is 10.000 or lesser
							if(clientTransaction.getAmount().doubleValue() <= Double.valueOf(10.000)) {
								// update status and balance
								BigDecimal newBalance = new BigDecimal(oldBalance-txnAmt);
								
								clientTransaction.setStatus(TransactionStatus.APPROVED);
								clientTransactionDAO.updateDecision(clientTransaction);
								
								clientAccount.setAmount(newBalance);
								clientAccountDAO.update(clientAccount);
							}
							
							redirect(resp, ServletPaths.CLIENT_DASHBOARD_PAGE);
						} else {
							sendError(req, "Insufficent balance in client account.");
							forward(req, resp);
						}
					} else {
						sendError(req, "Invalid Transaction Code.");
						forward(req, resp);
					}
				}
				else {
					Part filePart = req.getPart("fileSelect");
					InputStream filecontent = filePart.getInputStream();
					BufferedReader reader = new BufferedReader (new InputStreamReader(filecontent, "UTF-8"));
					String line = "";
					String separtor = ",";
					boolean isError = false;
					int total_count = 0;
					int fault_count = 0;
					
					while((line = reader.readLine()) != null) {
						String[] fields = line.split(separtor);
						
						try {
							if(fields.length == 3) {
								clientTransaction.setAmount(new BigDecimal(fields[2]));
								clientTransaction.setTransCode(fields[0]);
								clientTransaction.setToAccountNum(fields[1]);
								
								// check if transaction code is valid
								if (!transactionCodesDAO.isTransationCodeUsed(clientTransaction.getTransCode(), clientTransaction.getUser().getId())) {
									// consume transaction code
									transactionCodesDAO.updateTxnCodeStatus(clientTransaction.getTransCode(), clientTransaction.getUser().getId(), true);
									
									// generate transaction
									clientTransactionDAO.create(clientTransaction);
									
									// auto-approved txn amount is 10.000 or lesser
									if(clientTransaction.getAmount().doubleValue() <= Double.valueOf(10.000)) {
										// check balance and deduct
										ClientAccount clientAccount = new ClientAccount();
										clientAccount.setUser(clientTransaction.getUser());
										
										double oldBalance = clientAccountDAO.getAccountBalance(clientAccount);
										double txnAmt = clientTransaction.getAmount().doubleValue();
										
										if(txnAmt < oldBalance && txnAmt >= 0) {
											BigDecimal newBalance = new BigDecimal(oldBalance-txnAmt);
											
											clientAccount.setAmount(newBalance);
											clientTransaction.setStatus(TransactionStatus.APPROVED);
											
											clientTransactionDAO.updateDecision(clientTransaction);
											clientAccountDAO.update(clientAccount);
										} else {
											clientTransaction.setStatus(TransactionStatus.DECLINED);
											clientTransactionDAO.updateDecision(clientTransaction);
										}
									} 
								}
								else {
									// Invalid transaction code
									isError = true;
									fault_count++;
								}
							}
							else {
								// incorrect csv file data format
								isError = true;
								fault_count++;
							}
						} catch (Exception e) {
							// exception due to incorrect field format
							isError = true;
							fault_count++;
						}
						total_count++;
					}
					
					if(isError == true) {
						String errorMsg = "Batch Mode: " + fault_count + " in total number of transaction(" + total_count + ") failed.";
						sendError(req, errorMsg);
						forward(req, resp);
					}
					else {
						redirect(resp, ServletPaths.CLIENT_DASHBOARD_PAGE);
					}
				}
			}
			else {
				sendError(req, "Error processing request.");
				forward(req, resp);
			}
			
		} catch (ServiceException e) {
			if (e.getMessage().contains("Invalid Transaction Code.")) {
				sendError(req, "Error processing request.");
				forward(req, resp);
			}
			else if (e.getMessage().contains("Insufficent balance in client account.")) {
				sendError(req, "Insufficent balance in client account.");
			}
			else {
				sendError(req, e.getMessage());
				forward(req, resp);
			}
		}
		
		AccountBalanceLock.getInstance().unlock();
	}
}
