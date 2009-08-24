package org.owasp.proxy.dao;

import java.net.InetSocketAddress;

import org.owasp.proxy.httpclient.BufferedRequest;
import org.owasp.proxy.httpclient.BufferedResponse;
import org.owasp.proxy.httpclient.MessageFormatException;
import org.owasp.proxy.httpclient.RequestHeader;
import org.owasp.proxy.httpclient.ResponseHeader;

public class ConversationSummary {

	private long id;

	private InetSocketAddress target;

	private String requestMethod, requestResource, requestContentType,
			responseStatus, responseReason, responseContentType, connection;

	private boolean ssl;

	private int requestContentSize, responseContentSize;

	private long requestTime, responseHeaderStartedTime,
			responseHeaderCompletedTime, responseContentStartedTime,
			responseContentCompletedTime;

	public ConversationSummary() {
	}

	public void summarizeRequest(BufferedRequest request)
			throws MessageFormatException {
		byte[] content = request.getContent();
		int contentSize = content == null ? 0 : content.length;
		summarizeRequest(request, contentSize);
	}

	public void summarizeRequest(RequestHeader request, int contentSize)
			throws MessageFormatException {
		target = request.getTarget();
		ssl = request.isSsl();
		requestMethod = request.getMethod();
		requestResource = request.getResource();
		requestContentType = request.getHeader("Content-Type");
		requestContentSize = contentSize;
		requestTime = request.getSubmissionTime();
	}

	public void summarizeResponse(BufferedResponse response)
			throws MessageFormatException {
		byte[] content = response.getContent();
		int contentSize = content == null ? 0 : content.length;
		summarizeResponse(response, contentSize);
	}

	public void summarizeResponse(ResponseHeader response, int contentSize)
			throws MessageFormatException {
		responseStatus = response.getStatus();
		responseReason = response.getReason();
		responseContentType = response.getHeader("Content-Type");
		responseContentSize = contentSize;
		responseHeaderStartedTime = response.getHeaderStartedTime();
		responseHeaderCompletedTime = response.getHeaderCompletedTime();
		responseContentStartedTime = response.getContentStartedTime();
		responseContentCompletedTime = response.getContentCompletedTime();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public InetSocketAddress getTarget() {
		return target;
	}

	public void setTarget(InetSocketAddress target) {
		this.target = target;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getRequestResource() {
		return requestResource;
	}

	public void setRequestResource(String requestResource) {
		this.requestResource = requestResource;
	}

	public String getRequestContentType() {
		return requestContentType;
	}

	public void setRequestContentType(String requestContentType) {
		this.requestContentType = requestContentType;
	}

	public String getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getResponseReason() {
		return responseReason;
	}

	public void setResponseReason(String responseReason) {
		this.responseReason = responseReason;
	}

	public String getResponseContentType() {
		return responseContentType;
	}

	public void setResponseContentType(String responseContentType) {
		this.responseContentType = responseContentType;
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	public int getRequestContentSize() {
		return requestContentSize;
	}

	public void setRequestContentSize(int requestContentSize) {
		this.requestContentSize = requestContentSize;
	}

	public int getResponseContentSize() {
		return responseContentSize;
	}

	public void setResponseContentSize(int responseContentSize) {
		this.responseContentSize = responseContentSize;
	}

	public long getRequestSubmissionTime() {
		return requestTime;
	}

	public void setRequestSubmissionTime(long time) {
		this.requestTime = time;
	}

	public long getResponseHeaderCompletedTime() {
		return responseHeaderCompletedTime;
	}

	public void setResponseHeaderCompletedTime(long time) {
		this.responseHeaderCompletedTime = time;
	}

	public long getResponseContentCompletedTime() {
		return responseContentCompletedTime;
	}

	public void setResponseContentCompletedTime(long time) {
		this.responseContentCompletedTime = time;
	}

}