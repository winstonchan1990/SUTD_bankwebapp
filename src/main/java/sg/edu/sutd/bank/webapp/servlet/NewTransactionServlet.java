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
import sg.edu.sutd.bank.webapp.commons.XSSFinder;
import sg.edu.sutd.bank.webapp.model.ClientAccount;
import sg.edu.sutd.bank.webapp.model.ClientTransaction;
import sg.edu.sutd.bank.webapp.model.TransactionStatus;
import sg.edu.sutd.bank.webapp.model.User;
import sg.edu.sutd.bank.webapp.service.ClientAccountDAO;
import sg.edu.sutd.bank.webapp.service.ClientAccountDAOImpl;
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
				
				
				/// SINGLE
				if (transactionMode.equals("single")){
					
					// check for potential XSS attack
					String txnCode = XSSFinder.check_string(req.getParameter("transcode"));
					
					clientTransaction.setAmount(new BigDecimal(req.getParameter("amount")));
					clientTransaction.setTransCode(txnCode);
					clientTransaction.setToAccountNum(req.getParameter("toAccountNum"));
					clientTransaction.setStatus(null);
					
					Boolean IsPositiveValue = clientTransactionDAO.isPositiveValue(clientTransaction);
					
					Boolean IsValidTransactionCode = !(transactionCodesDAO.isTransationCodeUsed(
						clientTransaction.getTransCode(), 
						clientTransaction.getUser().getId()
					));
					
					Boolean HasSufficientBalance = clientTransactionDAO.hasSufficientBalance(clientTransaction);
					
					
					if (!IsPositiveValue) {
						sendError(req, "Invalid transaction amount.");
						forward(req, resp);
						return;
					}
					
					if(!IsValidTransactionCode) {
						sendError(req, "Invalid Transaction Code.");
						forward(req, resp);
					}
					
					if(!HasSufficientBalance) {
						sendError(req, "Insufficent balance in client account.");
						forward(req, resp);
						return;
					}
					
					
					ClientAccount clientAccount = new ClientAccount();
					clientAccount.setUser(clientTransaction.getUser());
						
					double oldBalance = clientAccountDAO.getAccountBalance(clientAccount);
					double txnAmt = clientTransaction.getAmount().doubleValue();
						
					transactionCodesDAO.updateTxnCodeStatus(
						clientTransaction.getTransCode(), 
						clientTransaction.getUser().getId(), 
						true
					);
							
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
		
				}
				else {
					
					/// BATCH PROCESSING
					
					Part filePart = req.getPart("fileSelect");
					InputStream filecontent = filePart.getInputStream();
					BufferedReader reader = new BufferedReader (new InputStreamReader(filecontent, "UTF-8"));
					String line = "";
					String separtor = ",";
					boolean isError = false;
					int total_count = 0;
					int error_ispositivevalue_count = 0;
					int error_isvalidtxncode_count = 0;
					int error_sufficientbalance_count = 0;
					int error_incorrectnumfields_count = 0;
					int error_format_count = 0;
					
					ClientAccount clientAccount = new ClientAccount();
					clientAccount.setUser(user);
					
					double availableBalance = clientAccountDAO.getAccountBalance(clientAccount);
					
					
					while((line = reader.readLine()) != null) {
						String[] fields = line.split(separtor);
						
						try {
							if(fields.length != 3) {
								isError = true;
								error_incorrectnumfields_count++;
								continue;
							}
							
							// check for potential XSS attack
							String txnCode = XSSFinder.check_string(fields[0]);
							

							clientTransaction.setAmount(new BigDecimal(fields[2]));
							clientTransaction.setTransCode(txnCode);
							clientTransaction.setToAccountNum(fields[1]);
							clientTransaction.setStatus(null);
							double txnAmt = clientTransaction.getAmount().doubleValue();
								
							Boolean IsPositiveValue = clientTransactionDAO.isPositiveValue(clientTransaction);
								
							Boolean IsValidTransactionCode = !(transactionCodesDAO.isTransationCodeUsed(
								clientTransaction.getTransCode(), 
								clientTransaction.getUser().getId()
							));
								
							Boolean HasSufficientBalance = (
								clientTransactionDAO.hasSufficientBalance(clientTransaction) &&
								(availableBalance >= clientTransaction.getAmount().doubleValue())
							);
								
							if (!IsPositiveValue) {
								isError = true;
								error_ispositivevalue_count ++;
								continue;
							}
								
							if(!IsValidTransactionCode) {
								isError = true;
								error_isvalidtxncode_count ++;
								continue;
							}
								
							if(!HasSufficientBalance) {
								isError = true;
								error_sufficientbalance_count ++;
								continue;
							}
								
											
							// consume transaction code
							transactionCodesDAO.updateTxnCodeStatus(
								clientTransaction.getTransCode(), 
								clientTransaction.getUser().getId(), 
								true
							);
									
							// generate transaction
							clientTransactionDAO.create(clientTransaction);
									
							// auto-approved txn amount is 10.000 or lesser
							if(clientTransaction.getAmount().doubleValue() <= Double.valueOf(10.000)) {
										
								double oldBalance = clientAccountDAO.getAccountBalance(clientAccount);								
								BigDecimal newBalance = new BigDecimal(oldBalance-txnAmt);
									
								clientAccount.setAmount(newBalance);
								clientTransaction.setStatus(TransactionStatus.APPROVED);
								
								clientTransactionDAO.updateDecision(clientTransaction);
								clientAccountDAO.update(clientAccount); 
							}
							
							// update available balance 
							availableBalance -= txnAmt;
								 
						} catch (Exception e) {
							// exception due to incorrect field format
							isError = true;
							error_format_count++;
						}
						
						total_count++;
						
					}
					
					if(isError == true) {
						String errorMsg = "Batch Mode: " + 
							total_count + " transactions in total \n" + 
							error_format_count + " transactions with wrong format\n" + 
							error_incorrectnumfields_count + " transactions with incorrect no. of fields\n" + 
							error_ispositivevalue_count +" transactions with zero / negative txn values\n" +
							error_isvalidtxncode_count + " transactions with invalid txn codes\n" +
							error_sufficientbalance_count + " transactions rejected due to insufficient balance\n";
						
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
