package com.example.DML;

import com.example.ASTNode;

public class DMLStatementsAST {
    // ========================= Bshr ==========================
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
    // ==================== SELECT Statement ====================

    public class SelectStatementNode extends ASTNode {

        @Override
        public String getNodeType() {
            return "SELECT";
        }
    }
    public class SelectItemNode extends ASTNode {
    private boolean hasAlias;

    public SelectItemNode(boolean hasAlias) {
        this.hasAlias = hasAlias;
    }

    @Override
    public String getNodeType() {
        return hasAlias ? "SelectItem (with alias)" : "SelectItem";
    }
    }

    public class FromClauseNode extends ASTNode {

        @Override
        public String getNodeType() {
            return "FROM";
        }
    }
    public class WhereClauseNode extends ASTNode {

        @Override
        public String getNodeType() {
            return "WHERE";
        }
    }
    public class OrderByClauseNode extends ASTNode {

        @Override
        public String getNodeType() {
            return "ORDER BY";
        }
    }
    public class OrderItemNode extends ASTNode {
        private boolean isAsc;

        public OrderItemNode(boolean isAsc) {
            this.isAsc = isAsc;
        }

        @Override
        public String getNodeType() {
            return isAsc ? "OrderItem ASC" : "OrderItem DESC";
        }
    }

    public class GroupByClauseNode extends ASTNode {
        @Override
        public String getNodeType() {
            return "GROUP BY";
        }
    }
    public class HavingClauseNode extends ASTNode {
        @Override
        public String getNodeType() {
            return "HAVING";
        }
    }
    public class JoinClauseNode extends ASTNode {
        private String joinType; // INNER, LEFT, RIGHT, FULL, CROSS

        public JoinClauseNode(String joinType) {
            this.joinType = joinType;
        }

        @Override
        public String getNodeType() {
            return "JOIN (" + joinType + ")";
        }
    }
    public class TableAliasNode extends ASTNode {
        private String alias;

        public TableAliasNode(String alias) {
            this.alias = alias;
        }

        @Override
        public String getNodeType() {
            return "TableAlias: " + alias;
        }
    }

}
