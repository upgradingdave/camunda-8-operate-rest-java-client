package io.camunda.operate.rest.dto;

import java.util.List;

public class ProcessDefinitionQuery {

  ProcessDefinition filter;
  Long size;
  List<QuerySort> sort;
  List<Object> sortValues;

  public ProcessDefinitionQuery() {
  }

  public ProcessDefinition getFilter() {
    return filter;
  }

  public void setFilter(ProcessDefinition filter) {
    this.filter = filter;
  }

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public List<QuerySort> getSort() {
    return sort;
  }

  public void setSort(List<QuerySort> sort) {
    this.sort = sort;
  }

  public List<Object> getSortValues() {
    return sortValues;
  }

  public void setSortValues(List<Object> sortValues) {
    this.sortValues = sortValues;
  }
}
