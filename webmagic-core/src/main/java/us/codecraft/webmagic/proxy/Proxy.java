package us.codecraft.webmagic.proxy;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class Proxy {

    private final String scheme;

    private final String host;

    private final int port;

    private final String username;

    private final String password;

    public Proxy(String host, int port, String scheme, String username, String password) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public Proxy(String host, int port) {
        this(host, port, null);
    }

    public Proxy(String host, int port, String scheme) {
        this(host, port, scheme, null, null);
    }

    public Proxy(String host, int port, String username, String password) {
        this(host, port, null, username, password);
    }

    public static Proxy create(final URI uri) {
        String userInfo = uri.getUserInfo();
        String username = null;
        String password = null;
        if (userInfo != null) {
            String[] up = userInfo.split(":");
            if (up.length == 1) {
                username = up[0].isEmpty() ? null : up[0];
            } else {
                username = up[0].isEmpty() ? null : up[0];
                password = up[1].isEmpty() ? null : up[1];
            }
        }
        return new Proxy(uri.getHost(), uri.getPort(), uri.getScheme(), username, password);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getScheme(){return scheme;}

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public URI toURI() {
        final StringBuilder userInfoBuffer = new StringBuilder();
        if (username != null) {
            userInfoBuffer.append(urlencode(username));
        }
        if (password != null) {
            userInfoBuffer.append(":").append(urlencode(password));
        }
        final String userInfo = StringUtils.defaultIfEmpty(userInfoBuffer.toString(), null);
        URI uri;
        try {
            uri = new URI(scheme, userInfo, host, port, null, null, null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return uri;
    }

    private String urlencode(String s) {
        String enc = StandardCharsets.UTF_8.name();
        try {
            return URLEncoder.encode(s, enc);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Proxy proxy = (Proxy) o;

        if (port != proxy.port) return false;
        if (host != null ? !host.equals(proxy.host) : proxy.host != null) return false;
        if (scheme != null ? !scheme.equals(proxy.scheme) : proxy.scheme != null) return false;
        if (username != null ? !username.equals(proxy.username) : proxy.username != null) return false;
        return password != null ? password.equals(proxy.password) : proxy.password == null;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + port;
        result = 31 * result + (scheme != null ? scheme.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return this.toURI().toString();
    }

}
