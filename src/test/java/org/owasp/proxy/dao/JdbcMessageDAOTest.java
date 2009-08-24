package org.owasp.proxy.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.owasp.proxy.dao.Conversation;
import org.owasp.proxy.dao.JdbcMessageDAO;
import org.owasp.proxy.httpclient.MutableBufferedRequest;
import org.owasp.proxy.httpclient.MutableBufferedResponse;
import org.owasp.proxy.httpclient.RequestHeader;
import org.owasp.proxy.httpclient.ResponseHeader;
import org.owasp.proxy.util.AsciiString;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

public class JdbcMessageDAOTest {

	private static Logger logger = Logger.getAnonymousLogger();

	private static JdbcMessageDAO dao = null;

	private static DriverManagerDataSource dataSource = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Logger dslogger = Logger.getLogger(DriverManagerDataSource.class
				.getName());
		dslogger.setLevel(Level.OFF);
		dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl("jdbc:h2:mem:webscarab3;DB_CLOSE_DELAY=-1");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		dao = new JdbcMessageDAO();
		dao.setDataSource(dataSource);
	}

	@Before
	public void setUp() {
		dao.createTables();
	}

	@After
	public void tearDown() {
	}

	private void dump() {
		dump("SELECT * FROM contents");
		dump("SELECT * FROM headers");
		dump("SELECT * FROM requests");
		dump("SELECT * FROM responses");
		dump("SELECT * FROM conversations");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		dao.getJdbcTemplate().execute("SHUTDOWN");
	}

	@Test
	public void testSaveMessageContent() {
		MutableBufferedRequest request = new MutableBufferedRequest.Impl();
		request.setTarget(InetSocketAddress.createUnresolved("localhost", 80));
		request.setSsl(false);
		request.setHeader(AsciiString
				.getBytes("GET / HTTP/1.0\r\nHost: localhost\r\n\r\n"));
		request.setSubmissionTime(1);
		MutableBufferedResponse response = new MutableBufferedResponse.Impl();
		response.setHeader(AsciiString
				.getBytes("HTTP/1.0 200 Ok\r\nContent-Type: text\r\n\r\n"));
		byte[] cont = AsciiString.getBytes("Some content");
		response.setContent(cont);
		response.setHeaderStartedTime(2);
		response.setHeaderCompletedTime(3);
		response.setContentStartedTime(4);
		response.setContentCompletedTime(5);

		dao.saveRequest(request);
		dao.saveResponse(response);

		int id = dao.saveConversation(request.getId(), response.getId());

		logger.fine("ADDED conversation");
		dump();
		logger.fine("##############################################");

		Conversation c = dao.getConversation(id);

		RequestHeader reqh = dao.loadRequestHeader(c.getRequestId());
		ResponseHeader resph = dao.loadResponseHeader(c.getResponseId());

		assertTrue(Arrays.equals(request.getHeader(), reqh.getHeader()));
		assertEquals(request.getTarget(), reqh.getTarget());
		assertEquals(request.isSsl(), reqh.isSsl());
		assertEquals(request.getSubmissionTime(), reqh.getSubmissionTime());

		assertTrue("Response headers differ", Arrays.equals(response
				.getHeader(), resph.getHeader()));
		assertEquals(response.getHeaderStartedTime(), resph
				.getHeaderStartedTime());
		assertEquals(response.getHeaderCompletedTime(), resph
				.getHeaderCompletedTime());
		assertEquals(response.getContentStartedTime(), resph
				.getContentStartedTime());
		assertEquals(response.getContentCompletedTime(), resph
				.getContentCompletedTime());
		byte[] content = dao.loadMessageContent(dao.getMessageContentId(c
				.getRequestId()));

		assertNull(content);

		content = dao.loadMessageContent(dao.getMessageContentId(c
				.getResponseId()));

		assertTrue(Arrays.equals(cont, content));

		assertTrue("Delete failed", dao.deleteConversation(id));

		dump();
	}

	private static void dump(String sql) {
		logger.fine("\n" + sql);
		SqlRowSet rs = dao.getJdbcTemplate().queryForRowSet(sql);
		try {
			SqlRowSetMetaData rsmd = rs.getMetaData();
			int c = rsmd.getColumnCount();
			StringBuffer buff = new StringBuffer();
			for (int i = 1; i <= c; i++) {
				buff.append(rsmd.getColumnLabel(i));
				buff.append(i == c ? "\n" : "\t");
			}
			logger.fine(buff.toString());
			buff.delete(0, buff.length());
			while (rs.next()) {
				for (int i = 1; i <= c; i++) {
					buff.append(rs.getObject(i));
					buff.append(i == c ? "\n" : "\t");
				}
				logger.fine(buff.toString());
				buff.delete(0, buff.length());
			}
			logger.fine("================\n\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}