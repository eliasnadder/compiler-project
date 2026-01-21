package com.example;

import com.example.DML.*;
import com.example.Expressions.*;

import java.util.*;

public class AntlrVisitor extends SQLParserBaseVisitor<ASTNode> {

    // ================ ENTRY ================

    @Override
    public ASTNode visitSqlScript(SQLParser.SqlScriptContext ctx) {
        List<ASTNode> statements = new ArrayList<>();
        if (ctx.statementList() != null) {
            for (SQLParser.SingleStatementContext stmt : ctx.statementList().singleStatement()) {
                ASTNode node = visit(stmt);
                if (node != null)
                    statements.add(node);
            }
        }
        return new ASTNodeList(statements);
    }

    @Override
    public ASTNode visitSingleStatement(SQLParser.SingleStatementContext ctx) {
        return visit(ctx.statement());
    }

    // ================ DML STATEMENTS ================

    // ----------- SELECT -----------
    @Override
    public ASTNode visitSelectStatement(SQLParser.SelectStatementContext ctx) {
        List<ExpressionNode> selectList = new ArrayList<>();
        if (ctx.selectList().STAR() != null) {
            selectList.add(new ColumnNode("*"));
        } else {
            for (SQLParser.SelectItemContext item : ctx.selectList().selectItem()) {
                selectList.add((ExpressionNode) visit(item.expression()));
            }
        }

        // FROM clause (only first tableFactor as table source)
        String table = null;

        if (ctx.fromClause() != null) {
            SQLParser.TableFactorContext tf = ctx.fromClause().tableSource().tableFactor();
            if (tf != null && tf.qualifiedName() != null) {
                table = tf.qualifiedName().getText();
            }
        }

        ExpressionNode where = null;
        if (ctx.whereClause() != null) {
            where = (ExpressionNode) visit(ctx.whereClause().expression());
        }

        // Group By
        List<ExpressionNode> groupBy = null;
        if (ctx.groupByClause() != null) {
            groupBy = new ArrayList<>();
            for (SQLParser.ExpressionContext e : ctx.groupByClause().expression()) {
                groupBy.add((ExpressionNode) visit(e));
            }
        }

        // Having
        ExpressionNode having = null;
        if (ctx.havingClause() != null) {
            having = (ExpressionNode) visit(ctx.havingClause().expression());
        }

        // Order By
        List<ExpressionNode> orderBy = null;
        if (ctx.orderByClause() != null) {
            orderBy = new ArrayList<>();
            for (SQLParser.OrderExpressionContext oec : ctx.orderByClause().orderExpression()) {
                orderBy.add((ExpressionNode) visit(oec.expression()));
            }
        }

        return new SelectNode(selectList, where, groupBy, having, orderBy);
    }

    // ----------- INSERT -----------
    @Override
    public ASTNode visitInsertStatement(SQLParser.InsertStatementContext ctx) {
        // Table name
        String table = ctx.qualifiedName().getText();

        // Columns list
        List<String> columns = null;
        if (ctx.LPAREN() != null && ctx.IDENTIFIER().size() > 0) {
            columns = new ArrayList<>();

            int qualifiedNameIndex = -1;
            for (int i = 0; i < ctx.getChildCount(); i++) {
                if (ctx.getChild(i).equals(ctx.qualifiedName())) {
                    qualifiedNameIndex = i;
                    break;
                }
            }
            if (qualifiedNameIndex >= 0 && qualifiedNameIndex + 1 < ctx.getChildCount()) {
                // The child right after qualifiedName should be '(' for columns list if present
                if ("(".equals(ctx.getChild(qualifiedNameIndex + 1).getText())) {
                    for (int i = qualifiedNameIndex + 2; i < ctx.getChildCount(); i++) {
                        String txt = ctx.getChild(i).getText();
                        if (")".equals(txt))
                            break;
                        if (!",".equals(txt))
                            columns.add(txt);
                    }
                }
            }
        }

        // Values - multiple rows possible
        List<List<ExpressionNode>> rows = new ArrayList<>();

        if (ctx.VALUES() != null) {
            // Parse multiple rows of values: VALUES (expr, expr, ...), (expr, expr, ...)
            for (int i = 0; i < ctx.getChildCount(); i++) {
                if ("(".equals(ctx.getChild(i).getText())) {
                    List<ExpressionNode> row = new ArrayList<>();
                    int j = i + 1;
                    while (j < ctx.getChildCount() && !")".equals(ctx.getChild(j).getText())) {
                        if (ctx.getChild(j) instanceof SQLParser.ExpressionContext) {
                            row.add((ExpressionNode) visit(ctx.getChild(j)));
                        }
                        j++;
                    }
                    if (!row.isEmpty())
                        rows.add(row);
                }
            }
        } else if (ctx.selectStatement() != null) {
            // Insert from subquery
            List<ExpressionNode> subquery = new ArrayList<>();
            subquery.add((ExpressionNode) visit(ctx.selectStatement()));
            rows.add(subquery);
        }

        return new InsertNode(table, columns, rows);
    }

