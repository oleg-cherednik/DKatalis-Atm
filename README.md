# ATM

## Problem Statement

You are asked to develop a Command Line Interface (CLI) to simulate an interaction of an ATM with a retail bank.

## Commands

* `login [name]` - Logs in as this customer and creates the customer if not exist
* `deposit [amount]` - Deposits this amount to the logged in customer
* `withdraw [amount]` - Withdraws this amount from the logged in customer
* `transfer [target] [amount]` - Transfers this amount from the logged in customer to the target customer
* `logout` - Logs out of the current customer

## Example Session

Your console output should contain at least the following output depending on the scenario and commands. But feel free to add extra output as you see
fit.

```bash
$ login Alice
Hello, Alice!
Your balance is $0

$ deposit 100
Your balance is $100

$ logout
Goodbye, Alice!

$ login Bob
Hello, Bob!
Your balance is $0

$ deposit 80
Your balance is $80

$ transfer Alice 50
Transferred $50 to Alice
your balance is $30

$ transfer Alice 100
Transferred $30 to Alice
Your balance is $0
Owed $70 to Alice

$ deposit 30
Transferred $30 to Alice
Your balance is $0
Owed $40 to Alice

$ logout
Goodbye, Bob!

$ login Alice
Hello, Alice!
Your balance is $210
Owed $40 from Bob

$ transfer Bob 30
Your balance is $210
Owed $10 from Bob

$ logout
Goodbye, Alice!

$ login Bob
Hello, Bob!
Your balance is $0
Owed $10 to Alice

$ deposit 100
Transferred $10 to Alice
Your balance is $90

$ logout
Goodbye, Bob!
```

## How to build and run

### Prerequisite

- Java 8
- Gradle 5.2.1

### Build

The project uses Gradle. To build this application run following command:

```bash
> gradlew clean build
```

It will build an executable jar file in `/build/libs/atm-1.0.jar`.

#### Build with test coverage report

To build test coverage report for this project run following command:

```bash
> gradlew clean build jacocoTestReport
```

Test coverage report can be found in `/build/reports/jacoco/test/html/index.html`

### Run

To run the program, move to the `/build/libs` folder and run following command:

```bash
> java -jar atm-1.0.jar
```

### Help

To get help for available command run following command:

```bash
> help
```

```
login [name] - logs in as this customer and creates the customer if not exist
logout - logs out of the current customer
deposit [amount] - deposits this amount to the logged in customer
withdraw [amount] - withdraws this amount from the logged in customer
transfer [target] [amount] - transfers this amount from the logged in customer to the target customer
exit - close the program
help - print this help
```
