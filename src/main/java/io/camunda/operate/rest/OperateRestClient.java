package io.camunda.operate.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.camunda.operate.rest.auth.Authentication;
import io.camunda.operate.rest.exception.OperateException;
import io.camunda.operate.rest.exception.OperateRestException;
import io.camunda.operate.rest.dto.AccessTokenResponse;
import io.camunda.operate.rest.dto.ErrorResponse;
import io.camunda.operate.rest.dto.ProcessDefinitionQuery;
import io.camunda.operate.rest.dto.ProcessDefinitionQueryResults;
import io.camunda.operate.rest.json.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class OperateRestClient {

  Authentication authentication;
  AccessTokenResponse accessTokenResponse;
  String operateBaseUrl;

  private final HttpClient httpClient;

  public OperateRestClient(
      @Autowired @Qualifier("OperateRestAuthentication") Authentication authentication,
      @Value("${operate.client.operateBaseUrl}") String operateBaseUrl) {
    this.authentication = authentication;
    httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();

    this.operateBaseUrl = operateBaseUrl;
  }

  public Authentication getAuthentication() {
    return authentication;
  }

  public void setAuthentication(Authentication authentication) {
    this.authentication = authentication;
  }

  public AccessTokenResponse getAccessTokenResponse() {
    return accessTokenResponse;
  }

  public void setAccessTokenResponse(AccessTokenResponse accessTokenResponse) {
    this.accessTokenResponse = accessTokenResponse;
  }

  public HttpResponse<String> post(String endPoint, String body)
      throws OperateException, OperateRestException {
    return postOrPatch("POST", endPoint, body);
  }

  public HttpResponse<String> patch(String endPoint, String body)
      throws OperateException, OperateRestException {
    return postOrPatch("PATCH", endPoint, body);
  }

  private HttpResponse<String> handleResponse(HttpResponse<String> response) throws OperateRestException, OperateException {
    if(response.statusCode() == 200) {
      return response;
    } else if (response.statusCode() == 400) {

      JsonUtils<ErrorResponse> jsonUtils = new JsonUtils<>(ErrorResponse.class);
      try {
        ErrorResponse errorResponse = jsonUtils.fromJson(response.body());
        throw new OperateRestException(errorResponse);
      } catch (JsonProcessingException e) {
        throw new OperateException("Unable to parse error response", e);
      }

    } else {
      throw new OperateException("Unexpected response", new RuntimeException(response.statusCode() + " " + response.body()));
    }
  }

  private HttpResponse<String> postOrPatch(String method, String endPoint, String body)
      throws OperateException, OperateRestException {

    if(accessTokenResponse == null) {
      this.authentication.authenticate(this);
    }

    HttpResponse<String> response = doPostOrPatch(method, endPoint, body);

    //If we get a 401 that might mean the access token has expired. Try to refresh the token
    if(response.statusCode() == 401) {
      this.authentication.authenticate(this);
      response = doPostOrPatch(method, endPoint, body);
    }

    return handleResponse(response);
  }

  private HttpResponse<String> doPostOrPatch(String method, String endPoint, String body) throws OperateException {
    try {

      HttpRequest request = HttpRequest.newBuilder()
          .uri(new URI(endPoint))
          .header("content-type", "application/json")
          .header("Authorization", "Bearer " + accessTokenResponse.getAccess_token())
          .timeout(Duration.ofSeconds(10))
          .method(method, HttpRequest.BodyPublishers.ofString(body))
          .build();

      return httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    } catch (URISyntaxException e) {
      throw new OperateException("Endpoint URL must be a valid URI", e);
    } catch (IOException | InterruptedException e) {
      throw new OperateException("Unable to complete request", e);
    }

  }

  private HttpResponse<String> get(String endPoint)
      throws OperateException, OperateRestException {

    if(accessTokenResponse == null) {
      this.authentication.authenticate(this);
    }

    HttpResponse<String> response = doGet(endPoint);

    //If we get a 401 that might mean the access token has expired. Try to refresh the token
    if(response.statusCode() == 401) {
      this.authentication.authenticate(this);
      response = doGet(endPoint);
    }

    return handleResponse(response);
  }

  private HttpResponse<String> doGet(String endPoint) throws OperateException {
    try {

      HttpRequest request = HttpRequest.newBuilder()
          .uri(new URI(endPoint))
          .header("content-type", "application/json")
          .header("Authorization", "Bearer " + accessTokenResponse.getAccess_token())
          .timeout(Duration.ofSeconds(10))
          .GET()
          .build();

      return httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    } catch (URISyntaxException e) {
      throw new OperateException("Endpoint URL must be a valid URI", e);
    } catch (IOException | InterruptedException e) {
      throw new OperateException("Unable to complete request", e);
    }

  }

  public ProcessDefinitionQueryResults query(ProcessDefinitionQuery request) throws OperateException, OperateRestException {
    JsonUtils<ProcessDefinitionQuery> jsonRequest = new JsonUtils<>(ProcessDefinitionQuery.class);
    try {

      String body = jsonRequest.toJson(request);
      String endpoint = operateBaseUrl + "/v1/process-definitions/search";
      HttpResponse<String> response = post(endpoint, body);
      JsonUtils<ProcessDefinitionQueryResults> jsonResponse = new JsonUtils<>(ProcessDefinitionQueryResults.class);
      return jsonResponse.fromJson(response.body());

    } catch (JsonProcessingException e) {
      throw new OperateException("Unable to parse response to json", e);
    }
  }

}
