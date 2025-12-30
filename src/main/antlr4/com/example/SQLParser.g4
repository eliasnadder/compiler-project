parser grammar SQLParser;

options {
    tokenVocab = SQLLexer;
}


sqlScript
    : selectStatement* EOF
    ;



selectStatement
    : SELECT selectList fromClause whereClause?
    ;

selectList
    : STAR
    | selectItem (COMMA selectItem)*
    ;

selectItem
    : IDENTIFIER
    ;


fromClause
    : FROM tableSource
    ;

tableSource
    : tableFactor joinClause*
    ;

tableFactor
    : IDENTIFIER
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


expression
    : expression AND expression
    | expression OR expression
    | predicate
    ;

predicate
    : IDENTIFIER comparisonOperator IDENTIFIER
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