
public class BankTest {
    public static void main(String[] args) {
        Bank bank = new Bank();

        Account account1 = new Account("1001", "1234", "Rahul", 10000);
        Account account2 = new Account("1002", "5678", "Amit", 5000);

        bank.addAccount(account1);
        bank.addAccount(account2);

        System.out.println("Before Transfer:");
        System.out.println("Account 1001: " + account1.getBalance());
        System.out.println("Account 1002: " + account2.getBalance());

        boolean result = bank.transfer("1001", "1002", 2000);

        System.out.println("\nTransfer Successful: " + result);
        System.out.println("After Transfer:");
        System.out.println("Account 1001: " + account1.getBalance());
        System.out.println("Account 1002: " + account2.getBalance());

        boolean invalidTransfer = bank.transfer("1001", "9999", 1000);
        System.out.println("\nTransfer to Missing Account: " + invalidTransfer);

        boolean insufficientTransfer = bank.transfer("1001", "1002", 50000);
        System.out.println("Transfer with Insufficient Funds: " + insufficientTransfer);

        System.out.println("Final Account 1001: " + account1.getBalance());
        System.out.println("Final Account 1002: " + account2.getBalance());
    }
}
