public class Transaction {
    private Account senderAccount;
    private Account receiverAccount;
    private double amount;
    private String transactionType; // "deposit", "withdrawal", "transfer"
    private String transactionStatus; // "pending", "completed", "failed"
    private String transactionDate;

    // Constructor for a deposit or withdrawal
    public Transaction(Account senderAccount, double amount, String transactionType) {
        this.senderAccount = senderAccount;
        this.amount = amount;
        this.transactionType = transactionType;
        this.transactionStatus = "pending";
        this.transactionDate = java.time.LocalDate.now().toString(); // Current date
    }

    // Constructor for a transfer
    public Transaction(Account senderAccount, Account receiverAccount, double amount, String transactionType) {
        this(senderAccount, amount, transactionType);
        this.receiverAccount = receiverAccount;
    }

    // Getter and Setter methods
    public Account getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(Account senderAccount) {
        this.senderAccount = senderAccount;
    }

    public Account getReceiverAccount() {
        return receiverAccount;
    }

    public void setReceiverAccount(Account receiverAccount) {
        this.receiverAccount = receiverAccount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    // Method to process the transaction
    public void processTransaction() {
        if (transactionType.equalsIgnoreCase("deposit")) {
            senderAccount.deposit(amount);
            transactionStatus = "completed";
        } else if (transactionType.equalsIgnoreCase("withdrawal")) {
            if (senderAccount.getBalance() >= amount) {
                senderAccount.withdraw(amount);
                transactionStatus = "completed";
            } else {
                transactionStatus = "failed";
                System.out.println("Insufficient balance for withdrawal.");
            }
        } else if (transactionType.equalsIgnoreCase("transfer")) {
            if (senderAccount.getBalance() >= amount) {
                senderAccount.withdraw(amount);
                receiverAccount.deposit(amount);
                transactionStatus = "completed";
            } else {
                transactionStatus = "failed";
                System.out.println("Insufficient balance for transfer.");
            }
        } else {
            transactionStatus = "failed";
            System.out.println("Invalid transaction type.");
        }
    }

    // Method to display transaction details
    public void displayTransactionDetails() {
        System.out.println("Transaction Date: " + transactionDate);
        System.out.println("Transaction Type: " + transactionType);
        System.out.println("Transaction Status: " + transactionStatus);
        System.out.println("Amount: $" + amount);
        System.out.println("Sender Account: " + senderAccount.getAccountNumber());
        if (receiverAccount != null) {
            System.out.println("Receiver Account: " + receiverAccount.getAccountNumber());
        }
    }
}