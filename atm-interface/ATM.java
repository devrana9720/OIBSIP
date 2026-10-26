
import java.util.ArrayList;
import java.util.Scanner;

public class ATM {

    private static final long LOCKOUT_DURATION = 15 * 60 * 1000L;

    private final Bank bank;
    private final Scanner scanner;
    private final ArrayList<Transaction> transactions;

    private Account currentAccount;
    private long lockoutUntil = 0;

    public ATM(Bank bank) {
        this.bank = bank;
        this.scanner = new Scanner(System.in);
        this.transactions = new ArrayList<>();
        this.currentAccount = null;
    }

    // ==================== AUTHENTICATION ====================

    public boolean authenticate() {

        if (System.currentTimeMillis() < lockoutUntil) {
            long remaining =
                    (lockoutUntil - System.currentTimeMillis() + 999) / 1000;

            System.out.println(
                    "ATM locked. Try again in " + remaining + " seconds."
            );
            return false;
        }

        System.out.println("\n========== ATM LOGIN ==========");
        System.out.print("Enter User ID: ");

        String userId = scanner.nextLine().trim();

        Account account = bank.findAccount(userId);

        if (account == null) {
            System.out.println("Invalid User ID.");
            return false;
        }

        for (int attempt = 1; attempt <= 3; attempt++) {

            System.out.print("Enter PIN: ");
            String enteredPin = scanner.nextLine();

            if (account.validatePin(enteredPin)) {
                currentAccount = account;

                System.out.println(
                        "Login successful. Welcome, "
                                + currentAccount.getHolderName() + "!"
                );

                return true;
            }

            int remaining = 3 - attempt;

            if (remaining > 0) {
                System.out.println(
                        "Incorrect PIN. Attempts remaining: " + remaining
                );
            } else {
                lockoutUntil =
                        System.currentTimeMillis() + LOCKOUT_DURATION;

                currentAccount = null;

                System.out.println(
                        "Access Denied: Maximum PIN attempts exceeded. "
                                + "Please try again after 15 minutes."
                );

                return false;
            }
        }

        return false;
    }

    // ==================== START ATM ====================

    public void start() {
        if (!authenticate()) {
            return;
        }

        showMenu();
    }

    // ==================== MAIN MENU ====================

    private void showMenu() {

        while (currentAccount != null) {

            System.out.println("\n========== ATM MAIN MENU ==========");
            System.out.println("1. Transaction History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Quit");
            System.out.println("===================================");

            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    showTransactionHistory();
                    break;

                case "2":
                    withdraw();
                    break;

                case "3":
                    deposit();
                    break;

                case "4":
                    transfer();
                    break;

                case "5":
                    System.out.println(
                            "Thank you for using the ATM. Goodbye!"
                    );

                    currentAccount = null;
                    break;

                default:
                    System.out.println(
                            "Invalid choice. Please select 1-5."
                    );
            }
        }
    }

    // ==================== TRANSACTION HISTORY ====================

    private void showTransactionHistory() {

        System.out.println("\n========== TRANSACTION HISTORY ==========");

        boolean found = false;

        String accountId = currentAccount.getAccountId();

        for (Transaction transaction : transactions) {

            boolean belongsToCurrentAccount =
                    accountId.equals(transaction.getFromAccountId())
                            || accountId.equals(transaction.getToAccountId());

            if (belongsToCurrentAccount) {

                System.out.println("------------------------------------------");
                transaction.displayDetails();

                found = true;
            }
        }

        if (!found) {
            System.out.println("No transactions found.");
        }

        System.out.println("==========================================");
    }

    // ==================== WITHDRAW ====================

    private void withdraw() {

        System.out.println("\n========== CASH WITHDRAWAL ==========");

        System.out.print("Enter withdrawal amount: ");

        String input = scanner.nextLine().trim();

        double amount;

        try {
            amount = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a number.");
            return;
        }

        if (!Double.isFinite(amount) || amount <= 0) {
            System.out.println("Amount must be greater than zero.");
            return;
        }

        if (amount > currentAccount.getBalance()) {
            System.out.println("Insufficient Funds");
            return;
        }

        boolean successful = currentAccount.withdraw(amount);

        if (successful) {

            transactions.add(
                    new Transaction(
                            "Withdrawal",
                            amount,
                            currentAccount.getAccountId(),
                            "N/A"
                    )
            );

            System.out.println("Withdrawal successful.");
            System.out.println("Withdrawn Amount: " + amount);
            System.out.println(
                    "Remaining Balance: " + currentAccount.getBalance()
            );

        } else {
            System.out.println("Withdrawal failed.");
        }
    }

    // ==================== DEPOSIT ====================

    private void deposit() {

        System.out.println("\n========== CASH DEPOSIT ==========");

        System.out.print("Enter deposit amount: ");

        String input = scanner.nextLine().trim();

        double amount;

        try {
            amount = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a number.");
            return;
        }

        if (!Double.isFinite(amount) || amount <= 0) {
            System.out.println("Amount must be greater than zero.");
            return;
        }

        boolean successful = currentAccount.deposit(amount);

        if (successful) {

            transactions.add(
                    new Transaction(
                            "Deposit",
                            amount,
                            currentAccount.getAccountId(),
                            currentAccount.getAccountId()
                    )
            );

            System.out.println("Deposit successful.");
            System.out.println("Deposited Amount: " + amount);
            System.out.println(
                    "Updated Balance: " + currentAccount.getBalance()
            );

        } else {
            System.out.println("Deposit failed.");
        }
    }

    // ==================== TRANSFER ====================

    private void transfer() {

        System.out.println("\n========== MONEY TRANSFER ==========");

        System.out.print("Enter receiver's User ID: ");

        String receiverId = scanner.nextLine().trim();

        Account receiver = bank.findAccount(receiverId);

        if (receiver == null) {
            System.out.println("Receiver account not found.");
            return;
        }

        if (receiverId.equals(currentAccount.getAccountId())) {
            System.out.println(
                    "You cannot transfer money to your own account."
            );
            return;
        }

        System.out.print("Enter transfer amount: ");

        String input = scanner.nextLine().trim();

        double amount;

        try {
            amount = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a number.");
            return;
        }

        if (!Double.isFinite(amount) || amount <= 0) {
            System.out.println("Amount must be greater than zero.");
            return;
        }

        if (amount > currentAccount.getBalance()) {
            System.out.println("Insufficient Funds");
            return;
        }

        boolean successful = bank.transfer(
                currentAccount.getAccountId(),
                receiverId,
                amount
        );

        if (successful) {

            transactions.add(
                    new Transaction(
                            "Transfer",
                            amount,
                            currentAccount.getAccountId(),
                            receiverId
                    )
            );

            System.out.println("Transfer successful.");
            System.out.println("Transferred Amount: " + amount);
            System.out.println(
                    "Receiver: " + receiver.getHolderName()
            );
            System.out.println(
                    "Your Updated Balance: " + currentAccount.getBalance()
            );

        } else {
            System.out.println(
                    "Transfer failed. Please check the account details "
                            + "and available balance."
            );
        }
    }
}
