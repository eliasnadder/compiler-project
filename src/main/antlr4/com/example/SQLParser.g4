parser grammar SQLParser;

options {
    tokenVocab = SQLLexer;
}

// ________________________ Entry Point 

sqlScript
    : statementList EOF
    ;

// ________________________ Statements 

statement
    : selectStatement
    | insertStatement
    | updateStatement
    | deleteStatement
    | mergeStatement
    | grantStatement
    | revokeStatement
    | denyStatement
    | transactionStatement
    | controlFlowStatement
    | createStatement
    | alterStatement
    | dropStatement
    | truncateStatement
    ;

// Bshr DML Statements 

// ---------------------- SELECT ---------------------------
selectStatement
    : SELECT selectList
      fromClause?
      whereClause?
      groupByClause?
      havingClause?
      orderByClause?
    ;

selectList
    : STAR
    | selectItem (COMMA selectItem)*
    ;

selectItem
    : expression (AS? IDENTIFIER)?
    ;

fromClause
    : FROM tableSource
    ;

tableSource
    : tableFactor (joinClause)*
    ;

tableFactor
    : qualifiedName (AS? IDENTIFIER)?
    | LPAREN selectStatement RPAREN AS? IDENTIFIER
    ;

joinClause
    : joinType? JOIN tableFactor ON expression
    ;

joinType
    : INNER
    | LEFT OUTER?
    | RIGHT OUTER?
    | FULL OUTER?
    | CROSS
    ;

whereClause
    : WHERE expression
    ;

groupByClause
    : GROUP BY expression (COMMA expression)*
    ;

havingClause
    : HAVING expression
    ;

orderByClause
    : ORDER BY orderExpression (COMMA orderExpression)*
    ;

orderExpression
    : expression (ASC | DESC)?
    ;

// ---------------------- INSERT ---------------------------
insertStatement
    : INSERT INTO qualifiedName (LPAREN IDENTIFIER (COMMA IDENTIFIER)* RPAREN)?
      (VALUES LPAREN expression (COMMA expression)* RPAREN (COMMA LPAREN expression (COMMA expression)* RPAREN)*
      | selectStatement)
    ;

// ---------------------- UPDATE ---------------------------
updateStatement
    : UPDATE qualifiedName
      SET assignmentClause (COMMA assignmentClause)*
      whereClause?
    ;

assignmentClause
    : IDENTIFIER EQ expression
    ;

// ---------------------- DELETE ---------------------------
deleteStatement
    : DELETE FROM qualifiedName
      whereClause?
    ;

// ---------------------- MERGE ----------------------------
mergeStatement
    : MERGE INTO qualifiedName (AS? IDENTIFIER)?
      USING tableSource
      ON expression
      whenClauseMerge+
    ;

whenClauseMerge
    : WHEN MATCHED (AND expression)? THEN mergeAction
    | WHEN NOT MATCHED (AND expression)? THEN mergeAction
    ;

mergeAction
    : UPDATE SET assignmentClause (COMMA assignmentClause)*
    | DELETE
    | INSERT (LPAREN IDENTIFIER (COMMA IDENTIFIER)* RPAREN)? VALUES LPAREN expression (COMMA expression)* RPAREN
    ;

//________________________________________________________________________________________________________________________________

// Aya 

// Expressions

expression
    : LPAREN expression RPAREN                                   # ParenExpression
    | NOT expression                                             # NotExpression
    | expression (STAR | DIV | MOD) expression                   # MultiplicativeExpression
    | expression (PLUS | MINUS_OP) expression                    # AdditiveExpression
    | expression comparisonOperator expression                   # ComparisonExpression
    | expression AND expression                                  # AndExpression
    | expression OR expression                                   # OrExpression
    | expression IS NOT? NULL                                    # IsNullExpression
    | expression NOT? IN LPAREN (selectStatement | expressionList) RPAREN # InExpression
    | expression NOT? BETWEEN expression AND expression          # BetweenExpression
    | expression NOT? LIKE expression (ESCAPE expression)?       # LikeExpression
    | EXISTS LPAREN selectStatement RPAREN                       # ExistsExpression
    | functionCall                                               # FunctionExpression
    | qualifiedName                                              # ColumnExpression
    | literal                                                    # LiteralExpression
    ;

