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
			Long userAccountId = accountService.getAccount(Long.valueOf(currentUser.getUser().getId())).getAccountId();
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
				//fix this
				//if(transfer.getTransferTypeId() == 1) {
				if (transfer.getAccountFrom() != userAccountId) {
					detail = fromPrefix;
					account = getUserIdFromAccount(Long.valueOf(transfer.getAccountFrom()));
				} else {
					detail = toPrefix;
					account = getUserIdFromAccount(Long.valueOf(transfer.getAccountTo()));
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
        } catch (AccountServiceException e) {
			System.out.println("Error accessing accounts: " + e.getMessage());
		}

        boolean isFound = false;
        TransferDetail transferDetail = new TransferDetail();
        do {
            System.out.println("---------");
			long transferIdChoice = Long.valueOf(console.getUserInputInteger("Please enter transfer ID to view details (0 to cancel)"));
			if (mapTransfer.containsKey(transferIdChoice)) {
				Account account = null;
				transferDetail.setTransferId(mapTransfer.get(transferIdChoice).getTransferId());
				transferDetail.setTransferStatus(mapTransfer.get(transferIdChoice).getTransferStatusDesc());
				transferDetail.setTransferType(mapTransfer.get(transferIdChoice).getTransferTypeDesc());
				account = getUserIdFromAccount(Long.valueOf(mapTransfer.get(transferIdChoice).getAccountFrom()));
				transferDetail.setFromName(mapUser.get(account.getUserId()));
				account = getUserIdFromAccount(Long.valueOf(mapTransfer.get(transferIdChoice).getAccountTo()));
				transferDetail.setToName(mapUser.get(account.getUserId()));
				transferDetail.setAmount(mapTransfer.get(transferIdChoice).getAmount());
				console.printTransferDetails(transferDetail);
				isFound = true;
			}
		} while (!isFound);
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub

	}

	private void sendBucks() {
		// TODO Auto-generated method stub
		console.printRegisteredUsers(mapUser);
		boolean isValid = false;
		long sendToUserId = 0;
		double amount = 0.00;

		do {
			sendToUserId = Long.valueOf(console.getUserInputInteger("Enter ID of user you are sending to (0 to cancel)"));
			if (mapUser.containsKey(sendToUserId)) {
				isValid = true;
			}
		} while (!isValid);

		isValid = false;
		do {
			amount = console.getUserInputDouble("Enter amount");
			isValid = true;
		} while (!isValid);

        //deduct money from own account
        //add money to recipient account
        //create record in transfer table with initial status of "approve"
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

	private Account getUserIdFromAccount(Long accountId) {

    	Account account = null;

    	try {
			account = accountService.getAccountByAccountId(accountId);
		} catch (AccountServiceException e) {
			System.out.println("Error accessing Account To: " + e.getMessage());
		}

    	return account;
	}
}
