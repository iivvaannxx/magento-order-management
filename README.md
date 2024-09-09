# Commerce Services - Technical Interview

You are currently on the main branch. This is just the starter code that was provided for
the assignment, without any modifications.

> [!NOTE]
> The original README with the assignment description can be found in
> the [ASSIGNMENT.md](ASSIGNMENT.md) file.

## Solution

To view the solution, please check out the following branches:

### 1. `assignment` branch

> [!IMPORTANT]
> This branch contains the core implementation that fulfills all the original
> requirements. The code in this branch adheres to what was asked in the assignment.
> [View this branch here](https://github.com/iivvaannxx/magento-order-management/tree/assignment).

The following features were implemented:

- Creating new orders via a POST endpoint.
- Retrieving existing orders via a GET endpoint.
- Unique order identifier generation.
- Asynchronous stock updating.
- Stock validation for order processing.
- Robust error handling and basic logging.
- Tests for all the above features, including both unit and integration tests.

[Visit this branch README for a complete and detailed explanation of the code](https://github.com/iivvaannxx/magento-order-management/tree/assignment).

### 2. `assignment-extra` branch

> [!IMPORTANT]
> This branch is built on top of the
> [assignment branch](https://github.com/iivvaannxx/magento-order-management/tree/assignment).
> It extends that solution with a frontend application that can be used to play with the
> API.
> [View this branch here](https://github.com/iivvaannxx/magento-order-management/tree/assignment-extra).

In my interview with Alejandro, he mentioned that the role involves a mix of frontend,
DevOps and backend responsibilities. To showcase my ability to contribute across these
diverse areas, I've created this branch with "extra" features that are not part of the
original requirements.

Aside from the original features, this branch adds the following:

- A simple frontend application that interacts with the backend API.
- A new endpoint to DELETE an order.
- A simple CI/CD pipeline that runs on GitHub Actions.