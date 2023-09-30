package io.camunda.operate.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

// This class is used as a query filter, so when fields are null, we don't want to include them in the json
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessDefinition {

  String key;
  String name;
  Integer version;
  String bpmnProcessId;

  public ProcessDefinition() {
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public String getBpmnProcessId() {
    return bpmnProcessId;
  }

  public void setBpmnProcessId(String bpmnProcessId) {
    this.bpmnProcessId = bpmnProcessId;
  }
}
