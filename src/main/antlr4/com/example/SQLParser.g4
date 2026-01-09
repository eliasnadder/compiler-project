parser grammar SQLParser;

options {
    tokenVocab = SQLLexer;
}
//! Updated: Replace '(' and ')' with LPAREN and RPAREN

// ________________ Init ________________
//! Updated: Replace statement* EOF with statementList EOF
sqlScript
    : statementList EOF
    ;

//* Updated: Add insert, update, delete, merge
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

//_______________________________________bshr 

// ________________ SELECT Statements ________________

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

// tableFactor
//     : IDENTIFIER (AS? IDENTIFIER)?
//     | '(' selectStatement ')' AS? IDENTIFIER
//     ;
// ! Updated: Replace IDENTIFIER with qualifiedName
tableFactor
    : qualifiedName (AS? IDENTIFIER)?
    | LPAREN selectStatement RPAREN AS? IDENTIFIER
    ;

//! Updated: Relpace joinType JOIN with joinType? JOIN
joinClause
    : joinType? JOIN tableFactor ON expression
    ;

//! Updated: Add OUTER? to LEFT, RIGHT, FULL
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

// ________________ INSERT Statement ________________

insertStatement
    : INSERT INTO qualifiedName (LPAREN IDENTIFIER (COMMA IDENTIFIER)* RPAREN)?
      (VALUES LPAREN expression (COMMA expression)* RPAREN (COMMA LPAREN expression (COMMA expression)* RPAREN)*
      | selectStatement)
    ;

// ________________ UPDATE Statement ________________

updateStatement
    : UPDATE qualifiedName
      SET assignmentClause (COMMA assignmentClause)*
      whereClause?
    ;

assignmentClause
    : IDENTIFIER EQ expression
    ;

// ________________ DELETE Statement ________________

deleteStatement
    : DELETE FROM qualifiedName
      whereClause?
    ;

// ________________ MERGE Statement ________________

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

//_______________________________________elias

// ________________ Expressions & Predicates ________________

// expression
//     : '(' expression ')'
//     | expression AND expression
//     | expression OR expression
//     | NOT expression
//     | predicate
//     ;

// predicate
//     : expression comparisonOperator expression
//     | functionCall
//     | literal
//     | IDENTIFIER
//     ;

//! Updated: merge expression and predicate rules into a single

expression
    : LPAREN expression RPAREN                                                 # ParenExpression
    | NOT expression                                                           # NotExpression
    | expression (STAR | DIV | MOD) expression                                 # MultiplicativeExpression
    | expression (PLUS | MINUS_OP) expression                                  # AdditiveExpression
    | expression comparisonOperator expression                                 # ComparisonExpression
    | expression AND expression                                                # AndExpression
    | expression OR expression                                                 # OrExpression
    | expression IS NOT? NULL                                                  # IsNullExpression
    | expression NOT? IN LPAREN (selectStatement | expressionList) RPAREN      # InExpression
    | expression NOT? BETWEEN expression AND expression                        # BetweenExpression
    | expression NOT? LIKE expression (ESCAPE expression)?                     # LikeExpression
    | EXISTS LPAREN selectStatement RPAREN                                     # ExistsExpression
    | functionCall                                                             # FunctionExpression
    | qualifiedName                                                            # ColumnExpression
    | literal                                                                  # LiteralExpression
    ;

expressionList
    : expression (COMMA expression)*
    ;

qualifiedName
    : IDENTIFIER (DOT IDENTIFIER)*
    ;

comparisonOperator
    : EQ
    | NEQ
    | LT
    | LE
    | GT
    | GE
    ;

//______________________________________aya
//______________________________________updated by elias

// ________________ Functions ________________

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
    : (COUNT|SUM|AVG|MIN|MAX) LPAREN (STAR | DISTINCT? expression) RPAREN (OVER LPAREN windowSpec RPAREN)?
    ;

windowFunction
    : (ROW_NUMBER|RANK|DENSE_RANK|NTILE) LPAREN RPAREN OVER LPAREN windowSpec RPAREN
    ;

userFunction
    : IDENTIFIER LPAREN (expression (COMMA expression)*)? RPAREN
    ;

windowSpec
    : (PARTITION BY expression (COMMA expression)*)? (ORDER BY orderExpression (COMMA orderExpression)*)?
    ;

// ________________ Literals ________________
//! Updated: added TRUE, FALSE, NULL
//! Fixed string literals string and numbers
literal
    : STRING_LITERAL
    | INT_LITERAL
    | FLOAT_LITERAL
    | HEX_LITERAL
    | TRUE
    | FALSE
    | NULL
    ;

// ________________ Security Statements ________________
//! Updated: Replace ON qualifiedName with ON qualifiedName
grantStatement
    : GRANT IDENTIFIER ON qualifiedName TO IDENTIFIER
    ;

revokeStatement
    : REVOKE IDENTIFIER ON qualifiedName FROM IDENTIFIER
    ;

denyStatement
    : DENY IDENTIFIER ON qualifiedName TO IDENTIFIER
    ;

// ________________ Transaction Control ________________

transactionStatement
    : BEGIN TRANSACTION?
    | COMMIT
    | ROLLBACK
    | SAVEPOINT IDENTIFIER
    ;

// ________________ Control Flow Statements ________________

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

