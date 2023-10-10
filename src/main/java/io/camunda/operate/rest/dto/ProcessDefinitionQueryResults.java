package io.camunda.operate.rest.dto;

import java.util.List;

public class ProcessDefinitionQueryResults {

  List<ProcessDefinition> items;
  Long total;
  List<Object> sortValues;

  public ProcessDefinitionQueryResults() {
  }

  public List<ProcessDefinition> getItems() {
    return items;
  }

  public void setItems(List<ProcessDefinition> items) {
    this.items = items;
  }

  public Long getTotal() {
    return total;
  }

  public void setTotal(Long total) {
    this.total = total;
  }

  public List<Object> getSortValues() {
    return sortValues;
  }

  public void setSortValues(List<Object> sortValues) {
    this.sortValues = sortValues;
  }
}
