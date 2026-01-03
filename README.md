# SQL Parser Compiler Project

A comprehensive SQL parser and lexer built using ANTLR4 that tokenizes and parses SQL statements into an Abstract Syntax Tree (AST). This project demonstrates both lexical analysis and syntactic analysis for SQL, supporting a wide range of SQL keywords, operators, data types, and complex query structures.

## 📋 Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Building the Project](#building-the-project)
- [Running the Project](#running-the-project)
- [Project Structure](#project-structure)
- [Supported SQL Features](#supported-sql-features)
- [Example Output](#example-output)
- [Grammar Files](#grammar-files)
- [Troubleshooting](#troubleshooting)

## ✨ Features

- **Complete SQL Parser**: Full parsing of SQL statements into Abstract Syntax Trees
- **Comprehensive SQL Support**: Parses DML, DDL, DCL, and TCL statements
- **Advanced SQL Features**: Window functions, CTEs, subqueries, and complex joins
- **Multiple SQL Dialects**: Supports syntax from various SQL databases (MySQL, PostgreSQL, Oracle, SQL Server)
- **Case-Insensitive Parsing**: SQL keywords are recognized regardless of case
- **Comment Handling**: Supports line comments (`--`), block comments (`/* */`), and hash comments (`#`)
- **Rich Data Type Support**: Includes standard and vendor-specific data types
- **Expression Parsing**: Full support for complex expressions, predicates, and operators
- **Control Flow**: IF/ELSE statements, WHILE loops, CASE expressions

## 🔧 Prerequisites

Before you begin, ensure you have the following installed on your system:

- **Java Development Kit (JDK) 11 or higher**
  - Check your version: `java -version`
  - Download from: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)

- **Apache Maven 3.6 or higher**
  - Check your version: `mvn -version`
  - Download from: [Maven Official Website](https://maven.apache.org/download.cgi)

- **Git** (for cloning the repository)
  - Check your version: `git --version`
  - Download from: [Git Official Website](https://git-scm.com/downloads)

## 📥 Installation

### Clone the Repository

```bash
git clone https://github.com/eliasnadder/compiler-project.git
cd compiler-project
```

### Verify Prerequisites

Ensure Java and Maven are properly installed:

```bash
java -version
mvn -version
```

You should see output indicating Java 11+ and Maven 3.6+.

## 🔨 Building the Project

The project uses Maven to manage dependencies and build automation. The build process includes:

1. Downloading ANTLR4 dependencies
2. Generating lexer code from the grammar file (`SQLLexer.g4`)
3. Generating parser code from the grammar file (`SQLParser.g4`)
4. Compiling all Java sources

### Build Command

```bash
mvn clean compile
```

**What this does:**

- `clean`: Removes any previous build artifacts
- `compile`: Compiles the source code and generates ANTLR4 lexer and parser classes

### Expected Output

You should see output similar to:

```Text
[INFO] --- antlr4-maven-plugin:4.13.1:antlr4 (default) @ demo ---
[INFO] Processing grammar: SQLLexer.g4
[INFO] Processing grammar: SQLParser.g4
[INFO] BUILD SUCCESS
```

## 🚀 Running the Project

Once the project is built, you can run the parser test:

### Option 1: Using Maven

```bash
mvn exec:java -Dexec.mainClass="com.example.TestParser"
```

### Option 2: Using Java Directly

```bash
# From the project root directory
java -cp "target/classes;target/dependency/*" com.example.TestParser
```

**Note for Linux/Mac users:** Replace `;` with `:` in the classpath:

```bash
java -cp "target/classes:target/dependency/*" com.example.TestParser
```

### Option 3: Build and Run in One Command

```bash
mvn clean compile exec:java -Dexec.mainClass="com.example.TestParser"
```

### Testing with Custom SQL

The parser reads SQL from `testing.sql` file. You can modify this file with your own SQL statements and run the parser to see the resulting parse tree.

## 📁 Project Structure

```
compiler-project/
├── src/
│   └── main/
│       ├── antlr4/
│       │   └── com/
│       │       └── example/
│       │           ├── SQLLexer.g4           # ANTLR4 lexer grammar
│       │           └── SQLParser.g4          # ANTLR4 parser grammar
│       └── java/
│           └── com/
│               └── example/
│                   ├── TestLexer.java        # Lexer test class
│                   └── TestParser.java       # Parser test class
├── target/                                   # Generated files (after build)
│   ├── classes/                              # Compiled classes
│   └── generated-sources/                    # ANTLR4 generated code
├── testing.sql                               # Sample SQL file for testing
├── pom.xml                                   # Maven configuration
├── .gitignore                                # Git ignore rules
└── README.md                                 # This file
```

## 🎯 Supported SQL Features

The parser recognizes and parses:

### Data Manipulation (DML)

- **SELECT**: Full SELECT statements with all clauses
  - FROM, WHERE, GROUP BY, HAVING, ORDER BY
  - DISTINCT, TOP, LIMIT, OFFSET
- **INSERT**: Single and multi-row inserts, INSERT...SELECT
- **UPDATE**: With SET and WHERE clauses
- **DELETE**: With WHERE clause
- **MERGE**: Full MERGE syntax with WHEN MATCHED/NOT MATCHED

### Data Definition (DDL)

- `CREATE`, `ALTER`, `DROP`, `TRUNCATE`
- `TABLE`, `VIEW`, `INDEX`, `SCHEMA`, `DATABASE`
- `CONSTRAINT`, `PRIMARY KEY`, `FOREIGN KEY`, `UNIQUE`, `CHECK`

### Access Control (DCL)

- `GRANT`, `REVOKE`, `DENY`

### Transaction Control (TCL)

- `BEGIN`, `COMMIT`, `ROLLBACK`, `SAVEPOINT`

### Advanced Features

#### Joins

- `INNER JOIN`, `LEFT JOIN`, `RIGHT JOIN`
- `FULL OUTER JOIN`, `CROSS JOIN`
- `NATURAL JOIN`, `LATERAL JOIN`

#### Expressions

- Arithmetic operators: `+`, `-`, `*`, `/`, `%`
- Comparison operators: `=`, `!=`, `<`, `>`, `<=`, `>=`
- Logical operators: `AND`, `OR`, `NOT`
- Predicates: `IN`, `BETWEEN`, `LIKE`, `EXISTS`, `IS NULL`

#### Functions

- **Aggregate**: `COUNT`, `SUM`, `AVG`, `MIN`, `MAX`, `STRING_AGG`
- **Window**: `ROW_NUMBER`, `RANK`, `DENSE_RANK`, `LAG`, `LEAD`, `NTILE`
- **System**: `CURRENT_DATE`, `CURRENT_TIME`, `CURRENT_TIMESTAMP`, `USER`

#### Control Flow

- `CASE...WHEN...THEN...ELSE...END`
- `IF (condition) { statements } ELSE { statements }`
- `WHILE (condition) { statements }`
- `RETURN`, `BREAK`, `CONTINUE`

#### Other Features

- **Qualified Names**: `table.column`, `schema.table.column`
- **Subqueries**: In SELECT, FROM, WHERE clauses
- **Window Specifications**: `PARTITION BY`, `ORDER BY`, `ROWS/RANGE`
- **CTEs**: `WITH ... AS (...)` (Common Table Expressions)

### Data Types

50+ SQL data types including:

- Numeric: `INT`, `BIGINT`, `DECIMAL`, `NUMERIC`, `FLOAT`, `DOUBLE`
- String: `CHAR`, `VARCHAR`, `TEXT`, `NVARCHAR`
- Date/Time: `DATE`, `TIME`, `DATETIME`, `TIMESTAMP`, `INTERVAL`
- Binary: `BLOB`, `CLOB`, `BINARY`, `VARBINARY`
- Modern: `JSON`, `JSONB`, `XML`, `UUID`, `ARRAY`

## 📊 Example Output

### Input SQL [testing.sql](testing.sql)

```sql
SELECT 
    u.name, 
    COUNT(o.id) AS order_count,
    ROW_NUMBER() OVER (PARTITION BY u.country ORDER BY u.name) AS row_num
FROM users u
INNER JOIN orders o ON u.id = o.user_id
WHERE u.age >= 18 AND u.status = 'active'
GROUP BY u.name, u.country
HAVING COUNT(o.id) > 0
ORDER BY order_count DESC;

BEGIN TRANSACTION;
IF (EXISTS(SELECT * FROM users WHERE id = 1)) {
    UPDATE users SET status = 'active' WHERE id = 1;
    COMMIT;
} ELSE {
    ROLLBACK;
}
```

### Output (ParserOutput.txt - Truncated)

```
└── sqlScript
    ├── statementList
    │   ├── singleStatement
    │   │   └── selectStatement
    │   │       ├── SELECT [SELECT]
    │   │       ├── selectList
    │   │       │   ├── selectItem
    │   │       │   │   └── expression
    │   │       │   │       └── qualifiedName
    │   │       │   │           ├── u [IDENTIFIER]
    │   │       │   │           └── name [IDENTIFIER]
    │   │       │   ├── selectItem
    │   │       │   │   ├── expression
    │   │       │   │   │   └── functionCall
    │   │       │   │   │       └── aggregateFunction
    │   │       │   │   │           ├── COUNT [COUNT]
    │   │       │   │   │           └── expression
    │   │       │   │   │               └── qualifiedName
    │   │       │   │   │                   ├── o [IDENTIFIER]
    │   │       │   │   │                   └── id [IDENTIFIER]
    ...
```

The parser generates a complete Abstract Syntax Tree showing the hierarchical structure of your SQL statements.

## 📝 Grammar Files

### SQLLexer.g4

Defines all tokens (keywords, operators, literals, identifiers) that make up SQL syntax. Features:

- Case-insensitive keyword matching
- String, numeric, and hex literals
- Multiple identifier styles (quoted, backtick, bracket)
- System and local variables
- Comprehensive operator support

### SQLParser.g4

Defines the grammatical rules for SQL statements. Key features:

- **No Mutual Left Recursion**: Carefully designed to avoid ANTLR4 limitations
- **Labeled Alternatives**: Enables easy visitor pattern implementation
- **Operator Precedence**: Proper handling of expression precedence
- **Flexible Syntax**: Optional semicolons, parentheses, and keywords

## 🐛 Troubleshooting

### Issue: `JAVA_HOME not set`

**Solution:**
Set the `JAVA_HOME` environment variable:

**Windows:**

```bash
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%
```

**Linux/Mac:**

```bash
export JAVA_HOME=/path/to/jdk
export PATH=$JAVA_HOME/bin:$PATH
```

### Issue: `Maven command not found`

**Solution:**
Install Maven or add it to your PATH:

- Download from [Maven Official Website](https://maven.apache.org/download.cgi)
- Follow installation instructions for your OS

### Issue: `Build failure - dependencies not found`

**Solution:**

```bash
mvn clean install -U
```

The `-U` flag forces Maven to update dependencies.

### Issue: `Generated sources not found`

**Solution:**
The ANTLR4 plugin generates sources during the build. Make sure to run:

```bash
mvn clean compile
```

This ensures both grammar files are processed and lexer/parser classes are generated.

## 📚 Learning Resources

To learn more about ANTLR4 and compiler construction:

- [ANTLR4 Official Documentation](https://github.com/antlr/antlr4/blob/master/doc/index.md)
- [The Definitive ANTLR4 Reference](https://pragprog.com/titles/tpantlr2/the-definitive-antlr-4-reference/)
- [SQL Grammar Examples](https://github.com/antlr/grammars-v4)

## 🤝 Contributing

Contributions are welcome! Feel free to:

- Report bugs
- Suggest new SQL features
- Improve grammar rules
- Submit pull requests
- Add support for additional SQL dialects

## 📄 License

This project is open source and available for educational purposes.

## 🫂 Team

- **Elias Nadder**
  - GitHub: [@eliasnadder](https://github.com/eliasnadder)

- **Aya Loai Mohammad**
  - GitHub: [@Aya-Mohammad](https://github.com/Aya-Mohammad)

- **Bshr Mdwar**
  - GitHub: [@BshrMdwar](https://github.com/BshrMdwar)

- **Hala Zain**
  - GitHub: [@Hala-Zain](https://github.com/Hala-Zain)

---

**Happy Parsing! 🚀**

*Built with ANTLR4 - The powerful parser generator for reading, processing, executing, or translating structured text or binary files.*
