# Store Management System

This project is a store management system that models and implements the process of stocking and selling goods in a store. It allows managing inventory, cashiers, and generating sales receipts.

## Features

- **Stock Management**: The system allows loading stocks into the store for sale. Each stock item is identified by a unique number and includes information such as name, unit price, category (food or non-food), and expiration date. The selling price of the stock is determined based on the category and proximity to the expiration date.

- **Cashier Management**: The system keeps track of cashiers working in the store. Each cashier is identified by a unique number and has a name and a monthly salary. Each cashier can be assigned to a specific cash register in the store.

- **Sales Process**: Cashiers mark the items that customers wish to purchase at the respective cash registers. The system verifies if the customer has sufficient funds to buy the requested items. If the quantity of a particular item is insufficient, an exception is thrown indicating the shortage of that item.

- **Sales Receipt Generation**: Upon successful purchase, the system generates a sales receipt containing the following information: receipt number, issuing cashier, date and time of issuance, a list of purchased items with their prices and quantities, and the total amount to be paid by the customer.

- **Sales Statistics**: The system maintains records of all issued sales receipts, including the total number of receipts and the total revenue generated. The content of each sales receipt is saved in a separate file named with the receipt number.

- **Financial Analysis**: The system allows calculating various financial metrics for the store, including employee salaries, stock delivery costs, revenue from sales, and overall profit.

- **Exception Handling**: The project employs exception handling techniques to ensure proper error handling and recovery.

- **Unit Testing**: Unit tests are implemented to validate the functionality of the system components.

## Technologies Used

The application is developed in Java and adheres to the requirements outlined above.

## Usage

To use this application, follow these steps:

1. Clone the repository to your local machine.
2. Compile and run the Java code.
3. Follow the on-screen instructions to interact with the store management system.

## Contributors

This project is developed and maintained by Martin Trenkov. Contributions and suggestions are welcome. Please submit any issues or feature requests through the GitHub repository.

## License

This project is licensed under the [MIT License](LICENSE). Feel free to use, modify, and distribute the code for personal and commercial purposes.
