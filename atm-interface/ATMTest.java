public class ATMTest {
    public static void main(String[] args) {
        Bank bank = new Bank();

        bank.addAccount(
                new Account("1001", "1234", "Rahul", 10000)
        );

        ATM atm = new ATM(bank);

        boolean loggedIn = atm.authenticate();

        System.out.println("Authenticated: " + loggedIn);
    }
}
