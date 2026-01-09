lexer grammar SQLLexer;

// ===================== Case-Insensitive Fragments =====================
fragment A: [Aa];
fragment B: [Bb];
fragment C: [Cc];
fragment D: [Dd];
fragment E: [Ee];
fragment F: [Ff];
fragment G: [Gg];
fragment H: [Hh];
fragment I: [Ii];
fragment J: [Jj];
fragment K: [Kk];
fragment L: [Ll];
fragment M: [Mm];
fragment N: [Nn];
fragment O: [Oo];
fragment P: [Pp];
fragment Q: [Qq];
fragment R: [Rr];
fragment S: [Ss];
fragment T: [Tt];
fragment U: [Uu];
fragment V: [Vv];
fragment W: [Ww];
fragment X: [Xx];
fragment Y: [Yy];
fragment Z: [Zz];

// ===================== DML Keywords =====================
SELECT: S E L E C T;
INSERT: I N S E R T;
UPDATE: U P D A T E;
DELETE: D E L E T E;
MERGE: M E R G E;
REPLACE: R E P L A C E;
UPSERT: U P S E R T;
CALL: C A L L;
DO: D O;

// ===================== DDL & Objects =====================
CREATE: C R E A T E;
ALTER: A L T E R;
DROP: D R O P;
MODIFY: M O D I F Y;
CHANGE: C H A N G E;
RENAME: R E N A M E;
TRUNCATE: T R U N C A T E;
TABLE: T A B L E;
COLUMN: C O L U M N;
VIEW: V I E W;
INDEX: I N D E X;
CONSTRAINT: C O N S T R A I N T;
PRIMARY: P R I M A R Y;
FOREIGN: F O R E I G N;
KEY: K E Y;
UNIQUE: U N I Q U E;
CHECK: C H E C K;
DEFAULT: D E F A U L T;
REFERENCES: R E F E R E N C E S;
CASCADE: C A S C A D E;
RESTRICT: R E S T R I C T;
RESTART: R E S T A R T;
IDENTITY: I D E N T I T Y;
NOT: N O T;
NULL: N U L L;
SCHEMA: S C H E M A;
DATABASE: D A T A B A S E;
DOMAIN: D O M A I N;
TYPE: T Y P E;
SEQUENCE: S E Q U E N C E;
TRIGGER: T R I G G E R;
PROCEDURE: P R O C E D U R E;
FUNCTION: F U N C T I O N;
PACKAGE: P A C K A G E;
BODY: B O D Y;
ADD: A D D;
IF_EXISTS: I F '_' E X I S T S;
IF_NOT_EXISTS: I F '_' N O T '_' E X I S T S;

// ===================== Access Control (DCL) =====================
GRANT: G R A N T;
REVOKE: R E V O K E;
DENY: D E N Y;
ROLE: R O L E;
ADMIN: A D M I N;
PUBLIC: P U B L I C;

// ===================== Transaction (TCL) =====================
BEGIN: B E G I N;
COMMIT: C O M M I T;
ROLLBACK: R O L L B A C K;
TRANSACTION: T R A N S A C T I O N;
SAVEPOINT: S A V E P O I N T;
SET: S E T;
LOCK: L O C K;
WAIT: W A I T;
NOWAIT: N O W A I T;
ISOLATION: I S O L A T I O N;
LEVEL: L E V E L;
TRY: T R Y;
CATCH: C A T C H;
GO: G O;

// ===================== Joins =====================
JOIN: J O I N;
INNER: I N N E R;
LEFT: L E F T;
RIGHT: R I G H T;
FULL: F U L L;
OUTER: O U T E R;
CROSS: C R O S S;
UNION: U N I O N;
INTERSECT: I N T E R S E C T;
EXCEPT: E X C E P T;
MINUS: M I N U S;
NATURAL: N A T U R A L;
APPLY: A P P L Y;