expressionList
    : expression (COMMA expression)*
    ;

qualifiedName
    : IDENTIFIER (DOT IDENTIFIER)*
    ;

comparisonOperator
    : EQ | NEQ | LT | LE | GT | GE
    ;


// function 

functionCall
    : systemFunction
    | aggregateFunction
    | windowFunction
    | userFunction
    ;

systemFunction
    : USER
    | CURRENT_DATE
    | CURRENT_TIME
    | CURRENT_TIMESTAMP
    ;

aggregateFunction
    : (COUNT | SUM | AVG | MIN | MAX) LPAREN (STAR | DISTINCT? expression) RPAREN (OVER LPAREN windowSpec RPAREN)?
    ;

windowFunction
    : (ROW_NUMBER | RANK | DENSE_RANK | NTILE) LPAREN RPAREN OVER LPAREN windowSpec RPAREN
    ;

userFunction
    : IDENTIFIER LPAREN (expression (COMMA expression)*)? RPAREN
    ;

windowSpec
    : (PARTITION BY expression (COMMA expression)*)? (ORDER BY orderExpression (COMMA orderExpression)*)?
    ;

//________________________________________________________________________________________________________________________________

// Elias 

// Literals 

literal
    : STRING_LITERAL
    | INT_LITERAL
    | FLOAT_LITERAL
    | HEX_LITERAL
    | TRUE
    | FALSE
    | NULL
    ;

// Security Statements

grantStatement
    : GRANT IDENTIFIER ON qualifiedName TO IDENTIFIER
    ;

revokeStatement
    : REVOKE IDENTIFIER ON qualifiedName FROM IDENTIFIER
    ;

denyStatement
    : DENY IDENTIFIER ON qualifiedName TO IDENTIFIER
    ;

//  Transaction 

transactionStatement
    : BEGIN TRANSACTION?
    | COMMIT
    | ROLLBACK
    | SAVEPOINT IDENTIFIER
    ;

//  Control Flow 

controlFlowStatement
    : caseExpression
    | ifStatement
    | whileStatement
    | returnStatement
    | breakStatement
    | continueStatement
    ;

caseExpression
    : CASE whenClause+ (ELSE expression)? END
    ;

whenClause
    : WHEN expression THEN expression
    ;

ifStatement
    : IF LPAREN expression RPAREN block (ELSE block)?
    ;

whileStatement
    : WHILE LPAREN expression RPAREN block
    ;

returnStatement
    : RETURN expression? SEMICOLON?
    ;

breakStatement
    : BREAK SEMICOLON?
    ;

continueStatement
    : CONTINUE SEMICOLON?
    ;

//  Blocks 

block
    : LBRACE statementList RBRACE
    | singleStatement
    ;

statementList
    : (singleStatement)*
    ;

singleStatement
    : (selectStatement
    | insertStatement
    | updateStatement
    | deleteStatement
    | mergeStatement
    | grantStatement
    | revokeStatement
    | denyStatement
    | transactionStatement
    | ifStatement
    | whileStatement
    | returnStatement
    | breakStatement
    | continueStatement
    | caseExpression
    | createStatement
    | alterStatement
    | dropStatement
    | truncateStatement) SEMICOLON?
    ;

//___________________________________________________________________________________________
// Hala DDL Statements 

createStatement
    : CREATE createObject
    ;

createObject
    : createDatabase
    | createTable
    | createView
    | createIndex
    ;

createDatabase
    : DATABASE IF_NOT_EXISTS? databaseName
    ;

databaseName
    : IDENTIFIER
    ;

createTable
    : TABLE IF_NOT_EXISTS? tableName LPAREN columnDefinition (COMMA columnDefinition)* RPAREN
    ;

tableName
    : IDENTIFIER
    ;

columnDefinition
    : columnName datatype columnConstraint*
    ;

columnName
    : IDENTIFIER
    ;

