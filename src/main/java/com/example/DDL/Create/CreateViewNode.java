package com.example.DDL.Create;

public class CreateViewNode extends CreateStatementNode {
    private final String viewName;

    public CreateViewNode(String viewName, int lineNumber, int columnNumber) {
        super(lineNumber, columnNumber);
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    @Override
    public String getNodeType() {
        return "CREATE_VIEW (" + viewName + ")";
    }
}
