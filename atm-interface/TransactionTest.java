public class TransactionTest {
    public static void main(String[] args) {
        Transaction transaction = new Transaction(
                "Deposit", 2000, "1001", "1001"
        );

        transaction.displayDetails();
    }
}