// ===================== Clauses =====================
FROM: F R O M;
WHERE: W H E R E;
GROUP: G R O U P;
BY: B Y;
HAVING: H A V I N G;
ORDER: O R D E R;
ASC: A S C;
TOP: T O P;
VALUES: V A L U E S;
INTO: I N T O;
DISTINCT: D I S T I N C T;
AS: A S;
ON: O N;
USING: U S I N G;
LIMIT: L I M I T;
OFFSET: O F F S E T;
FETCH: F E T C H;
NEXT: N E X T;
ONLY: O N L Y;
WITH: W I T H;
RECURSIVE: R E C U R S I V E;
LATERAL: L A T E R A L;
COLLATE: C O L L A T E;
WINDOW: W I N D O W;
PARTITION: P A R T I T I O N;
OVER: O V E R;
ROWS: R O W S;
RANGE: R A N G E;
GROUPS: G R O U P S;
UNBOUNDED: U N B O U N D E D;
PRECEDING: P R E C E D I N G;
FOLLOWING: F O L L O W I N G;
CURRENT: C U R R E N T;
ROW: R O W;
FILTER: F I L T E R;
WITHIN: W I T H I N; 
MATCHED: M A T C H E D; //! Updated 

// ===================== Logical Operators =====================
AND: A N D;
OR: O R;
XOR: X O R;
IN: I N;
BETWEEN: B E T W E E N;
LIKE: L I K E;
ILIKE: I L I K E;
SIMILAR: S I M I L A R;
RLIKE: R L I K E;
REGEXP: R E G E X P;
IS: I S;
EXISTS: E X I S T S;
ALL: A L L;
ANY: A N Y;
SOME: S O M E;
NULLS: N U L L S;
FIRST: F I R S T;
LAST: L A S T;
ESCAPE: E S C A P E;

TO: T O; //! Updated

NOT_IN: N O T '_' I N;
NOT_EXISTS: N O T '_' E X I S T S;
IS_NULL: I S '_' N U L L;
IS_NOT_NULL: I S '_' N O T '_' N U L L;

// ===================== Control Flow =====================
CASE: C A S E;
WHEN: W H E N;
THEN: T H E N;
ELSE: E L S E;
END: E N D;
IF: I F;
WHILE: W H I L E;
RETURN: R E T U R N;
BREAK: B R E A K;
CONTINUE: C O N T I N U E;
GOTO: G O T O;
DECLARE: D E C L A R E;
EXEC: E X E C;
EXECUTE: E X E C U T E;

// ===================== System Functions & Vars =====================
USER: U S E R;
SESSION_USER: S E S S I O N '_' U S E R;
SYSTEM_USER: S Y S T E M '_' U S E R;
CURRENT_USER: C U R R E N T '_' U S E R;
CURRENT_DATE: C U R R E N T '_' D A T E;
CURRENT_TIME: C U R R E N T '_' T I M E;
CURRENT_TIMESTAMP: C U R R E N T '_' T I M E S T A M P;
SYSDATE: S Y S D A T E;
COALESCE: C O A L E S C E;
NULLIF: N U L L I F;
GREATEST: G R E A T E S T;
LEAST: L E A S T;
CAST: C A S T;
CONVERT: C O N V E R T;
EXTRACT: E X T R A C T;
POSITION: P O S I T I O N;
SUBSTRING: S U B S T R I N G;
TRIM: T R I M;

// ===================== Aggregate Functions =====================
COUNT: C O U N T;
SUM: S U M;
AVG: A V G;
MIN: M I N;
MAX: M A X;
STDDEV: S T D D E V;
VARIANCE: V A R I A N C E;
STRING_AGG: S T R I N G '_' A G G;
GROUP_CONCAT: G R O U P '_' C O N C A T;
ARRAY_AGG: A R R A Y '_' A G G;

// ===================== Window Functions =====================
ROW_NUMBER: R O W '_' N U M B E R;
RANK: R A N K;
DENSE_RANK: D E N S E '_' R A N K;
LAG: L A G;
LEAD: L E A D;
FIRST_VALUE: F I R S T '_' V A L U E;
LAST_VALUE: L A S T '_' V A L U E;
NTILE: N T I L E;
PERCENT_RANK: P E R C E N T '_' R A N K;
CUME_DIST: C U M E '_' D I S T;
NTH_VALUE: N T H '_' V A L U E;

// ===================== Auxiliary =====================
SHOW: S H O W;
DESCRIBE: D E S C R I B E;
DESC: D E S C;
EXPLAIN: E X P L A I N;
ANALYZE: A N A L Y Z E;
USE: U S E;
PRAGMA: P R A G M A;

