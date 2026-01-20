package com.example.DML;

import com.example.Expressions.ExpressionNode;

import java.util.List;

public class SelectNode extends DMLStatementsNode {

    private List<ExpressionNode> selectList;
    private ExpressionNode whereClause;
    private List<ExpressionNode> groupByList;
    private ExpressionNode havingClause;
    private List<ExpressionNode> orderByList;

    public SelectNode(
            List<ExpressionNode> selectList,
            ExpressionNode whereClause,
            List<ExpressionNode> groupByList,
            ExpressionNode havingClause,
            List<ExpressionNode> orderByList) {
        this.selectList = selectList;
        this.whereClause = whereClause;
        this.groupByList = groupByList;
        this.havingClause = havingClause;
        this.orderByList = orderByList;

        selectList.forEach(this::addChild);
        if (whereClause != null)
            addChild(whereClause);
        if (groupByList != null)
            groupByList.forEach(this::addChild);
        if (havingClause != null)
            addChild(havingClause);
        if (orderByList != null)
            orderByList.forEach(this::addChild);
    }

    @Override
    public String getNodeType() {
        return "SELECT";
    }

    public List<ExpressionNode> getSelectList() {
        return selectList;
    }

    public ExpressionNode getWhereClause() {
        return whereClause;
    }

    public List<ExpressionNode> getGroupByList() {
        return groupByList;
    }

    public ExpressionNode getHavingClause() {
        return havingClause;
    }

    public List<ExpressionNode> getOrderByList() {
        return orderByList;
    }

}
