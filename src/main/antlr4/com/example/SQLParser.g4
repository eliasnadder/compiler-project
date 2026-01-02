parser grammar SQLParser;

options {
    tokenVocab = SQLLexer;
}

// ________________ Init ________________

sqlScript
    : statement* EOF
    ;

statement
    : selectStatement
    | grantStatement
    | revokeStatement
    | denyStatement
    | transactionStatement
    | controlFlowStatement
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

tableFactor
    : IDENTIFIER (AS? IDENTIFIER)?
    | '(' selectStatement ')' AS? IDENTIFIER
    ;

joinClause
    : joinType JOIN tableFactor ON expression
    ;

joinType
    : INNER
    | LEFT
    | RIGHT
    | FULL
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

// ________________ Expressions & Predicates ________________

expression
    : '(' expression ')'
    | expression AND expression
    | expression OR expression
    | NOT expression
    | predicate
    ;

predicate
    : expression comparisonOperator expression
    | functionCall
    | literal
    | IDENTIFIER
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
    : (COUNT|SUM|AVG|MIN|MAX) '(' (STAR | expression) ')' (OVER '(' windowSpec ')')?
    ;

windowFunction
    : (ROW_NUMBER|RANK|DENSE_RANK|NTILE) '(' ')' OVER '(' windowSpec ')'
    ;

userFunction
    : IDENTIFIER '(' (expression (COMMA expression)*)? ')'
    ;

windowSpec
    : (PARTITION BY expression (COMMA expression)*)? (ORDER BY orderExpression (COMMA orderExpression)*)?
    ;

// ________________ Literals ________________

literal
    : STRING
    | NUMBER
    ;

// ________________ Security Statements ________________

grantStatement
    : GRANT IDENTIFIER ON IDENTIFIER TO IDENTIFIER
    ;

revokeStatement
    : REVOKE IDENTIFIER ON IDENTIFIER FROM IDENTIFIER
    ;

denyStatement
    : DENY IDENTIFIER ON IDENTIFIER TO IDENTIFIER
    ;

// ________________ Transaction Control ________________

transactionStatement
    : BEGIN_TRANSACTION
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
    : CASE whenClause+ ELSE expression? END
    ;

whenClause
    : WHEN expression THEN expression
    ;

ifStatement
    : IF '(' expression ')' block
    ;

whileStatement
    : WHILE '(' expression ')' block
    ;

returnStatement
    : RETURN expression?
    ;

breakStatement
    : BREAK
    ;

continueStatement
    : CONTINUE
    ;

block
    : '{' statement* '}'
    ;