    // ----------- UPDATE -----------
    @Override
    public ASTNode visitUpdateStatement(SQLParser.UpdateStatementContext ctx) {
        String table = ctx.qualifiedName().getText();

        // Map<String, ExpressionNode> assignments
        Map<String, ExpressionNode> assignments = new LinkedHashMap<>();
        for (SQLParser.UpdateAssignmentContext ua : ctx.updateAssignment()) {
            UpdateAssignmentNode assignNode = (UpdateAssignmentNode) visit(ua);
            assignments.put(assignNode.getColumn(), assignNode.getValue());
        }

        ExpressionNode where = null;
        if (ctx.whereClause() != null) {
            where = (ExpressionNode) visit(ctx.whereClause().expression());
        }

        return new UpdateNode(table, assignments, where);
    }

    // ----------- DELETE -----------
    @Override
    public ASTNode visitDeleteStatement(SQLParser.DeleteStatementContext ctx) {
        String table = ctx.qualifiedName().getText();

        ExpressionNode where = null;
        if (ctx.whereClause() != null) {
            where = (ExpressionNode) visit(ctx.whereClause().expression());
        }

        return new DeleteNode(table, where);
    }

    // ----------- MERGE (simplified) -----------
    @Override
    public ASTNode visitMergeStatement(SQLParser.MergeStatementContext ctx) {
        String table = ctx.qualifiedName().getText();

        DMLStatementsNode usingSource = (DMLStatementsNode) visit(ctx.tableSource());

        ExpressionNode onCond = (ExpressionNode) visit(ctx.expression());

        List<DMLStatementsNode> whenMatchedActions = new ArrayList<>();
        List<DMLStatementsNode> whenNotMatchedActions = new ArrayList<>();

        for (SQLParser.WhenClauseMergeContext wcm : ctx.whenClauseMerge()) {
            if (wcm.WHEN().getText().equalsIgnoreCase("WHEN")) {
                boolean matched = wcm.MATCHED() != null;
                // Visit mergeAction
                DMLStatementsNode action = (DMLStatementsNode) visit(wcm.mergeAction());

                if (matched) {
                    whenMatchedActions.add(action);
                } else {
                    whenNotMatchedActions.add(action);
                }
            }
        }

        return new MergeNode(table, usingSource, onCond, whenMatchedActions, whenNotMatchedActions);
    }

    // ----------- UPDATE ASSIGNMENT -----------
    @Override
    public ASTNode visitUpdateAssignment(SQLParser.UpdateAssignmentContext ctx) {
        String column = ctx.IDENTIFIER().getText();
        String operator = ctx.getChild(1).getText();
        ExpressionNode value = (ExpressionNode) visit(ctx.expression());
        return new UpdateAssignmentNode(column, operator, value);
    }