datatype
    : INT | BIGINT | TINYINT | SMALLINT | DECIMAL | NUMERIC | FLOAT | DOUBLE | REAL
    | BOOLEAN | BOOL | CHAR | VARCHAR | TEXT | ENUM | SET | DATETIME | DATE | TIME
    | TIMESTAMP | YEAR | BINARY | VARBINARY | BLOB | JSON | UUID | BIT
    ;

columnConstraint
    : PRIMARY KEY
    | FOREIGN KEY
    | UNIQUE
    | DEFAULT literal?
    | CHECK LPAREN expression RPAREN
    | ON UPDATE expression
    | COLLATE IDENTIFIER
    | BINARY
    ;

createView
    : VIEW IF_NOT_EXISTS? viewName AS selectStatement
    ;

viewName
    : IDENTIFIER
    ;

createIndex
    : INDEX indexName ON tableName LPAREN columnName (COMMA columnName)* RPAREN
    ;

indexName
    : IDENTIFIER
    ;

//  ALTER Statements 

alterStatement
    : ALTER alterObject
    ;

alterObject
    : alterTable
    ;

alterTable
    : TABLE tableName alterTableAction (COMMA alterTableAction)*
    ;

alterTableAction
    : addColumn
    | dropColumn
    | modifyColumn
    | changeColumn
    | renameColumn
    | renameTable
    | addConstraint
    | dropConstraint
    | addIndex
    | dropColumnIndex
    | alterColumnDefault
    ;

addColumn       : ADD COLUMN columnDefinition;
dropColumn      : DROP COLUMN columnName;
modifyColumn    : MODIFY COLUMN columnDefinition;
changeColumn    : CHANGE COLUMN IDENTIFIER columnDefinition;
renameColumn    : RENAME COLUMN IDENTIFIER TO IDENTIFIER;
renameTable     : RENAME TO IDENTIFIER;
addConstraint   : ADD (CONSTRAINT IDENTIFIER)? tableConstraint;
dropConstraint  : DROP (PRIMARY KEY | FOREIGN KEY IDENTIFIER | INDEX IDENTIFIER | KEY IDENTIFIER | CONSTRAINT IDENTIFIER | CHECK IDENTIFIER);
addIndex        : ADD (UNIQUE)? (INDEX | KEY) IDENTIFIER LPAREN columnName (COMMA columnName)* RPAREN;
dropColumnIndex : DROP (INDEX | KEY) IDENTIFIER;
alterColumnDefault: ALTER COLUMN columnName (SET DEFAULT defaultValue | DROP DEFAULT);

tableConstraint
    : PRIMARY KEY LPAREN columnList RPAREN
    | UNIQUE LPAREN columnList RPAREN
    | FOREIGN KEY LPAREN columnList RPAREN REFERENCES tableName LPAREN columnList RPAREN
    | CHECK LPAREN expression RPAREN
    ;

columnList
    : columnName (COMMA columnName)*
    ;

defaultValue
    : STRING_LITERAL
    | INT_LITERAL
    | FLOAT_LITERAL
    | TRUE
    | FALSE
    | NULL
    ;

//  DROP Statements 

dropStatement
    : DROP dropObject
    ;

dropObject
    : dropTable
    | dropDatabase
    | dropView
    | dropIndex
    ;

dropDatabase
    : DATABASE (IF_EXISTS)? databaseName
    ;

dropTable
    : TABLE (IF_EXISTS)? tableName (COMMA tableName)?
    ;

dropView
    : VIEW (IF_EXISTS)? viewName (COMMA viewName)*
    ;

dropIndex
    : INDEX indexName ON tableName
    ;

//  TRUNCATE Statements 

truncateStatement
    : TRUNCATE truncateObject
    ;

truncateObject
    : truncateTable
    ;

truncateTable
    : TABLE (IF_EXISTS)? tableName (COMMA tableName)* truncateOption*
    ;

truncateOption
    : CASCADE
    | RESTRICT
    | RESTART IDENTITY
    | CONTINUE IDENTITY
    ;
