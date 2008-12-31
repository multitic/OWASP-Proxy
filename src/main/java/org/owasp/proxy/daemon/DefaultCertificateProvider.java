package org.owasp.proxy.daemon;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

public class DefaultCertificateProvider implements CertificateProvider {

	private SSLSocketFactory sslSocketFactory = null;

	public DefaultCertificateProvider() throws KeyStoreException,
			CertificateException, NoSuchAlgorithmException, IOException,
			UnrecoverableKeyException, KeyManagementException {
		this(null, "password", "password");
	}

	public DefaultCertificateProvider(String resource, String storePassword,
			String keyPassword) throws KeyStoreException, CertificateException,
			NoSuchAlgorithmException, IOException, UnrecoverableKeyException,
			KeyManagementException {
		if (resource == null) {
			resource = getClass().getPackage().getName().replace('.', '/')
					+ "/server.p12";
		}

		KeyStore ks = KeyStore.getInstance("PKCS12");
		InputStream is = getClass().getClassLoader().getResourceAsStream(
				resource);
		if (is != null) {
			char[] ksp = storePassword.toCharArray();
			ks.load(is, ksp);
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			char[] kp = keyPassword.toCharArray();
			kmf.init(ks, kp);
			SSLContext sslcontext = SSLContext.getInstance("SSLv3");
			sslcontext.init(kmf.getKeyManagers(), null, null);
			sslSocketFactory = sslcontext.getSocketFactory();
		}
	}

	/**
	 * This default implementation uses the same certificate for all hosts.
	 * 
	 * @param host
	 *            the host that the client wishes to CONNECT to
	 * @param port
	 *            the port that the client wishes to CONNECT to
	 * @return an SSLSocketFactory generated from the relevant server key
	 *         material
	 */
	public SSLSocketFactory getSocketFactory(String host, int port)
			throws IOException {
		if (sslSocketFactory == null) {
		}
		return sslSocketFactory;
	}

}