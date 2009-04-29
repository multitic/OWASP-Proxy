package org.owasp.httpclient;

import java.io.InputStream;

public interface StreamingResponse extends ResponseHeader, StreamingMessage {

	public class Impl extends ResponseHeader.Impl implements StreamingResponse {

		private InputStream content;

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.owasp.httpclient.StreamingResponse#getContent()
		 */
		public InputStream getContent() {
			return content;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.owasp.httpclient.StreamingResponse#setContent(java.io.InputStream
		 * )
		 */
		public void setContent(InputStream content) {
			this.content = content;
		}

	}

}