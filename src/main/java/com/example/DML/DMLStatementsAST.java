package com.example.DML;

import com.example.ASTNode;

public class DMLStatementsAST {
    // ========================= Elias ==========================
    // ==================== INSERT Statement ====================

    // INSERT node
    public class InsertStatementNode extends ASTNode {
        private boolean hasColumnList;
        private boolean isFromSelect;

        public InsertStatementNode(boolean hasColumnList, boolean isFromSelect) {
            this.hasColumnList = hasColumnList;
            this.isFromSelect = isFromSelect;
        }

        @Override
        public String getNodeType() {
            String type = "INSERT";
            if (isFromSelect) {
                type += " (from SELECT)";
            }
            return type;
        }
    }

    // Column list node
    public class ColumnListNode extends ASTNode {

        @Override
        public String getNodeType() {
            return "ColumnList";
        }
    }

    // Value list node
    public class ValuesListNode extends ASTNode {

        @Override
        public String getNodeType() {
            return "VALUES";
        }
    }

    // Value row node
    public class ValueRowNode extends ASTNode {

        @Override
        public String getNodeType() {
            return "ValueRow";
        }
    }

    // ==================== UPDATE Statement ====================

    // UPDATE node
    public class UpdateStatementNode extends ASTNode {

        @Override
        public String getNodeType() {
            return "UPDATE";
        }
    }

    // SET node
    public class SetClauseNode extends ASTNode {

        @Override
        public String getNodeType() {
            return "SET";
        }
    }

    // ASSIGNMENT Node
    public class AssignmentNode extends ASTNode {
        private String columnName;
        private String operator; // "=", "+=", "-=", etc.

        public AssignmentNode(String columnName, String operator) {
            this.columnName = columnName;
            this.operator = operator;
        }

        public String getColumnName() {
            return columnName;
        }

        public String getOperator() {
            return operator;
        }

        @Override
        public String getNodeType() {
            return "Assignment: " + columnName + " " + operator;
        }
    }

    // ==================== DELETE Statement ====================

    // DELETE
    public class DeleteStatementNode extends ASTNode {

        @Override
        public String getNodeType() {
            return "DELETE";
        }
    }

    // ==================== MERGE Statement ====================

    // MERGE
    public class MergeStatementNode extends ASTNode {

        @Override
        public String getNodeType() {
            return "MERGE";
        }
    }

    // USING in MERGE
    public class UsingClauseNode extends ASTNode {

        @Override
        public String getNodeType() {
            return "USING";
        }
    }

    // ON in MERGE
    public class OnClauseNode extends ASTNode {

        @Override
        public String getNodeType() {
            return "ON";
        }
    }

    // WHEN in MERGE
    public class WhenClauseNode extends ASTNode {
        private boolean isMatched;
        private boolean hasCondition;

        public WhenClauseNode(boolean isMatched, boolean hasCondition) {
            this.isMatched = isMatched;
            this.hasCondition = hasCondition;
        }

        @Override
        public String getNodeType() {
            String type = "WHEN ";
            type += isMatched ? "MATCHED" : "NOT MATCHED";
            if (hasCondition) {
                type += " (with condition)";
            }
            return type;
        }
    }

    // MERGE (UPDATE/DELETE/INSERT)
    public class MergeActionNode extends ASTNode {
        private String actionType; // "UPDATE", "DELETE", "INSERT"

        public MergeActionNode(String actionType) {
            this.actionType = actionType;
        }

        @Override
        public String getNodeType() {
            return "Action: " + actionType;
        }
    }
}