    /// ================ EXPRESSIONS ================

    // ================ EXPRESSIONS ================

    @Override
    public ASTNode visitExpression(SQLParser.ExpressionContext ctx) {
        // -- CAST --
        if (ctx.CAST() != null) {
            // CAST LPAREN expression AS datatype RPAREN
            ExpressionNode expr = (ExpressionNode) visit(ctx.expression(0));
            String datatype = ctx.datatype().getText();
            return new CastExpressionNode(expr, datatype);
        }

        // -- CASE --
        if (ctx.CASE() != null) {
            List<CaseExpressionNode.WhenClause> whenClauses = new ArrayList<>();

            for (SQLParser.WhenClauseContext wctx : ctx.whenClause()) {
                ExpressionNode cond = (ExpressionNode) visit(wctx.expression(0));
                ExpressionNode res = (ExpressionNode) visit(wctx.expression(1));

                whenClauses.add(new CaseExpressionNode.WhenClause(cond, res));
            }

            ExpressionNode elseExpr = null;
            if (ctx.ELSE() != null) {
                elseExpr = (ExpressionNode) visit(ctx.expression(ctx.expression().size() - 1));
            }

            return new CaseExpressionNode(whenClauses, elseExpr);
        }

        // -- NOT expr --
        if (ctx.NOT() != null && ctx.expression().size() == 1) {
            return new UnaryExpressionNode("NOT", (ExpressionNode) visit(ctx.expression(0)));
        }

        // -- IS NULL / IS NOT NULL --
        if (ctx.IS() != null && ctx.NULL() != null) {
            boolean not = ctx.NOT() != null;
            return new UnaryExpressionNode(not ? "IS NOT NULL" : "IS NULL",
                    (ExpressionNode) visit(ctx.expression(0)));
        }

        // -- BETWEEN / NOT BETWEEN --
        if (ctx.BETWEEN() != null) {
            boolean not = ctx.NOT() != null;
            ExpressionNode expr = (ExpressionNode) visit(ctx.expression(0));
            ExpressionNode start = (ExpressionNode) visit(ctx.expression(1));
            ExpressionNode end = (ExpressionNode) visit(ctx.expression(2));
            return new BetweenExpressionNode(expr, start, end, not);
        }

        // -- IN / NOT IN --
        if (ctx.IN() != null) {
            boolean not = ctx.NOT() != null;
            ExpressionNode expr = (ExpressionNode) visit(ctx.expression(0));
            List<ExpressionNode> list;

            if (ctx.selectStatement() != null) {
                // Subquery in IN
                list = new ArrayList<>();
                list.add((ExpressionNode) visit(ctx.selectStatement()));
            } else if (ctx.expressionList() != null) {
                list = new ArrayList<>();
                for (SQLParser.ExpressionContext e : ctx.expressionList().expression()) {
                    list.add((ExpressionNode) visit(e));
                }
            } else {
                list = Collections.emptyList();
            }
            return new InExpressionNode(expr, list, not);
        }

        // -- LIKE / NOT LIKE --
        if (ctx.LIKE() != null) {
            boolean not = ctx.NOT() != null;
            ExpressionNode expr = (ExpressionNode) visit(ctx.expression(0));
            ExpressionNode pattern = (ExpressionNode) visit(ctx.expression(1));
            ExpressionNode escape = null;
            if (ctx.ESCAPE() != null && ctx.expression().size() > 2) {
                escape = (ExpressionNode) visit(ctx.expression(2));
            }
            return new LikeExpressionNode(expr, pattern, escape, not);
        }

        // -- EXISTS / NOT EXISTS --
        if (ctx.EXISTS() != null) {
            boolean not = ctx.NOT() != null;
            ASTNode subquery = visit(ctx.selectStatement());
            return new ExistsExpressionNode(subquery, not);
        }

        // -- Function Calls --
        if (ctx.functionCall() != null) {
            return visit(ctx.functionCall());
        }

        // -- Parentheses (expr) --
        if (ctx.LPAREN() != null && ctx.RPAREN() != null && ctx.expression().size() == 1) {
            return visit(ctx.expression(0));
        }

        // -- Literal --
        if (ctx.literal() != null) {
            return visit(ctx.literal());
        }

        // -- QualifiedName (columns) --
        if (ctx.qualifiedName() != null) {
            return new ColumnNode(ctx.qualifiedName().getText());
        }

        int count = ctx.getChildCount();

        // -- Binary comparison operators: expr op expr --
        if (count == 3 && ctx.comparisonOperator() != null) {
            return new BinaryExpressionNode(
                    (ExpressionNode) visit(ctx.expression(0)),
                    ctx.comparisonOperator().getText(),
                    (ExpressionNode) visit(ctx.expression(1)));
        }

        // -- Arithmetic operators +, -, *, /, % --
        if (count == 3 && (ctx.PLUS() != null || ctx.MINUS_OP() != null ||
                ctx.STAR() != null || ctx.DIV() != null || ctx.MOD() != null)) {
            return new BinaryExpressionNode(
                    (ExpressionNode) visit(ctx.expression(0)),
                    ctx.getChild(1).getText(),
                    (ExpressionNode) visit(ctx.expression(1)));
        }

        // -- AND / OR logical ops --
        if (count == 3 && (ctx.AND() != null || ctx.OR() != null)) {
            return new BinaryExpressionNode(
                    (ExpressionNode) visit(ctx.expression(0)),
                    ctx.getChild(1).getText(),
                    (ExpressionNode) visit(ctx.expression(1)));
        }

        // -- Fallback to children --
        return visitChildren(ctx);
    }

