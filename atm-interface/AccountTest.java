public class AccountTest {
    public static void main(String[] args) {
        Account account = new Account("1001", "1234", "Rahul", 10000);

        System.out.println("Account ID: " + account.getAccountId());
        System.out.println("Holder Name: " + account.getHolderName());
        System.out.println("Initial Balance: ₹" + account.getBalance());

        System.out.println("Correct PIN: " + account.validatePin("1234"));
        System.out.println("Wrong PIN: " + account.validatePin("9999"));

        boolean depositResult = account.deposit(2000);
        System.out.println("Deposit Successful: " + depositResult);
        System.out.println("Balance After Deposit: ₹" + account.getBalance());

        boolean withdrawResult = account.withdraw(1000);
        System.out.println("Withdrawal Successful: " + withdrawResult);
        System.out.println("Balance After Withdrawal: ₹" + account.getBalance());

        boolean largeWithdrawal = account.withdraw(50000);
        System.out.println("Large Withdrawal Successful: " + largeWithdrawal);
        System.out.println("Final Balance: ₹" + account.getBalance());
    }
}
