/*
 * BMovieReviewer Copyright (C) 2009, 2010 Michael J. Beer
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import tools.AppLogger;

public class NormalX509TrustManager implements X509TrustManager {

    protected X509TrustManager trustManager = null;

    public NormalX509TrustManager() throws Exception {

        TrustManagerFactory fac = TrustManagerFactory.getInstance("PKIX");
        if (fac == null) {
            throw new NoSuchAlgorithmException();
        }
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("trustedCerts"),
            "passphrase".toCharArray());

        fac.init(ks);
        
        TrustManager[] man = fac.getTrustManagers();
        for (TrustManager tm : man) {
            if (tm instanceof X509TrustManager) {
                trustManager = (X509TrustManager) tm;
                break;
            }
        }
        if (trustManager == null) {
            throw new NoSuchAlgorithmException();
        }
    }

    @Override
    public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {
        trustManager.checkClientTrusted(arg0, arg1);
    }

    @Override
    public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {
        trustManager.checkServerTrusted(arg0, arg1);
    }

    @Override
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        X509Certificate[] certs;
        try {
            certs = getCert();
        } catch (IOException e) {
            return null;
        }
        return certs;
    }

    protected static X509Certificate[] getCert() throws IOException {
        if (certs == null) {
            Globals globs = Globals.getInstance();
            String certUrl = globs.getProperty("server.certificate");
            if (certUrl.equals("standard")) {
                certUrl = "file:///" + globs.getProperty("basedirectory") + File.separator + globs.getProperty("datadirectory")
                        + File.separator + "trustedcert.cert";
                AppLogger.info("Zertifikate werden von " + certUrl + " geladen");
            }
            URL cert = null;
            try {
                cert = new URL(certUrl);
            } catch (Exception e) {
                AppLogger.severe("Konnte Zertifikat nicht laden: " + e.getMessage());
                throw new IOException(e.getMessage());
            }
            InputStream is = cert.openConnection().getInputStream();
            try {
                certs = (X509Certificate[]) (CertificateFactory.getInstance("X.509").generateCertificates(is).toArray());
            } catch (CertificateException e) {
                throw new IOException(e.getMessage());
            }
            is.close();
            AppLogger.info("Zertifikate von " + certUrl + " geladen");
            for (X509Certificate c : certs) {
                AppLogger.info(c.toString());
            }
        }
        return certs;
    }

    protected static X509Certificate[] certs;
}
