
public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();

        bank.addAccount(
                new Account("1001", "1234", "Rahul", 10000)
        );

        bank.addAccount(
                new Account("1002", "5678", "Amit", 5000)
        );

        ATM atm = new ATM(bank);
        atm.start();
    }
}
