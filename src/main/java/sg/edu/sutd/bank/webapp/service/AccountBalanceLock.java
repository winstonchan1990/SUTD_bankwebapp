package sg.edu.sutd.bank.webapp.service;

import java.util.concurrent.locks.ReentrantLock;

public class AccountBalanceLock {
	
	private static final AccountBalanceLock inst = new AccountBalanceLock();
	private static final ReentrantLock lock_handle = new ReentrantLock();
	
	private AccountBalanceLock() {
		
	}
	
	public static AccountBalanceLock getInstance() {
		return inst;
	}
	
	public void lock() {
		lock_handle.lock();
	}

	public void unlock() {
		lock_handle.unlock();
	}
}
