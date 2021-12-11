package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;
import com.techelevator.view.ConsoleService;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };

    private static final String toPrefix = "To: ";
    private static final String fromPrefix = "From: ";
    private Map<Long, String> mapUser;

    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private AccountService accountService;
    private TransferService transferService;
    private UserService userService = new UserService();

    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL), new AccountService(API_BASE_URL), new TransferService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService, AccountService accountService, TransferService transferService) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.accountService = accountService;
		this.transferService = transferService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
		// TODO Auto-generated method stub
        //Long accountId = Long.parseLong(console.getUserInput("Enter Account ID"));
        try {
			System.out.println("Your current account balance is: $" + accountService.getAccount(Long.valueOf(currentUser.getUser().getId())).getBalance());
		} catch (AccountServiceException e) {
			System.out.println("Error accessing account: " + e.getMessage());
		}
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
        Transfer[] transfers = null;
		Map<Long, Transfer> mapTransfer = new HashMap<>();

        try {
			//Long userAccountId = accountService.getAccount(Long.valueOf(currentUser.getUser().getId())).getAccountId();
            Long userAccountId = getAccountIdByUserIdFromAccounts(Long.valueOf(currentUser.getUser().getId()));
            transfers = transferService.getTransfersByUserId(Long.valueOf(currentUser.getUser().getId()));
            Arrays.sort(transfers, new Comparator<Transfer>() {
				@Override
				public int compare(Transfer o1, Transfer o2) {
					if (o1.getTransferId() != o2.getTransferId()) {
						return o1.getTransferId().intValue() - o2.getTransferId().intValue();
					}
					return o1.getTransferId().compareTo(o2.getTransferId());
				}
			});
            console.displayHeader("Transfers", "ID          From/To                 Amount");
			for (Transfer transfer : transfers) {
				String detail = null;
                Account account = null;
				if (transfer.getAccountFrom() != userAccountId) {
					detail = fromPrefix;
					account = getAccountDetails(Long.valueOf(transfer.getAccountFrom()));
				} else {
					detail = toPrefix;
					account = getAccountDetails(Long.valueOf(transfer.getAccountTo()));
				}
				//get username from existing users map
				if (mapUser.containsKey(account.getUserId())) {
					detail += mapUser.get(account.getUserId());
				}
				console.printTransferView(transfer, detail);

				//utilize the foreach, create hashmap here for faster search.
				mapTransfer.put(transfer.getTransferId(), transfer);
			}

        } catch (TransferServiceException e) {
            System.out.println("Error accessing transfers: " + e.getMessage());
        }

        TransferDetail transferDetail = new TransferDetail();
		long transferIdChoice = 0;
		System.out.println("---------");
		do {
			transferIdChoice = Long.valueOf(console.getUserInputInteger("Please enter transfer ID to view details (0 to cancel)"));
			if (mapTransfer.containsKey(transferIdChoice)) {
				Account account = null;
				transferDetail.setTransferId(mapTransfer.get(transferIdChoice).getTransferId());
				transferDetail.setTransferStatus(mapTransfer.get(transferIdChoice).getTransferStatusDesc());
				transferDetail.setTransferType(mapTransfer.get(transferIdChoice).getTransferTypeDesc());
				account = getAccountDetails(Long.valueOf(mapTransfer.get(transferIdChoice).getAccountFrom()));
				transferDetail.setFromName(mapUser.get(account.getUserId()));
				account = getAccountDetails(Long.valueOf(mapTransfer.get(transferIdChoice).getAccountTo()));
				transferDetail.setToName(mapUser.get(account.getUserId()));
				transferDetail.setAmount(mapTransfer.get(transferIdChoice).getAmount());
				console.printTransferDetails(transferDetail);
				break;
			} else {
				System.out.println("*** Transfer ID not found ***");
			}
		} while (transferIdChoice != 0);
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub

	}

	private void sendBucks() {
		// TODO Auto-generated method stub

		boolean isValid = false;
		long sendToUserId = 0;
		double amount = 0.00;
		Long accountId = null;
		Account senderAccount = null;
		Account receiverAccount = null;

		//get sender's Account details
		accountId = getAccountIdByUserIdFromAccounts(Long.valueOf(currentUser.getUser().getId()));
		senderAccount = getAccountDetails(accountId);

		//print all registered users
		//make sure to skip printing current user
		console.printRegisteredUsers(mapUser, Long.valueOf(currentUser.getUser().getId()));

		do {
			sendToUserId = Long.valueOf(console.getUserInputInteger("Enter ID of user you are sending to (0 to cancel)"));
			if (mapUser.containsKey(sendToUserId) && sendToUserId !=0) {
				amount = console.getUserInputDouble("Enter amount");
				if (senderAccount.getBalance() >= amount) {
					accountId = getAccountIdByUserIdFromAccounts(sendToUserId);
					receiverAccount = getAccountDetails(accountId);
					isValid = true;
					break;
				} else {
					System.out.println("*** Balance not enough for this transaction ***");
				}
			}
		} while (sendToUserId != 0);

        //deduct money from own account
		//add money to recipient account
        //create record in transfer table with initial status of "approve"
		if (isValid) {
			try {
				senderAccount.deductBalance(amount);
				accountService.updateAccount(senderAccount);
				receiverAccount.increaseBalance(amount);
				accountService.updateAccount(receiverAccount);
				//populate fields for new transfer
                Transfer newtransfer = new Transfer();
				newtransfer.setTransferTypeDesc("Send");
				newtransfer.setTransferStatusDesc("Approved");
				newtransfer.setAccountFrom(Integer.valueOf(String.valueOf(senderAccount.getAccountId())));
				newtransfer.setAccountTo(Integer.valueOf(String.valueOf(receiverAccount.getAccountId())));
				newtransfer.setAmount(amount);
                transferService.createTransfer(newtransfer);
			} catch (AccountServiceException e) {
				System.out.println("Error updating account service for sending: " + e.getMessage());
			} catch (TransferServiceException e) {
				System.out.println("Error updating transfer service for sending: " + e.getMessage());
			}
		}
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
				accountService.setAuthToken(currentUser.getToken());
				transferService.setAuthToken(currentUser.getToken());
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}

		User[] users = userService.getUserName(API_BASE_URL);
		getAllUsersForReference(users);
	}

	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}

	private void printTransfers(Transfer[] transfers) {

    	for (Transfer transfer : transfers) {
			System.out.println(transfer.getTransferId() + "     ");
			System.out.println(transfer.getAmount());
		}
	}

	private void getAllUsersForReference(User[] users) {

		mapUser  = new HashMap<>();
    	for (User user: users) {
			mapUser.put(Long.valueOf(user.getId()), user.getUsername());
		}
	}

	private Account getAccountDetails(Long accountId) {

    	Account account = null;

    	try {
			account = accountService.getAccountByAccountId(accountId);
		} catch (AccountServiceException e) {
			System.out.println("Error accessing Account To: " + e.getMessage());
		}

    	return account;
	}

	private Long getAccountIdByUserIdFromAccounts(Long userId) {

        Long userAccountId = null;

        try {
            userAccountId = accountService.getAccount(userId).getAccountId();
        } catch (AccountServiceException e) {
            System.out.println("Error retrieving Account Id from Accounts: " + e.getMessage());
        }

        return userAccountId;
    }
}
