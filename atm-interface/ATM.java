
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

    public boolean authenticate() {
        if (System.currentTimeMillis() < lockoutUntil) {
            long remaining =
                    (lockoutUntil - System.currentTimeMillis() + 999) / 1000;

            System.out.println("ATM locked. Try again in "
                    + remaining + " seconds.");
            return false;
        }

        System.out.println("===== ATM LOGIN =====");
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
                System.out.println("Login successful. Welcome, "
                        + account.getHolderName() + "!");
                return true;
            }

            int remaining = 3 - attempt;

            if (remaining > 0) {
                System.out.println("Incorrect PIN. Attempts remaining: "
                        + remaining);
            } else {
                lockoutUntil =
                        System.currentTimeMillis() + LOCKOUT_DURATION;
                currentAccount = null;

                System.out.println(
                        "Access Denied: Maximum PIN attempts exceeded. "
                        + "Please try again after 15 minutes.");

                return false;
            }
        }

        return false;
    }

    public void start() {
        if (!authenticate()) {
            return;
        }

        showMenu();
    }

    private void showMenu() {
        while (currentAccount != null) {
            System.out.println("\n===== ATM MAIN MENU =====");
            System.out.println("1. Transaction History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Quit");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("Transaction History selected.");
                    break;

                case "2":
                    System.out.println("Withdraw selected.");
                    break;

                case "3":
                    System.out.println("Deposit selected.");
                    break;

                case "4":
                    System.out.println("Transfer selected.");
                    break;

                case "5":
                    System.out.println("Thank you for using the ATM. Goodbye!");
                    currentAccount = null;
                    break;

                default:
                    System.out.println("Invalid choice. Please select 1-5.");
            }
        }
    }
}
