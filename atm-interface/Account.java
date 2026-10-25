public class Account {
    private String accountId;
    private String pin;
    private String holderName;
    private double balance;

    public Account(String accountId, String pin,
                   String holderName, double balance) {
        this.accountId = accountId;
        this.pin = pin;
        this.holderName = holderName;
        this.balance = balance;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getBalance() {
        return balance;
    }

    public boolean validatePin(String enteredPin) {
        return pin.equals(enteredPin);
    }

    public boolean deposit(double amount) {
        if (amount <= 0 || !Double.isFinite(amount)) {
            return false;
        }

        balance += amount;
        return true;
    }

    public boolean withdraw(double amount) {
        if (amount <= 0 || !Double.isFinite(amount)) {
            return false;
        }

        if (amount > balance) {
            return false;
        }

        balance -= amount;
        return true;
    }
}
