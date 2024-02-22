# AhmedDB-SQL
Fully functional relational database management system which is designed and implemented in Java.

## Introduction
The project for learning purpose to get conceptual knowledge and experience about how a database management system works under the hood and how every component interacts with each other.

## Features

- Functionally, it is a multiuser, transaction-oriented database server that executes SQL statements and interacts with clients via JDBC.
- Structurally, it contains the same basic components as a commercial database management system, with similar APIs.
- Extensive API documentation provided as a JavaDoc with some examples. Public API docs will be available after the first release.
- Some diagrams added in Resource folder to visualize workflow for some db components.
- Current Data types available: Integer, String, BLOB (raw data, i.e: bytes).

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

## Known problems
- There are holes in db files (e.g: table files, log files, etc...). The holes are exist due to some records don't fit into the remaining of the file block space, so a new block is created every time, leaving the previous block in non-full state.
- A way to handle big records that exceed the buffer capacity (page size).

## Need to be proved
- Log manager uses indirect buffer for its memory page, I don't know if this is a good option or not when comparing it to the direct buffer

## Future Ideas to be considered 
- Caching mechanism like redis
- search engine with features like: "Auto-complete, spell-checking, full text-search, geoloaction, etc..."

## Setup
It will be available after finishing of the project.

## Testing
I have a full AhmedDB-SQL test suite that I use to ensure the db components do what they're supposed to do.

To Run the tests, make sure the maven is installed and run:

```shell
mvn test
```

## References
- Sciore, E. (2020). Database design and implementation. Data-Centric Systems and Applications. https://doi.org/10.1007/978-3-030-33836-7 