//! Updated: Add optional ELSE block to ifStatement
ifStatement
    : IF LPAREN expression RPAREN block (ELSE block)?
    ;

whileStatement
    : WHILE LPAREN expression RPAREN block
    ;

//! Updated: Add optional SEMICOLON to returnStatement
returnStatement
    : RETURN expression? SEMICOLON?
    ;

breakStatement
    : BREAK SEMICOLON?
    ;

continueStatement
    : CONTINUE SEMICOLON?
    ;

//! Updated: Replace (statement)* with statementList
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
//______________________________________Hala
//________________Create________________
createStatement:
    CREATE createObject;
createObject:
    createDatabase
    |createTable
    |createView
    |createIndex
    ;

    
columnName
    : IDENTIFIER
    ;
datatype:
    INT
    |BIGINT
    |TINYINT
    |SMALLINT
    |DECIMAL
    |NUMERIC
    |FLOAT
    |DOUBLE
    |REAL
    |BOOLEAN
    |BOOL
    |CHAR
    |VARCHAR
    |TEXT
    |ENUM
    |SET
    |DATETIME
    |DATE
    |TIME
    |TIMESTAMP
    |YEAR
    |BINARY
    |VARBINARY
    |BLOB
    |JSON
    |UUID
    |BIT;
columnConstraint
    : PRIMARY KEY
    | FOREIGN KEY
    | UNIQUE
    | DEFAULT literal?
    | CHECK '(' expression ')'
    | ON UPDATE expression
    | COLLATE IDENTIFIER
    | BINARY
    ;


columnDefinition:columnName datatype columnConstraint*;

createDatabase:
    DATABASE IF_NOT_EXISTS? databaseName;

databaseName:IDENTIFIER;

createTable:
    TABLE IF_NOT_EXISTS? tableName
    LPAREN columnDefinition (COMMA columnDefinition)* RPAREN;

tableName:IDENTIFIER;

viewName:IDENTIFIER;

createView:
    VIEW IF_NOT_EXISTS? viewName AS selectStatement;

indexName:IDENTIFIER;

createIndex:
    INDEX indexName ON tableName LPAREN columnName (COMMA columnName)* RPAREN;
//________________Alter_______________________
alterStatement:
    ALTER alterObject;

alterObject:
    alterTable
    ;

alterTable:
    TABLE tableName alterTableAction  (COMMA alterTableAction)*
    ;

oldColumnName:
    IDENTIFIER
    ;

newColumnName:
    IDENTIFIER
    ;
newTableName:
    IDENTIFIER
    ;

constraintName:
    IDENTIFIER
    ;


alterTableAction:
    addColumn
    |dropColumn
    |modifyColumn
    |changeColumn
    |renameColumn
    |renameTable
    |addConstraint
    |dropConstraint
    |addIndex
    |dropColumnIndex
    |alterColumnDefault
    ;

addColumn:
    ADD COLUMN columnDefinition
    ;

dropColumn:
    DROP COLUMN columnName
    ;

modifyColumn:
    MODIFY COLUMN columnDefinition
    ;

changeColumn:
    CHANGE COLUMN oldColumnName columnDefinition
    ;

renameColumn:
    RENAME COLUMN oldColumnName TO newColumnName
    ;

renameTable:
    RENAME TO newTableName
    ;

tableConstraint
    : PRIMARY KEY LPAREN columnList RPAREN
    | UNIQUE LPAREN columnList RPAREN
    | FOREIGN KEY LPAREN columnList RPAREN REFERENCES tableName LPAREN columnList RPAREN
    | CHECK LPAREN expression RPAREN
    ;

columnList
    : columnName (COMMA columnName)*
    ;

addConstraint
    : ADD (CONSTRAINT constraintName)? tableConstraint
    ;

dropConstraint:
      DROP (
          PRIMARY KEY
        | FOREIGN KEY constraintName
        | INDEX constraintName
        | KEY constraintName
        | CONSTRAINT constraintName
        | CHECK constraintName
      )
    ;


addIndex:
    ADD (UNIQUE)? (INDEX | KEY) indexName LPAREN columnName (COMMA columnName)* RPAREN
    ;
dropColumnIndex:
    DROP (INDEX|KEY) indexName
    ;
defaultValue
    : STRING_LITERAL
    | INT_LITERAL
    | FLOAT_LITERAL
    | TRUE
    | FALSE
    | NULL
    ;

alterColumnDefault:
    ALTER COLUMN columnName (SET DEFAULT defaultValue|DROP DEFAULT)
    ;



//________________Drop__________________________
dropStatement:
    DROP dropObject;

dropObject:
    dropTable
    |dropDatabase
    |dropView
    |dropIndex
    ;

dropDatabase:
    DATABASE (IF_EXISTS)? databaseName
    ;

dropTable:
    TABLE (IF_EXISTS)? tableName (COMMA tableName)?
    ;

dropView:
    VIEW (IF_EXISTS)? viewName (COMMA viewName)*
    ;

dropIndex:
    INDEX indexName ON tableName 
    ;

//________________Truncate________________________

truncateStatement:
    TRUNCATE truncateObject
    ;

truncateObject:
    truncateTable
    ;

truncateTable:
    TABLE (IF_EXISTS)? tableName (COMMA tableName)* truncateOption*;

truncateOption:
    CASCADE
    |RESTRICT
    |RESTART IDENTITY
    |CONTINUE IDENTITY
    ;


    
    


