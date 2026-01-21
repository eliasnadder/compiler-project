package com.example.DDL.Truncate;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class TruncateTableNode extends TruncateStatementNode {
    public enum TruncateOption {
        CASCADE,
        RESTRICT,
        RESTART_IDENTITY,
        CONTINUE_IDENTITY
    }

    private final List<String> tableNames;
    private final EnumSet<TruncateOption> options;
    private final boolean ifExists;

    public TruncateTableNode(boolean ifExists, int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
        this.tableNames = new ArrayList<>();
        this.options = EnumSet.noneOf(TruncateOption.class);
        this.ifExists = ifExists;
    }

    public void addTableName(String tableName) {
        if (tableName != null) {
            tableNames.add(tableName);
        }

    }

    public void addOption(TruncateOption option) {
        options.add(option);
    }

    public List<String> getTableNames() {
        return tableNames;
    }

    public EnumSet<TruncateOption> getOptions() {
        return options;
    }

    public boolean isIfExists() {
        return ifExists;
    }

    @Override
    public String getNodeType() {
        return "TRUNCATE_TABLE " + tableNames +
                (ifExists ? " IF_EXISTS" : "") + (options.isEmpty() ? "" : " OPTIONS=" + options);
    }

}
