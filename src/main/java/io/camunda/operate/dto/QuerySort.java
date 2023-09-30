package io.camunda.operate.dto;

public class QuerySort {

  public final static String QUERY_SORT_ORDER_ASC = "ASC";
  public final static String QUERY_SORT_ORDER_DESC = "DESC";

  String field;
  String order;

  public QuerySort() {
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public String getOrder() {
    return order;
  }

  public void setOrder(String order) {
    this.order = order;
  }
}
