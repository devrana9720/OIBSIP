
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String type;
    private double amount;
    private String fromAccountId;
    private String toAccountId;
    private LocalDateTime dateTime;

    public Transaction(String type, double amount,
                       String fromAccountId, String toAccountId) {
        this.type = type;
        this.amount = amount;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.dateTime = LocalDateTime.now();
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getFromAccountId() {
        return fromAccountId;
    }

    public String getToAccountId() {
        return toAccountId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void displayDetails() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        System.out.println("Transaction Type: " + type);
        System.out.println("Amount: " + amount);
        System.out.println("From Account: " + fromAccountId);
        System.out.println("To Account: " + toAccountId);
        System.out.println("Date and Time: " + dateTime.format(formatter));
    }
}