// ===================== Data Types =====================
INT: I N T;
INTEGER: I N T E G E R;
BIGINT: B I G I N T;
SMALLINT: S M A L L I N T;
TINYINT: T I N Y I N T;
DECIMAL: D E C I M A L;
NUMERIC: N U M E R I C;
FLOAT: F L O A T;
REAL: R E A L;
DOUBLE: D O U B L E;
PRECISION: P R E C I S I O N;
CHAR: C H A R;
VARCHAR: V A R C H A R;
VARCHAR2: V A R C H A R '2';
NVARCHAR: N V A R C H A R;
NCHAR: N C H A R;
TEXT: T E X T;
DATE: D A T E;
TIME: T I M E;
DATETIME: D A T E T I M E;
TIMESTAMP: T I M E S T A M P;
INTERVAL: I N T E R V A L;
BOOLEAN: B O O L E A N;
BOOL: B O O L;
BLOB: B L O B;
CLOB: C L O B;
JSON: J S O N;
JSONB: J S O N B;
XML: X M L;
UUID: U U I D;
MONEY: M O N E Y;
BIT: B I T;
VARBIT: V A R B I T;
BINARY: B I N A R Y;
VARBINARY: V A R B I N A R Y;
IMAGE: I M A G E;
ENUM: E N U M;
ARRAY: A R R A Y;
STRUCT: S T R U C T;
MAP: M A P;

// ===================== Time Units =====================
YEAR: Y E A R;
MONTH: M O N T H;
DAY: D A Y;
HOUR: H O U R;
MINUTE: M I N U T E;
SECOND: S E C O N D;
ZONE: Z O N E;
LOCAL: L O C A L;
AT: A T;

// ===================== Boolean Literals =====================
TRUE: T R U E;
FALSE: F A L S E;
UNKNOWN: U N K N O W N;

// ===================== Numbers =====================
FLOAT_LITERAL:
	[0-9]+ '.' [0-9]* ([eE] [+-]? [0-9]+)?
	| '.' [0-9]+ ([eE] [+-]? [0-9]+)
	| [0-9]+ [eE] [+-]? [0-9]+;

INT_LITERAL: [0-9]+;

HEX_LITERAL: '0' [xX] [0-9a-fA-F]+;

// ===================== Strings & Bytes =====================
STRING_LITERAL: '\'' ( ~'\'' | '\'\'')* '\'';
UNICODE_STRING: [nN] '\'' ( ~'\'' | '\'\'')* '\'';
HEX_STRING: [xX] '\'' [0-9a-fA-F]* '\'';
BIT_STRING: [bB] '\'' [01]* '\'';

// ===================== Identifiers & Variables =====================
IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_]*;
QUOTED_IDENTIFIER: '"' (~'"' | '""')* '"';
BACKTICK_IDENTIFIER: '`' (~'`' | '``')* '`';
BRACKET_IDENTIFIER: '[' (~']')* ']';

// Variables
SYSTEM_VARIABLE: '@@' [a-zA-Z_][a-zA-Z0-9_]*;
LOCAL_VARIABLE: '@' [a-zA-Z_][a-zA-Z0-9_]*;

// ===================== Operators =====================
EQ: '=';
NEQ: '!=' | '<>' | '^=';
GE: '>=';
LE: '<=';
GT: '>';
LT: '<';
PLUS: '+';
MINUS_OP: '-';
STAR: '*';
DIV: '/';
MOD: '%';
CONCAT_PIPE: '||';
BIT_AND: '&';
BIT_OR: '|';
BIT_XOR: '^';
BIT_NOT: '~';
LSHIFT: '<<';
RSHIFT: '>>';

PLUS_EQ  : '+=';
MINUS_EQ : '-=';
MULT_EQ  : '*=';
DIV_EQ   : '/=';
MOD_EQ   : '%=';

COMMA: ',';
SEMICOLON: ';';
DOT: '.';
LPAREN: '(';
RPAREN: ')';
LBRACKET: '[';
RBRACKET: ']';
LBRACE: '{';
RBRACE: '}';

// ===================== Comments & Whitespace =====================
WS: [ \t\r\n]+ -> skip;
LINE_COMMENT: '--' ~[\r\n]* -> skip;
// BLOCK_COMMENT: '/*' .*? '*/' -> skip;
//! Updated to support nested block comments
BLOCK_COMMENT_START: '/*' -> pushMode(BLOCK_COMMENT_MODE), skip;

mode BLOCK_COMMENT_MODE;

BLOCK_COMMENT_NESTED: '/*' -> pushMode(BLOCK_COMMENT_MODE), skip;
BLOCK_COMMENT_END: '*/' -> popMode, skip;
BLOCK_COMMENT_CONTENT: . -> skip;

HASH_COMMENT: '#' ~[\r\n]* -> skip; 