package com.example;

import com.example.DML.*;
import com.example.Expressions.*;
import com.example.Expressions.CaseExpressionNode;
import com.example.DDL.Create.*;
import com.example.DDL.Alter.*;
import com.example.DDL.Drop.*;
import com.example.DDL.Truncate.*;
import com.example.CONTROL.*;
import com.example.TRANSACTION.*;

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

    @Override
    public ASTNode visitStatement(SQLParser.StatementContext ctx) {
        // زيارة WITH clause إذا وجدت
        if (ctx.withClause() != null) {
            // يمكن إضافة معالجة CTE هنا إذا لزم الأمر
        }
        return visit(ctx.coreStatement());
    }

    // ================ DML STATEMENTS ================

    // ----------- SELECT -----------
    @Override
    public ASTNode visitSelectStatement(SQLParser.SelectStatementContext ctx) {
        List<ExpressionNode> selectList = new ArrayList<>();

        // معالجة SELECT list
        if (ctx.selectList().STAR() != null) {
            selectList.add(new ColumnNode("*"));
        } else {
            for (SQLParser.SelectItemContext item : ctx.selectList().selectItem()) {
                selectList.add((ExpressionNode) visit(item.expression()));
            }
        }

        // FROM clause
        String table = null;
        if (ctx.fromClause() != null) {
            SQLParser.TableFactorContext tf = ctx.fromClause().tableSource().tableFactor();
            if (tf != null && tf.qualifiedName() != null) {
                table = tf.qualifiedName().getText();
            }
        }

        // WHERE clause
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
        String table = ctx.qualifiedName().getText();

        // معالجة الأعمدة
        List<String> columns = new ArrayList<>();
        for (int i = 0; i < ctx.IDENTIFIER().size(); i++) {
            columns.add(ctx.IDENTIFIER(i).getText());
        }

        // معالجة القيم
        List<List<ExpressionNode>> rows = new ArrayList<>();
        if (ctx.VALUES() != null) {
            // حساب عدد مجموعات القيم
            for (int i = 0; i < ctx.getChildCount(); i++) {
                if ("VALUES".equals(ctx.getChild(i).getText())) {
                    // البحث عن كل مجموعة قيم بين ()
                    for (int j = i + 1; j < ctx.getChildCount(); j++) {
                        if ("(".equals(ctx.getChild(j).getText())) {
                            List<ExpressionNode> row = new ArrayList<>();
                            int k = j + 1;
                            while (k < ctx.getChildCount() && !")".equals(ctx.getChild(k).getText())) {
                                if (ctx.getChild(k) instanceof SQLParser.ExpressionContext) {
                                    row.add((ExpressionNode) visit(ctx.getChild(k)));
                                }
                                k++;
                            }
                            if (!row.isEmpty()) {
                                rows.add(row);
                            }
                        }
                    }
                    break;
                }
            }
        } else if (ctx.selectStatement() != null) {
            // INSERT ... SELECT - نترك القائمة فارغة ونعالج SELECT بشكل منفصل
            // في هذه الحالة، يمكن تحسين InsertNode لدعم subquery
            // لكن حالياً سنتجاهلها أو نضع placeholder
            rows = new ArrayList<>(); // قائمة فارغة للـ INSERT...SELECT
        }

        return new InsertNode(table, columns.isEmpty() ? null : columns, rows);
    }

    // ----------- UPDATE -----------
    @Override
    public ASTNode visitUpdateStatement(SQLParser.UpdateStatementContext ctx) {
        String table = ctx.qualifiedName().getText();

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

    // ----------- MERGE -----------
    @Override
    public ASTNode visitMergeStatement(SQLParser.MergeStatementContext ctx) {
        String table = ctx.qualifiedName().getText();
        DMLStatementsNode usingSource = (DMLStatementsNode) visit(ctx.tableSource());
        ExpressionNode onCond = (ExpressionNode) visit(ctx.expression());

        List<DMLStatementsNode> whenMatchedActions = new ArrayList<>();
        List<DMLStatementsNode> whenNotMatchedActions = new ArrayList<>();

        for (SQLParser.WhenClauseMergeContext wcm : ctx.whenClauseMerge()) {
            boolean matched = wcm.MATCHED() != null && wcm.NOT() == null;
            DMLStatementsNode action = (DMLStatementsNode) visit(wcm.mergeAction());

            if (matched) {
                whenMatchedActions.add(action);
            } else {
                whenNotMatchedActions.add(action);
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

    // ----------- TABLE SOURCE -----------
    @Override
    public ASTNode visitTableSource(SQLParser.TableSourceContext ctx) {
        return visit(ctx.tableFactor());
    }

    @Override
    public ASTNode visitTableFactor(SQLParser.TableFactorContext ctx) {
        if (ctx.qualifiedName() != null) {
            String tableName = ctx.qualifiedName().getText();
            String alias = null;
            if (ctx.alias() != null) {
                alias = ctx.alias().getText();
            }
            return new TableReferenceNode(tableName, alias);
        } else if (ctx.selectStatement() != null) {
            return visit(ctx.selectStatement());
        }
        return null;
    }

    // ================ DDL STATEMENTS ================

    // ----------- CREATE -----------
    @Override
    public ASTNode visitCreateStatement(SQLParser.CreateStatementContext ctx) {
        return visit(ctx.createObject());
    }

    @Override
    public ASTNode visitCreateDatabase(SQLParser.CreateDatabaseContext ctx) {
        String dbName = ctx.databaseName().getText();
        boolean ifNotExists = ctx.IF_NOT_EXISTS() != null;
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();
        return new CreateDatabaseNode(dbName, line, col, ifNotExists);
    }

    @Override
    public ASTNode visitCreateTable(SQLParser.CreateTableContext ctx) {
        String tableName = ctx.tableName().getText();
        boolean ifNotExists = ctx.IF_NOT_EXISTS() != null;
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();

        CreateTableNode tableNode = new CreateTableNode(tableName, line, col, ifNotExists);

        for (SQLParser.ColumnDefinitionContext colDef : ctx.columnDefinition()) {
            ColumnDefinitionNode colNode = (ColumnDefinitionNode) visit(colDef);
            tableNode.addColumn(colNode);
        }

        return tableNode;
    }

    @Override
    public ASTNode visitColumnDefinition(SQLParser.ColumnDefinitionContext ctx) {
        String colName = ctx.columnName().getText();
        String dataType = ctx.datatype().getText();
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();

        ColumnDefinitionNode colNode = new ColumnDefinitionNode(colName, dataType, line, col);

        for (SQLParser.ColumnConstraintContext constraint : ctx.columnConstraint()) {
            ColumnConstraintNode constraintNode = (ColumnConstraintNode) visit(constraint);
            if (constraintNode != null) {
                colNode.addConstraint(constraintNode);
            }
        }

        return colNode;
    }

    @Override
    public ASTNode visitColumnConstraint(SQLParser.ColumnConstraintContext ctx) {
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();

        if (ctx.PRIMARY() != null && ctx.KEY() != null) {
            return new PrimaryKeyConstraintNode(line, col);
        }
        if (ctx.UNIQUE() != null) {
            return new UniqueConstraintNode(line, col);
        }
        if (ctx.NOT() != null && ctx.NULL() != null) {
            return new CheckConstraintNode(null, line, col);
        }
        if (ctx.NULL() != null && ctx.NOT() == null) {
            return null; // NULL constraint - يمكن معالجتها إذا لزم الأمر
        }
        if (ctx.CHECK() != null) {
            ExpressionNode expr = (ExpressionNode) visit(ctx.expression());
            return new CheckConstraintNode(expr, line, col);
        }
        if (ctx.DEFAULT() != null) {
            ExpressionNode defaultValue = ctx.literal() != null ? (ExpressionNode) visit(ctx.literal()) : null;
            return new DefaultConstraintNode(defaultValue, line, col);
        }
        if (ctx.BINARY() != null) {
            return new BinaryConstraintNode(line, col);
        }
        if (ctx.COLLATE() != null) {
            String collation = ctx.IDENTIFIER().getText();
            return new CollateConstraintNode(collation, line, col);
        }
        if (ctx.AUTO_INCREMENT() != null) {
            return null; // يمكن إضافة node خاص بـ AUTO_INCREMENT
        }
        if (ctx.IDENTITY() != null) {
            return null; // يمكن إضافة node خاص بـ IDENTITY
        }
        if (ctx.ON() != null && ctx.UPDATE() != null) {
            ExpressionNode expr = (ExpressionNode) visit(ctx.expression());
            return new OnUpdateConstraintNode(expr, line, col);
        }

        return null;
    }

    @Override
    public ASTNode visitCreateView(SQLParser.CreateViewContext ctx) {
        String viewName = ctx.viewName().getText();
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();
        return new CreateViewNode(viewName, line, col);
    }

    @Override
    public ASTNode visitCreateIndex(SQLParser.CreateIndexContext ctx) {
        String indexName = ctx.indexName().getText();
        String tableName = ctx.tableName().getText();
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();

        CreateIndexNode indexNode = new CreateIndexNode(indexName, tableName, line, col);

        for (SQLParser.ColumnNameContext colName : ctx.columnName()) {
            indexNode.addColumnName(colName.getText());
        }

        return indexNode;
    }

    // ----------- ALTER -----------
    @Override
    public ASTNode visitAlterStatement(SQLParser.AlterStatementContext ctx) {
        return visit(ctx.alterObject());
    }

    @Override
    public ASTNode visitAlterTable(SQLParser.AlterTableContext ctx) {
        String tableName = ctx.tableName().getText();
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();

        AlterTableNode alterNode = new AlterTableNode(tableName, line, col);

        for (SQLParser.AlterTableActionContext action : ctx.alterTableAction()) {
            AlterTableActionNode actionNode = (AlterTableActionNode) visit(action);
            if (actionNode != null) {
                alterNode.addAction(actionNode);
            }
        }

        return alterNode;
    }

    @Override
    public ASTNode visitAddColumn(SQLParser.AddColumnContext ctx) {
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();
        ColumnDefinitionNode colDef = (ColumnDefinitionNode) visit(ctx.columnDefinition());
        return new AddColumnNode(colDef, line, col);
    }

    @Override
    public ASTNode visitDropColumn(SQLParser.DropColumnContext ctx) {
        String colName = ctx.columnName().getText();
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();
        return new DropColumnNode(colName, line, col);
    }

    @Override
    public ASTNode visitModifyColumn(SQLParser.ModifyColumnContext ctx) {
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();
        ColumnDefinitionNode colDef = (ColumnDefinitionNode) visit(ctx.columnDefinition());
        return new ModifyColumnNode(colDef, line, col);
    }

    @Override
    public ASTNode visitChangeColumn(SQLParser.ChangeColumnContext ctx) {
        String oldName = ctx.IDENTIFIER().getText();
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();
        ColumnDefinitionNode newCol = (ColumnDefinitionNode) visit(ctx.columnDefinition());
        return new ChangeColumnNode(oldName, newCol, line, col);
    }

    @Override
    public ASTNode visitRenameColumn(SQLParser.RenameColumnContext ctx) {
        String oldName = ctx.IDENTIFIER(0).getText();
        String newName = ctx.IDENTIFIER(1).getText();
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();
        return new RenameColumnNode(oldName, newName, line, col);
    }

    @Override
    public ASTNode visitRenameTable(SQLParser.RenameTableContext ctx) {
        String newName = ctx.IDENTIFIER().getText();
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();
        return new RenameTableNode(newName, line, col);
    }

    @Override
    public ASTNode visitAddConstraint(SQLParser.AddConstraintContext ctx) {
        String constraintName = ctx.IDENTIFIER() != null ? ctx.IDENTIFIER().getText() : null;
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();
        TableConstraintNode constraint = (TableConstraintNode) visit(ctx.tableConstraint());
        return new AddConstraintNode(constraintName, constraint, line, col);
    }

    @Override
    public ASTNode visitDropConstraint(SQLParser.DropConstraintContext ctx) {
        String constraintName = ctx.IDENTIFIER() != null ? ctx.IDENTIFIER().getText() : "PRIMARY_KEY";
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();
        return new DropConstraintNode(constraintName, line, col);
    }

    @Override
    public ASTNode visitAddIndex(SQLParser.AddIndexContext ctx) {
        String indexName = ctx.IDENTIFIER().getText();
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();
        return new AddIndexNode(indexName, line, col);
    }

    @Override
    public ASTNode visitDropColumnIndex(SQLParser.DropColumnIndexContext ctx) {
        String indexName = ctx.IDENTIFIER().getText();
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();
        return new com.example.DDL.Alter.DropIndexNode(indexName, line, col);
    }

    @Override
    public ASTNode visitAlterColumnDefault(SQLParser.AlterColumnDefaultContext ctx) {
        String colName = ctx.columnName().getText();
        String defaultValue = ctx.defaultValue() != null ? ctx.defaultValue().getText() : null;
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();
        return new AlterColumnDefaultNode(line, col, colName, defaultValue);
    }

    @Override
    public ASTNode visitTableConstraint(SQLParser.TableConstraintContext ctx) {
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();

        if (ctx.PRIMARY() != null && ctx.KEY() != null) {
            PrimarykeyTableConstraintNode pkNode = new PrimarykeyTableConstraintNode(line, col);
            // ctx.columnList() returns List<ColumnListContext>, so we need to get the first
            // one
            if (ctx.columnList() != null && !ctx.columnList().isEmpty()) {
                SQLParser.ColumnListContext columnListCtx = ctx.columnList(0);
                if (columnListCtx != null && columnListCtx.columnName() != null) {
                    for (SQLParser.ColumnNameContext colName : columnListCtx.columnName()) {
                        if (colName != null) {
                            pkNode.addColumn(colName.getText());
                        }
                    }
                }
            }
            return pkNode;
        }

        if (ctx.FOREIGN() != null && ctx.KEY() != null) {
            String refTable = ctx.tableName() != null ? ctx.tableName().getText() : "";
            ForeignKeyTableConstraintNode fkNode = new ForeignKeyTableConstraintNode(refTable, line, col);

            // Local columns - first columnList
            if (ctx.columnList() != null && ctx.columnList().size() > 0) {
                SQLParser.ColumnListContext localColumnListCtx = ctx.columnList(0);
                if (localColumnListCtx != null && localColumnListCtx.columnName() != null) {
                    for (SQLParser.ColumnNameContext colName : localColumnListCtx.columnName()) {
                        if (colName != null) {
                            fkNode.addLocalColumn(colName.getText());
                        }
                    }
                }

                // Referenced columns - second columnList
                if (ctx.columnList().size() > 1) {
                    SQLParser.ColumnListContext refColumnListCtx = ctx.columnList(1);
                    if (refColumnListCtx != null && refColumnListCtx.columnName() != null) {
                        for (SQLParser.ColumnNameContext colName : refColumnListCtx.columnName()) {
                            if (colName != null) {
                                fkNode.addReferencedColumn(colName.getText());
                            }
                        }
                    }
                }
            }
            return fkNode;
        }

        if (ctx.UNIQUE() != null) {
            UniqueTableConstraintNode uniqueNode = new UniqueTableConstraintNode(line, col);
            // ctx.columnList() returns List<ColumnListContext>, so we need to get the first
            // one
            if (ctx.columnList() != null && !ctx.columnList().isEmpty()) {
                SQLParser.ColumnListContext columnListCtx = ctx.columnList(0);
                if (columnListCtx != null && columnListCtx.columnName() != null) {
                    for (SQLParser.ColumnNameContext colName : columnListCtx.columnName()) {
                        if (colName != null) {
                            uniqueNode.addColumn(colName.getText());
                        }
                    }
                }
            }
            return uniqueNode;
        }

        if (ctx.CHECK() != null) {
            ExpressionNode expr = null;
            if (ctx.expression() != null) {
                expr = (ExpressionNode) visit(ctx.expression());
            }
            return new CheckTableConstraintNode(expr, line, col);
        }

        return null;
    }

    // ----------- DROP -----------
    @Override
    public ASTNode visitDropStatement(SQLParser.DropStatementContext ctx) {
        return visit(ctx.dropObject());
    }

    @Override
    public ASTNode visitDropDatabase(SQLParser.DropDatabaseContext ctx) {
        String dbName = ctx.databaseName().getText();
        boolean ifExists = ctx.IF_EXISTS() != null;
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();
        return new DropDatabaseNode(dbName, line, col, ifExists);
    }

    @Override
    public ASTNode visitDropTable(SQLParser.DropTableContext ctx) {
        boolean ifExists = ctx.IF_EXISTS() != null;
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();

        DropTableNode dropNode = new DropTableNode(line, col, ifExists);
        for (SQLParser.TableNameContext tableName : ctx.tableName()) {
            dropNode.addTableName(tableName.getText());
        }

        return dropNode;
    }

    @Override
    public ASTNode visitDropView(SQLParser.DropViewContext ctx) {
        boolean ifExists = ctx.IF_EXISTS() != null;
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();

        String firstView = ctx.viewName(0).getText();
        DropViewNode dropNode = new DropViewNode(firstView, line, col, ifExists);

        for (SQLParser.ViewNameContext viewName : ctx.viewName()) {
            dropNode.addViewName(viewName.getText());
        }

        return dropNode;
    }

    @Override
    public ASTNode visitDropIndex(SQLParser.DropIndexContext ctx) {
        String indexName = ctx.indexName().getText();
        String tableName = ctx.tableName().getText();
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();
        return new com.example.DDL.Drop.DropIndexNode(indexName, tableName, line, col);
    }

    // ----------- TRUNCATE -----------
    @Override
    public ASTNode visitTruncateStatement(SQLParser.TruncateStatementContext ctx) {
        return visit(ctx.truncateObject());
    }

    @Override
    public ASTNode visitTruncateTable(SQLParser.TruncateTableContext ctx) {
        boolean ifExists = ctx.IF_EXISTS() != null;
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();

        TruncateTableNode truncateNode = new TruncateTableNode(ifExists, line, col);

        for (SQLParser.TableNameContext tableName : ctx.tableName()) {
            truncateNode.addTableName(tableName.getText());
        }

        for (SQLParser.TruncateOptionContext option : ctx.truncateOption()) {
            if (option.CASCADE() != null) {
                truncateNode.addOption(TruncateTableNode.TruncateOption.CASCADE);
            } else if (option.RESTRICT() != null) {
                truncateNode.addOption(TruncateTableNode.TruncateOption.RESTRICT);
            } else if (option.RESTART() != null && option.IDENTITY() != null) {
                truncateNode.addOption(TruncateTableNode.TruncateOption.RESTART_IDENTITY);
            } else if (option.CONTINUE() != null && option.IDENTITY() != null) {
                truncateNode.addOption(TruncateTableNode.TruncateOption.CONTINUE_IDENTITY);
            }
        }

        return truncateNode;
    }

    // ================ TRANSACTION STATEMENTS ================

    @Override
    public ASTNode visitTransactionStatement(SQLParser.TransactionStatementContext ctx) {
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();

        if (ctx.BEGIN() != null) {
            boolean explicitTransaction = ctx.TRANSACTION() != null;
            return new BiginTransactionNode(explicitTransaction, line, col);
        }

        if (ctx.COMMIT() != null) {
            return new CommitTranasactionNode(line, col);
        }

        if (ctx.ROLLBACK() != null) {
            String savepointName = null;
            if (ctx.IDENTIFIER() != null) {
                savepointName = ctx.IDENTIFIER().getText();
            }
            return new RollBackTransactionNode(savepointName, line, col);
        }

        if (ctx.SAVEPOINT() != null) {
            String savepointName = ctx.IDENTIFIER().getText();
            return new SavepointNode(savepointName, line, col);
        }

        return null;
    }

    // ================ CONTROL FLOW STATEMENTS ================

    @Override
    public ASTNode visitControlFlowStatement(SQLParser.ControlFlowStatementContext ctx) {
        if (ctx.caseExpression() != null) {
            return visit(ctx.caseExpression());
        }
        if (ctx.ifStatement() != null) {
            return visit(ctx.ifStatement());
        }
        if (ctx.whileStatement() != null) {
            return visit(ctx.whileStatement());
        }
        if (ctx.returnStatement() != null) {
            return visit(ctx.returnStatement());
        }
        if (ctx.breakStatement() != null) {
            return visit(ctx.breakStatement());
        }
        if (ctx.continueStatement() != null) {
            return visit(ctx.continueStatement());
        }
        return null;
    }

    @Override
    public ASTNode visitIfStatement(SQLParser.IfStatementContext ctx) {
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();

        ExpressionNode condition = (ExpressionNode) visit(ctx.expression());
        BlockNode thenBlock = (BlockNode) visit(ctx.block(0));
        BlockNode elseBlock = null;

        if (ctx.ELSE() != null && ctx.block().size() > 1) {
            elseBlock = (BlockNode) visit(ctx.block(1));
        }

        return new IfStatementNode(condition, thenBlock, elseBlock, line, col);
    }

    @Override
    public ASTNode visitWhileStatement(SQLParser.WhileStatementContext ctx) {
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();

        ExpressionNode condition = (ExpressionNode) visit(ctx.expression());
        ASTNode body = visit(ctx.block());

        return new WhileStatementNode(condition, body, line, col);
    }

    @Override
    public ASTNode visitReturnStatement(SQLParser.ReturnStatementContext ctx) {
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();

        ASTNode expression = null;
        if (ctx.expression() != null) {
            expression = visit(ctx.expression());
        }

        return new ReturnStatementNode(expression, line, col);
    }

    @Override
    public ASTNode visitBreakStatement(SQLParser.BreakStatementContext ctx) {
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();
        return new BreakStatementNode(line, col);
    }

    @Override
    public ASTNode visitContinueStatement(SQLParser.ContinueStatementContext ctx) {
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();
        return new ContenueStatementNode(line, col);
    }

    @Override
    public ASTNode visitBlock(SQLParser.BlockContext ctx) {
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();

        BlockNode blockNode = new BlockNode(line, col);

        if (ctx.LBRACE() != null) {
            // Block with braces
            if (ctx.statementList() != null) {
                for (SQLParser.SingleStatementContext stmt : ctx.statementList().singleStatement()) {
                    ASTNode stmtNode = visit(stmt);
                    if (stmtNode != null) {
                        blockNode.addStatement(stmtNode);
                    }
                }
            }
        } else {
            // Single statement
            ASTNode stmtNode = visit(ctx.singleStatement());
            if (stmtNode != null) {
                blockNode.addStatement(stmtNode);
            }
        }

        return blockNode;
    }

    // ================ EXPRESSIONS ================

    @Override
    public ASTNode visitExpression(SQLParser.ExpressionContext ctx) {
        // CAST
        if (ctx.CAST() != null) {
            ExpressionNode expr = (ExpressionNode) visit(ctx.expression(0));
            String datatype = ctx.datatype().getText();
            return new CastExpressionNode(expr, datatype);
        }

        // CASE
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

        // NOT expr
        if (ctx.NOT() != null && ctx.expression().size() == 1) {
            return new UnaryExpressionNode("NOT", (ExpressionNode) visit(ctx.expression(0)));
        }

        // IS NULL / IS NOT NULL
        if (ctx.IS() != null && ctx.NULL() != null) {
            boolean not = ctx.NOT() != null;
            return new UnaryExpressionNode(not ? "IS NOT NULL" : "IS NULL",
                    (ExpressionNode) visit(ctx.expression(0)));
        }

        // BETWEEN / NOT BETWEEN
        if (ctx.BETWEEN() != null) {
            boolean not = ctx.NOT() != null;
            ExpressionNode expr = (ExpressionNode) visit(ctx.expression(0));
            ExpressionNode start = (ExpressionNode) visit(ctx.expression(1));
            ExpressionNode end = (ExpressionNode) visit(ctx.expression(2));
            return new BetweenExpressionNode(expr, start, end, not);
        }

        // IN / NOT IN
        if (ctx.IN() != null) {
            boolean not = ctx.NOT() != null;
            ExpressionNode expr = (ExpressionNode) visit(ctx.expression(0));
            List<ExpressionNode> list;

            if (ctx.selectStatement() != null) {
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

        // LIKE / NOT LIKE
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

        // EXISTS / NOT EXISTS
        if (ctx.EXISTS() != null) {
            ASTNode subquery = visit(ctx.selectStatement());
            return new ExistsExpressionNode(subquery, false);
        }

        // Function Calls
        if (ctx.functionCall() != null) {
            return visit(ctx.functionCall());
        }

        // Parentheses - Subquery
        if (ctx.LPAREN() != null && ctx.RPAREN() != null) {
            if (ctx.selectStatement() != null) {
                return visit(ctx.selectStatement());
            }
            if (ctx.expression().size() == 1) {
                return visit(ctx.expression(0));
            }
        }

        // Literal
        if (ctx.literal() != null) {
            return visit(ctx.literal());
        }

        // QualifiedName (columns)
        if (ctx.qualifiedName() != null) {
            return new ColumnNode(ctx.qualifiedName().getText());
        }

        int count = ctx.getChildCount();

        // Binary comparison operators: expr op expr
        if (count == 3 && ctx.comparisonOperator() != null) {
            return new BinaryExpressionNode(
                    (ExpressionNode) visit(ctx.expression(0)),
                    ctx.comparisonOperator().getText(),
                    (ExpressionNode) visit(ctx.expression(1)));
        }

        // Arithmetic operators +, -, *, /, %
        if (count == 3 && (ctx.PLUS() != null || ctx.MINUS_OP() != null ||
                ctx.STAR() != null || ctx.DIV() != null || ctx.MOD() != null)) {
            return new BinaryExpressionNode(
                    (ExpressionNode) visit(ctx.expression(0)),
                    ctx.getChild(1).getText(),
                    (ExpressionNode) visit(ctx.expression(1)));
        }

        // AND / OR logical ops
        if (count == 3 && (ctx.AND() != null || ctx.OR() != null)) {
            return new BinaryExpressionNode(
                    (ExpressionNode) visit(ctx.expression(0)),
                    ctx.getChild(1).getText(),
                    (ExpressionNode) visit(ctx.expression(1)));
        }

        return visitChildren(ctx);
    }

    // ================ Literals ================

    @Override
    public ASTNode visitLiteral(SQLParser.LiteralContext ctx) {
        String text = ctx.getText();

        if (ctx.INT_LITERAL() != null) {
            return new LiteralNode(Integer.parseInt(text));
        }
        if (ctx.FLOAT_LITERAL() != null) {
            return new LiteralNode(Double.parseDouble(text));
        }
        if (ctx.STRING_LITERAL() != null) {
            String val = text.substring(1, text.length() - 1);
            val = val.replace("''", "'"); // معالجة الـ escaped quotes
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
        if (ctx.HEX_LITERAL() != null) {
            return new LiteralNode(text);
        }

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
            functionName = ctx.aggregateFunction().getChild(0).getText();

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

    // ================ USE and GO Statements ================

    @Override
    public ASTNode visitUseStatement(SQLParser.UseStatementContext ctx) {
        // معالجة USE statement - يمكن إضافة node خاص إذا لزم الأمر
        return null;
    }

    @Override
    public ASTNode visitGoStatement(SQLParser.GoStatementContext ctx) {
        // معالجة GO statement - يمكن إضافة node خاص إذا لزم الأمر
        return null;
    }

    // ================ Cursor Statements ================

    @Override
    public ASTNode visitCursorStatement(SQLParser.CursorStatementContext ctx) {
        // معالجة cursor statements - يمكن إضافة nodes خاصة إذا لزم الأمر
        return null;
    }

    // ================ WITH Clause (CTE) ================

    @Override
    public ASTNode visitWithClause(SQLParser.WithClauseContext ctx) {
        boolean recursive = ctx.recursiveClause() != null;
        List<WithNode.CTETable> cteTables = new ArrayList<>();

        for (SQLParser.CteTableExpressionContext cteCtx : ctx.cteTableExpression()) {
            String name = cteCtx.IDENTIFIER().getText();
            DMLStatementsNode subquery = (DMLStatementsNode) visit(cteCtx.selectStatement());
            cteTables.add(new WithNode.CTETable(name, subquery));
        }

        return new WithNode(recursive, cteTables);
    }

    // ================ Merge Action ================

    @Override
    public ASTNode visitMergeAction(SQLParser.MergeActionContext ctx) {
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();

        if (ctx.UPDATE() != null) {
            Map<String, ExpressionNode> assignments = new LinkedHashMap<>();
            for (SQLParser.UpdateAssignmentContext ua : ctx.updateAssignment()) {
                UpdateAssignmentNode assignNode = (UpdateAssignmentNode) visit(ua);
                assignments.put(assignNode.getColumn(), assignNode.getValue());
            }
            return new UpdateNode("", assignments, null);
        }

        if (ctx.DELETE() != null) {
            return new DeleteNode("", null);
        }

        if (ctx.INSERT() != null) {
            List<String> columns = new ArrayList<>();
            if (ctx.IDENTIFIER() != null) {
                for (int i = 0; i < ctx.IDENTIFIER().size(); i++) {
                    columns.add(ctx.IDENTIFIER(i).getText());
                }
            }

            List<List<ExpressionNode>> rows = new ArrayList<>();
            List<ExpressionNode> row = new ArrayList<>();
            for (SQLParser.ExpressionContext e : ctx.expression()) {
                row.add((ExpressionNode) visit(e));
            }
            rows.add(row);

            return new InsertNode("", columns.isEmpty() ? null : columns, rows);
        }

        return null;
    }

    // ================ Default fallback ================

    @Override
    protected ASTNode defaultResult() {
        return null;
    }

    @Override
    protected ASTNode aggregateResult(ASTNode aggregate, ASTNode nextResult) {
        return nextResult != null ? nextResult : aggregate;
    }
}