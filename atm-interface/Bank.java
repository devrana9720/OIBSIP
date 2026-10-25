
import java.util.ArrayList;

public class Bank {
    private ArrayList<Account> accounts;

    public Bank() {
        accounts = new ArrayList<>();
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public Account findAccount(String accountId) {
        for (Account account : accounts) {
            if (account.getAccountId().equals(accountId)) {
                return account;
            }
        }

        return null;
    }

    public boolean transfer(String senderId, String receiverId, double amount) {
        if (amount <= 0 || !Double.isFinite(amount)) {
            return false;
        }

        Account sender = findAccount(senderId);
        Account receiver = findAccount(receiverId);

        if (sender == null || receiver == null) {
            return false;
        }

        if (senderId.equals(receiverId)) {
            return false;
        }

        if (sender.getBalance() < amount) {
            return false;
        }

        if (sender.withdraw(amount)) {
            boolean deposited = receiver.deposit(amount);

            if (!deposited) {
                sender.deposit(amount);
                return false;
            }

            return true;
        }

        return false;
    }

    public ArrayList<Account> getAccounts() {
        return new ArrayList<>(accounts);
    }
}
