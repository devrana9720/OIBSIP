# 🏧 ATM Interface — Java Console Application

A console-based ATM simulation developed using Java as part of the OIBSIP Internship. The application demonstrates account authentication, deposits, withdrawals, money transfers, and transaction history using object-oriented programming.

## 📌 Project Overview

The ATM Interface is a command-line application that simulates basic ATM operations. It uses Java classes to manage accounts, validate PINs, process transactions, and maintain account balances during application execution.

**Note:** This is an educational simulation. It does not connect to real banking services, dispense cash, or process actual financial transactions.

## ✨ Features

- **PIN Authentication:** Log in using a configured account ID and PIN.
- **PIN Attempt Limit:** Denies access after three incorrect PIN attempts and displays the specified 15-minute access-denied message.
- **Account Management:** Maintain account information and balances.
- **Cash Withdrawal:** Withdraw a valid amount when sufficient funds are available.
- **Cash Deposit:** Deposit a valid amount into the logged-in account.
- **Money Transfer:** Transfer funds between configured accounts.
- **Transaction History:** View successful transactions recorded during application execution.
- **Input Validation:** Reject invalid, non-numeric, zero, negative, and non-finite transaction amounts.
- **Insufficient Funds Handling:** Display `Insufficient Funds` when a withdrawal or transfer exceeds the available balance.
- **Console Menu:** Access the available operations using a numbered menu.

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Java Collections | Store accounts and transactions |
| Scanner | Read console input |
| LocalDateTime | Record transaction timestamps |
| Git | Version control |
| GitHub | Source code hosting |
| Visual Studio Code | Development environment |

## 📂 Project Structure

```text
atm-interface/
├── Account.java
├── AccountTest.java
├── ATM.java
├── ATMTest.java
├── Bank.java
├── BankTest.java
├── Main.java
├── Transaction.java
├── TransactionTest.java
└── README.md
```

### File Responsibilities

- **`Main.java`** — Entry point of the application. Creates the bank, initializes the sample accounts, and starts the ATM.
- **`ATM.java`** — Handles user authentication, the main menu, deposits, withdrawals, transfers, and transaction history.
- **`Account.java`** — Stores account information, validates PINs, and manages deposits and withdrawals.
- **`Bank.java`** — Stores configured accounts, searches for accounts, and performs money transfers.
- **`Transaction.java`** — Represents a transaction with its type, amount, account details, and date and time.
- **`AccountTest.java`** — Tests account-related operations.
- **`TransactionTest.java`** — Tests transaction creation and display.
- **`BankTest.java`** — Tests account lookup and transfers.
- **`ATMTest.java`** — Contains ATM-related test code.

## ⚙️ Prerequisites

Install the following software:

- JDK 21
- Visual Studio Code or another Java editor
- PowerShell or another terminal
- Git (optional, for cloning the repository)

Verify your Java installation:

```powershell
java -version
javac -version
```

## 🚀 Build and Run

### 1. Clone the repository

```bash
git clone https://github.com/devrana9720/OIBSIP.git
```

### 2. Navigate to the ATM project

```powershell
cd OIBSIP/atm-interface
```

If you already have the repository, navigate to its existing `atm-interface` folder.

### 3. Compile the Java files

```powershell
javac *.java
```

### 4. Run the application

```powershell
java Main
```

This project uses plain Java source files. Maven and a database are not required.

## 🔑 Sample Login Credentials

The sample accounts configured in `Main.java` are:

| Account ID | PIN | Initial Balance |
|---|---|---:|
| `1001` | `1234` | ₹10,000 |
| `1002` | `5678` | ₹5,000 |

These credentials are for the educational simulation only.

## 🔄 Application Workflow

1. Launch the application.
2. Enter a configured account ID.
3. Enter the account PIN.
4. Select an operation from the ATM menu.
5. Enter the required amount or recipient account.
6. Review the operation result and updated balance.
7. View transactions using Transaction History.
8. Select Quit to end the ATM session.

## 🧾 Available Operations

| Menu Option | Operation |
|---|---|
| 1 | Transaction History |
| 2 | Withdraw |
| 3 | Deposit |
| 4 | Transfer |
| 5 | Quit |

## 🧪 Testing

Compile the project before testing:

```powershell
javac *.java
```

Run the application and verify these scenarios:

- Login with correct and incorrect credentials.
- Three consecutive incorrect PIN attempts.
- Successful and invalid deposits.
- Successful withdrawals and insufficient-funds cases.
- Successful transfers between accounts.
- Transfers to missing or identical accounts.
- Transaction history after successful operations.
- Invalid menu selections and invalid monetary amounts.
- Quit and return from the ATM menu.

The individual test classes can also be run if they contain executable `main` methods.

## ⚠️ Limitations

- Account balances are stored in memory and reset when the application restarts.
- Transaction history is stored in memory and is not persisted to a database or file.
- The PIN lockout timer is stored in memory. In the current application flow, the process exits after failed authentication, so the lockout is not preserved across application restarts.
- Monetary calculations use `double`, which is suitable for a basic educational simulation but not recommended for production financial systems.
- The application does not provide production-grade banking security.

## 🔮 Future Improvements

- Persist accounts and transactions using a database.
- Improve the application lifecycle and lockout enforcement.
- Add more automated tests for authentication and transaction edge cases.
- Improve console input handling and error messages.
- Generate account statements and support transaction filtering.
- Use `BigDecimal` for more appropriate monetary calculations.
- Exclude generated `.class` files from version control.

These are potential improvements, not features currently claimed as implemented.

## 🎯 Learning Outcomes

This project provides practical experience with:

- Java object-oriented programming.
- Classes, objects, encapsulation, and methods.
- Java Collections, including `ArrayList`.
- Conditional statements and loops.
- Input validation and exception handling.
- Account balance management and transaction processing.
- Git and GitHub version control.

## 👨‍💻 Developer

**Dev Rana**

- GitHub Profile: https://github.com/devrana9720
- Internship Repository: https://github.com/devrana9720/OIBSIP

---

**Project:** ATM Interface  
**Language:** Java 21  
**Type:** Console Application  
**Purpose:** OIBSIP Internship educational project