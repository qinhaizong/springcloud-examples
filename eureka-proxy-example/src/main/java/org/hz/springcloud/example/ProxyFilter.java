package org.hz.springcloud.example;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.client.impl.ClientRequestImpl;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class ProxyFilter implements Filter {

    private final String proxy;
    private final Collection<ClientFilter> filters;

    public ProxyFilter(Collection<ClientFilter> filters, String proxy) {
        this.filters = filters;
        this.proxy = proxy;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if (true) {
            String queryString = request.getQueryString();
            StringBuffer buffer = new StringBuffer(proxy).append(request.getRequestURI());
            if (StringUtils.hasText(queryString)) {
                buffer.append("?").append(queryString);
            }
            URI uri = UriBuilder.fromUri(buffer.toString()).build();
            String method = request.getMethod();
            ClientRequestImpl clientRequest = new ClientRequestImpl(uri, method);
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String header = headerNames.nextElement();
                Enumeration<String> headers = request.getHeaders(header);
                while (headers.hasMoreElements()) {
                    String value = headers.nextElement();
                    clientRequest.getHeaders().add(header, value);
                }
            }
            if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
                clientRequest.setEntity(request.getInputStream());
            }
            Client client = Client.create();
            if (!CollectionUtils.isEmpty(filters)) {
                for (ClientFilter cf : filters) {
                    client.addFilter(cf);
                }
            }
            ClientResponse clientResponse = client.getHeadHandler().handle(clientRequest);
            MultivaluedMap<String, String> headers = clientResponse.getHeaders();
            for (Map.Entry<String, List<String>> e : headers.entrySet()) {
                for (String value : e.getValue()) {
                    response.addHeader(e.getKey(), value);
                }
            }
            if (clientResponse.hasEntity()) {
                StreamUtils.copy(clientResponse.getEntityInputStream(), response.getOutputStream());
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