    // ================ Literals ================

    @Override
    public ASTNode visitLiteral(SQLParser.LiteralContext ctx) {
        String text = ctx.getText();

        // Try to parse to corresponding Java types if needed
        if (ctx.INT_LITERAL() != null) {
            return new LiteralNode(Integer.parseInt(text));
        }
        if (ctx.FLOAT_LITERAL() != null) {
            return new LiteralNode(Double.parseDouble(text));
        }
        if (ctx.STRING_LITERAL() != null) {
            // Remove quotes if needed
            String val = text.substring(1, text.length() - 1);
            return new LiteralNode(val);
        }
        if (ctx.TRUE() != null) {
            return new LiteralNode(true);
        }
        if (ctx.FALSE() != null) {
            return new LiteralNode(false);
        }
        if (ctx.NULL() != null) {
            return new NullNode();
        }

        // Default fallback
        return new LiteralNode(text);
    }

    // ================ Function Calls ================

    @Override
    public ASTNode visitFunctionCall(SQLParser.FunctionCallContext ctx) {
        String functionName = null;
        List<ExpressionNode> args = new ArrayList<>();

        if (ctx.systemFunction() != null) {
            functionName = ctx.systemFunction().getText();
        } else if (ctx.aggregateFunction() != null) {
            functionName = ctx.aggregateFunction().getChild(0).getText(); // COUNT, SUM, etc.

            if (ctx.aggregateFunction().STAR() != null) {
                args.add(new ColumnNode("*"));
            } else if (ctx.aggregateFunction().expression() != null) {
                args.add((ExpressionNode) visit(ctx.aggregateFunction().expression()));
            }
        } else if (ctx.userFunction() != null) {
            functionName = ctx.userFunction().IDENTIFIER().getText();
            if (ctx.userFunction().expression() != null) {
                for (SQLParser.ExpressionContext e : ctx.userFunction().expression()) {
                    args.add((ExpressionNode) visit(e));
                }
            }
        } else if (ctx.windowFunction() != null) {
            functionName = ctx.windowFunction().getChild(0).getText();
        }

        return new FunctionCallNode(functionName, args);
    }

    // ================ Default fallback ================

    @Override
    protected ASTNode defaultResult() {
        return null;
    }

}
