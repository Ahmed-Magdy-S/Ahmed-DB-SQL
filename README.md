# AhmedDB-SQL
Fully functional relational database management system which is designed and implemented in Java.

## Introduction
The project for learning purpose to get conceptual knowledge and experience about how a database management system works under the hood and how every component interacts with each other.
Its implementation is similar to SimpleDb which created by Edward Sciore in his [book](https://link.springer.com/book/10.1007/978-3-030-33836-7) but with new & modified APIs to be more understandable with extended features and appropriate documentation comments.

## Features

- Functionally, it is a multiuser, transaction-oriented database server that executes SQL statements and interacts with clients via JDBC.
- Structurally, it contains the same basic components as a commercial database management system, with similar APIs.
- Extensive API documentation provided as a JavaDoc with some examples.
- Some diagrams added in Resource folder to visualize workflow for some db components.
- Current Data types available: Integer, String.

### Implementation Roadmap
****The project is still work in progress***
- [x] Disk and File Management
  - Added Page class.
  - Added BlockId class.
  - Added FileManager Class.
  - Added initial configuration classes.
- [ ] Memory Management
- [ ] Transaction Management
- [ ] Record Management
- [ ] Metadata Management
- [ ] Query Processing
- [ ] Parsing
- [ ] Planning Process
- [ ] JDBC Interfaces
- [ ] Indexing
- [ ] Materialization and Sorting
- [ ] Effective Buffer Utilization
- [ ] Query Optimization

## Setup
It will be available after finishing of the project.

## Testing
I have a full AhmedDB-SQL test suite that I use to ensure the db components do what they're supposed to do.

To Run the tests, make sure the maven is installed and run:

```shell
mvn test
```
