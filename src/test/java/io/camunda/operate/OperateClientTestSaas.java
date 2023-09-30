package io.camunda.operate;

import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("saas")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OperateClientTestSaas extends OperateClientTest {
  @SpringBootApplication
  @Deployment(resources = "classpath:/bpmn/tasklistRestAPIUnitTestProcess.bpmn")
  public static class OperateClientTestApp {
    public static void main(String[] args) {
      SpringApplication.run(OperateClientTestApp.class, args);
    }
  }
}
