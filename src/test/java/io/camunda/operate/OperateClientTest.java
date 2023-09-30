package io.camunda.operate;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.camunda.operate.auth.JWTAuthentication;
import io.camunda.operate.dto.*;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.exception.OperateRestException;
import io.camunda.operate.json.JsonUtils;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.response.Topology;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public abstract class OperateClientTest {

  @Autowired
  JWTAuthentication jwtAuthentication;
  @Autowired
  OperateRestClient operateRestClient;
  @Autowired
  ZeebeClient zeebeClient;

  @Value("${operate.unit-test.bpmnProcessId: 'tasklistRestAPIUnitTestProcess'}")
  String bpmnProcessId;

  public void createInstance(Map<String, String> variables) {
    ProcessInstanceEvent event = zeebeClient.newCreateInstanceCommand()
        .bpmnProcessId(bpmnProcessId)
        .latestVersion()
        .variables(variables)
        .send()
        .join();

    Long processInstanceKey = event.getProcessInstanceKey();
    assertNotNull(processInstanceKey);
  }

  public ProcessDefinitionQueryResults findLatestDefinition(String bpmnProcessId) throws OperateException, OperateRestException {

    // filter so we only get definitions for a given bpmnProcessId
    ProcessDefinition processDefinition = new ProcessDefinition();
    processDefinition.setBpmnProcessId(bpmnProcessId);

    // sort by version ASC so we get the latest version
    QuerySort versionSort = new QuerySort();
    versionSort.setField("version");
    versionSort.setOrder(QuerySort.QUERY_SORT_ORDER_DESC);
    List<QuerySort> querySorts = new ArrayList<>();
    querySorts.add(versionSort);

    ProcessDefinitionQuery processDefinitionQuery = new ProcessDefinitionQuery();
    processDefinitionQuery.setFilter(processDefinition);
    processDefinitionQuery.setSort(querySorts);
    processDefinitionQuery.setSize(1L);
    return operateRestClient.query(processDefinitionQuery);
  }

  public void setup() {
    // Nothing to do yet
  }

  @BeforeAll
  public void before() {
    setup();
  }

  @Test
  void contextLoads() {
  }

  @Test
  void authenticationTest() throws OperateException {
    assertTrue(jwtAuthentication.authenticate(operateRestClient));
  }

  @Test
  public void zeebeStatus() {
    Topology topology = zeebeClient.newTopologyRequest().send().join();
    assertTrue(topology.getClusterSize() > 0);
  }

  @Test
  public void jsonTest() throws JsonProcessingException {
    JsonUtils<AccessTokenRequest> jsonUtils = new JsonUtils<>(AccessTokenRequest.class);
    AccessTokenRequest accessTokenRequest = new AccessTokenRequest("xxx", "xxx", "tasklist.camunda.io", "client_credentials");
    String json = jsonUtils.toJson(accessTokenRequest);
    assertNotNull(json);
    assertEquals("{\"client_id\":\"xxx\",\"client_secret\":\"xxx\",\"audience\":\"tasklist.camunda.io\",\"grant_type\":\"client_credentials\"}", json);

    AccessTokenRequest result = jsonUtils.fromJson(json);
    assertNotNull(result);
    assertEquals("xxx", result.getClient_id());
  }

  @Test
  public void findDefinitionsTest() throws OperateRestException, OperateException {
    ProcessDefinitionQueryResults results = findLatestDefinition("tasklistRestAPIUnitTestProcess");
    assertNotNull(results);
    assertEquals(results.getItems().get(0).getBpmnProcessId(), "tasklistRestAPIUnitTestProcess");
  }

}
