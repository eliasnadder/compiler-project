# SQL Lexer Compiler Project

A comprehensive SQL lexer built using ANTLR4 that tokenizes SQL statements. This project demonstrates lexical analysis for SQL, supporting a wide range of SQL keywords, operators, data types, and syntax elements.

## 📋 Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Building the Project](#building-the-project)
- [Running the Project](#running-the-project)
- [Project Structure](#project-structure)
- [Supported SQL Features](#supported-sql-features)
- [Example Output](#example-output)
- [Troubleshooting](#troubleshooting)

## ✨ Features

- **Comprehensive SQL Support**: Tokenizes DML, DDL, DCL, and TCL statements
- **Advanced SQL Features**: Window functions, CTEs, and complex joins
- **Multiple SQL Dialects**: Supports syntax from various SQL databases (MySQL, PostgreSQL, Oracle, etc.)
- **Case-Insensitive Parsing**: SQL keywords are recognized regardless of case
- **Comment Handling**: Supports line comments (`--`), block comments (`/* */`), and hash comments (`#`)
- **Rich Data Type Support**: Includes standard and vendor-specific data types

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
3. Compiling all Java sources

### Build Command

```bash
mvn clean compile
```

**What this does:**

- `clean`: Removes any previous build artifacts
- `compile`: Compiles the source code and generates ANTLR4 lexer classes

### Expected Output

You should see output similar to:

```
[INFO] --- antlr4-maven-plugin:4.13.1:antlr4 (antlr) @ demo ---
[INFO] Processing grammar: SQLLexer.g4
[INFO] BUILD SUCCESS
```

## 🚀 Running the Project

Once the project is built, you can run the lexer test:

### Option 1: Using Maven

```bash
mvn exec:java -Dexec.mainClass="com.example.TestLexer"
```

### Option 2: Using Java Directly

```bash
# From the project root directory
java -cp "target/classes;target/dependency/*" com.example.TestLexer
```

**Note for Linux/Mac users:** Replace `;` with `:` in the classpath:

```bash
java -cp "target/classes:target/dependency/*" com.example.TestLexer
```

### Option 3: Build and Run in One Command

```bash
mvn clean compile exec:java -Dexec.mainClass="com.example.TestLexer"
```

## 📁 Project Structure

```
compiler-project/
├── src/
│   └── main/
│       ├── antlr4/
│       │   └── com/
│       │       └── example/
│       │           └── SQLLexer.g4          # ANTLR4 grammar definition
│       └── java/
│           └── com/
│               └── example/
│                   └── TestLexer.java       # Main test class
├── target/                                   # Generated files (after build)
│   ├── classes/                             # Compiled classes
│   └── generated-sources/                   # ANTLR4 generated code
├── pom.xml                                   # Maven configuration
├── .gitignore                               # Git ignore rules
└── README.md                                 # This file
```

## 🎯 Supported SQL Features

The lexer recognizes tokens for:

### Data Manipulation (DML)

- `SELECT`, `INSERT`, `UPDATE`, `DELETE`, `MERGE`

### Data Definition (DDL)

- `CREATE`, `ALTER`, `DROP`, `TRUNCATE`
- `TABLE`, `VIEW`, `INDEX`, `SCHEMA`, `DATABASE`

### Access Control (DCL)

- `GRANT`, `REVOKE`, `DENY`

### Transaction Control (TCL)

- `BEGIN`, `COMMIT`, `ROLLBACK`, `SAVEPOINT`

### Advanced Features

- **Joins**: `INNER`, `LEFT`, `RIGHT`, `FULL OUTER`, `CROSS`
- **Window Functions**: `ROW_NUMBER`, `RANK`, `DENSE_RANK`, `LAG`, `LEAD`
- **Aggregate Functions**: `COUNT`, `SUM`, `AVG`, `MIN`, `MAX`
- **Data Types**: 50+ SQL data types including `VARCHAR`, `INTEGER`, `TIMESTAMP`, `JSON`, `BLOB`
- **Operators**: Comparison, arithmetic, logical, and bitwise operators

## 📊 Example Output

When you run the project, you'll see tokenized output like:

```
Testing SQL Input: SELECT id, name FROM Users WHERE age >= 18 AND country = 'KSA'; -- Check users
--------------------------------------------------
Token Type: SELECT               | Text: SELECT
Token Type: IDENTIFIER           | Text: id
Token Type: COMMA                | Text: ,
Token Type: IDENTIFIER           | Text: name
Token Type: FROM                 | Text: FROM
Token Type: IDENTIFIER           | Text: Users
Token Type: WHERE                | Text: WHERE
Token Type: IDENTIFIER           | Text: age
Token Type: GE                   | Text: >=
Token Type: INT_LITERAL          | Text: 18
Token Type: AND                  | Text: AND
Token Type: IDENTIFIER           | Text: country
Token Type: EQ                   | Text: =
Token Type: STRING_LITERAL       | Text: 'KSA'
Token Type: SEMICOLON            | Text: ;
```

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

This ensures the grammar file is processed and lexer classes are generated.

## 📝 Customizing the Lexer

To test with your own SQL statements:

1. Open `src/main/java/com/example/TestLexer.java`
2. Modify the `sqlInput` variable on line 7
3. Rebuild and run:
   ```bash
   mvn clean compile exec:java -Dexec.mainClass="com.example.TestLexer"
   ```

## 🤝 Contributing

Contributions are welcome! Feel free to:

- Report bugs
- Suggest new features
- Submit pull requests

## 📄 License

This project is open source and available for educational purposes.

## 👤 Author

**Elias Nadder**

- GitHub: [@eliasnadder](https://github.com/eliasnadder)

---

**Happy Compiling! 🚀**
