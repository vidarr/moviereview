/**
 * BMovieReviewer Copyright (C) 2010 Michael J. Beer
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package data;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import tools.AppLogger;
import data.formats.BogenFormat;
import data.formats.JPEG;
import data.formats.Tex;

public class HTTPPost {

	public static final int HTTP_OK = 200;

	public static final String[] LINKTYPE_NAMES = { "unknown", // eigl. nicht
																// belegt
			"imdb", // IMDB Deutsch
			"imdb", // IMDB englisch
			"wiki_ger", // Wikipedia Deutsch
			"wiki_eng", // Wikipedia Englisch
			"videoraiders", // ...
			"ofdb", "badmovies"
	/* ,"Youtube" // Das ist mal angedacht gewesen - */
	};

	public HTTPPost(String url) {
		this(url, null, null);
	}

	public HTTPPost(String url, String user, String passwort)
			throws IllegalArgumentException {
		if (url == null) {
			throw new IllegalArgumentException();
		}
		this.url = url;
		this.passwort = passwort;
		this.user = user;
		dir = Globals.getInstance().getProperty("server.dir");
		dir = (dir.equals("")) ? dir : dir + '/';
		globs = Globals.getInstance();
		client = new DefaultHttpClient();

		setupSSL();

	}

	public String postCover(Bogen bogen) throws HTTPException, IOException {
		String answer = "\n\n\nPost Cover:\n";
		Globals globs = Globals.getInstance();
		if (bogen.getCoverImage() == null) {
			throw new IllegalArgumentException();
		}
		String name = bogen.getCover().getText();
		BogenFormat formatter = new JPEG(bogen);
		answer += postWithFormatter(globs.getProperty("server.postjpeg"), name,
				formatter, bogen, globs.getProperty("jpeg.mime"));
		return answer;
	}

	public String postXML(Bogen bogen) throws HTTPException, IOException {
		String answer = "\n\n\nPost XML :\n";
		Globals globs = Globals.getInstance();

		String name = bogen.getFileNameWithoutEnding();
		BogenFormat formatter = new data.formats.XML(bogen);
		answer += postWithFormatter(globs.getProperty("server.postxml"), name
				+ ".xml", formatter, bogen, globs.getProperty("mimexml"));
		return answer;
	}

	public String postTex(Bogen bogen) throws HTTPException, IOException {
		String answer = "\n\n\nPost Tex :\n";
		Globals globs = Globals.getInstance();
		String name = bogen.getFileNameWithoutEnding();
		BogenFormat formatter = new Tex(bogen);
		answer += postWithFormatter(globs.getProperty("server.posttex"), name
				+ ".tex", formatter, bogen, globs.getProperty("mimetex"));
		return answer;
	}

	public String registerMovie(Bogen bogen) throws HTTPException {
		List<NameValuePair> post = new LinkedList<NameValuePair>();
		String answer = "\n\n\nPost BMovie :\n";
		String name = bogen.getFileNameWithoutEnding();

		post.add(new BasicNameValuePair("movie_name", bogen
				.getText(Bogen.I_TITEL)));
		post.add(new BasicNameValuePair("genre", bogen.getText(Bogen.I_GENRE)));
		post.add(new BasicNameValuePair("eval_file", name));
		for (QualifiedString l : bogen.getLinks()) {
			String typ = "";
			if (l.getTyp() < Link.TYPES.length) {
				typ = LINKTYPE_NAMES[l.getTyp()];
			}
			post.add(new BasicNameValuePair(typ, l.getText()));
		}
		if (bogen.getCover().toString().equals("")) {
			throw new HTTPException(
					"Konnte Film nicht registrieren: Kein Cover angegeben");
		}
		post.add(new BasicNameValuePair("image", (new File(bogen.getCover()
				.getText())).getName()));
		post.add(new BasicNameValuePair("rss", bogen.getText(Bogen.I_RSS)));
		post.add(new BasicNameValuePair("new_movie", "Post_movie"));
		answer += postRequest(globs.getProperty("server.postbmovie"), post);
		return answer;
	}

	public String postQuotes(Bogen bogen) throws HTTPException {
		List<NameValuePair> post = new LinkedList<NameValuePair>();
		String answer = "\n\n\nPost Quotes :\n";
		String name = bogen.getFileNameWithoutEnding();
		post.add(new BasicNameValuePair("movie_name_2", bogen
				.getText(Bogen.I_TITEL)));
		post.add(new BasicNameValuePair("eval_file_2", name));
		String quotes = "";
		int num = 0;
		for (QualifiedString str : bogen.getZitate()) {
			AppLogger.info("Zitat : " + str.getTyp() + " : " + str.getText());
			if (str.getTyp() > 0) { // registrieren, wenn nicht als
				// uninteressant markiert
				if (num > 0) {
					quotes += "\n";
				}
				quotes += str.getText();
				num++;
			}
		}
		if (num > 0) {
			post.add(new BasicNameValuePair("quotes", quotes));
			answer += postRequest(globs.getProperty("server.postquotations"),
					post);
		} else {
			answer += "Keine Zitate zu registrieren";
		}

		return answer;
	}

	// ////////////////////////////////////////////////////////////////////////
	// INTERNALS
	// ////////////////////////////////////////////////////////////////////////

	protected String executePost(String receiver, HttpEntity entity)
			throws HTTPException {
		if (receiver == null || entity == null) {
			throw new IllegalArgumentException();
		}
		String answer = "";
		HttpResponse response = null;
		HttpPost method = new HttpPost(this.url + "/" + receiver);
		method.setEntity(entity);

		try {
			response = client.execute(method);
		} catch (ClientProtocolException e) {
			throw new HTTPException(e.getMessage());
		} catch (IOException e) {
			throw new HTTPException(e.getMessage());
		}
		int code = response.getStatusLine().getStatusCode();

		if (code != HTTP_OK)
			throw new HTTPException(response.getStatusLine().getReasonPhrase(),
					code);

		HttpEntity respEntity = response.getEntity();
		if (respEntity != null) {
			try {
				BufferedReader isr = new BufferedReader(new InputStreamReader(
						respEntity.getContent()));
				String line = null;
				while ((line = isr.readLine()) != null) {
					answer += line.replaceAll("^\\s+", "");
				}
				isr.close();
			} catch (IOException e) {
				throw new HTTPException(e.getMessage(), code);
			}
		}

		return answer;
	}

	protected String executePost(String receiver, String name, ContentBody body)
			throws HTTPException {
		if (receiver == null || name == null || body == null) {
			throw new IllegalArgumentException();
		}
		MultipartEntity entity = new MultipartEntity();
		FormBodyPart part = null;
		if (passwort != null && this.user != null) {
			try {
				part = new FormBodyPart(
						globs.getProperty("server.passwordstring"),
						new StringBody(this.passwort));
				part.getHeader().removeFields("Content-Transfer-Encoding");
				part.getHeader().removeFields("Content-Type");
				entity.addPart(part);
				part = new FormBodyPart(globs.getProperty("server.userstring"),
						new StringBody(this.user));
				part.getHeader().removeFields("Content-Transfer-Encoding");
				part.getHeader().removeFields("Content-Type");
				entity.addPart(part);
			} catch (UnsupportedEncodingException e) {
				throw new HTTPException(e.getMessage());
			}
		}
		entity.addPart(name, body);
		return executePost(receiver, entity);
	}

	protected String postRequest(String receiver, List<NameValuePair> params)
			throws HTTPException {
		UrlEncodedFormEntity entity = null;
		if (receiver == null || params == null) {
			throw new IllegalArgumentException();
		}
		if (passwort != null && this.user != null) {
			params.add(new BasicNameValuePair(globs
					.getProperty("server.passwordstring"), this.passwort));
			params.add(new BasicNameValuePair(globs
					.getProperty("server.userstring"), this.user));
		}
		try {
			entity = new UrlEncodedFormEntity(params,
					globs.getProperty("encoding"));
		} catch (UnsupportedEncodingException e) {
			throw new HTTPException(e.getMessage());
		}
		return executePost(receiver, entity);

	}

	protected String postWithFormatter(String receiver, String name,
			BogenFormat formatter, Object obj, String contentType)
			throws HTTPException {
		if (receiver == null || name == null || formatter == null
				|| obj == null || contentType == null) {
			throw new IllegalArgumentException();
		}
		String answer = "";
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		try {
			formatter.write(bas);
			byte[] data = bas.toByteArray();
			ContentBody content = new ByteContentBody(data, contentType, name);
			
			answer = executePost(receiver, "uploadedfile", content);
		} catch (HTTPException e) {
			throw e;
		} catch (Exception e) {
			AppLogger.warning(e.toString());
			e.printStackTrace();
			throw new HTTPException(e.getMessage());
		}
		return answer;
	}

	protected void setupSSL() {
		String seclevel = Globals.getInstance().getProperty(
				"server.securitylevel");
		X509TrustManager trustMan;
		AppLogger.info("Securitylevel: " + seclevel);
		if (seclevel.equals("fuckit")) {
			// Hier wirds witzig
			// Scheisz auf die Zertifizierungsvalidierung
			trustMan = new X509TrustManager() {
				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] arg0, String arg1)
						throws java.security.cert.CertificateException {

				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] arg0, String arg1)
						throws java.security.cert.CertificateException {

				}

				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
		} else if (seclevel.equals("paranoid")) {
			return;
		} else {
			AppLogger.info("Seclevel normal");
			try {
				trustMan = new NormalX509TrustManager();
			} catch (Exception e) {
				AppLogger.warning("Konnte TrustManager nicht laden");
				return;
			}
		}
		try {
			SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(null, new X509TrustManager[] { trustMan }, null);
			SSLSocketFactory sf = new SSLSocketFactory(sslcontext);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = client.getConnectionManager();
			SchemeRegistry schemeRegistry = ccm.getSchemeRegistry();
			schemeRegistry.register(new Scheme("https", sf, 443));
			client = new DefaultHttpClient(ccm, client.getParams());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	protected String user = null;
	protected String passwort = null;

	protected String dir = "";
	protected String url;
	protected Globals globs = null;
	protected HttpClient client = null;

	public static class HTTPException extends Exception {

		protected int responseCode = -1;

		public HTTPException(String err) {
			super(err);
		}

		public HTTPException(String err, int code) {
			this(err);
			responseCode = code;
		}

		public int getResponseCode() {
			return responseCode;
		}

		/**
         * 
         */
		private static final long serialVersionUID = 1L;

	}
}
