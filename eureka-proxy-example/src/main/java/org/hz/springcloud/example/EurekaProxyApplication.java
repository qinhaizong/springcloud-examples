package org.hz.springcloud.example;

import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.GZIPContentEncodingFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

@SpringBootApplication
public class EurekaProxyApplication extends SpringBootServletInitializer {

    private String proxy = "http://localhost:8761";

    private String key = "DiscoveryClient_EUREKA-CLIENT/localhost";

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(EurekaProxyApplication.class);
    }

    @Bean
    public IEncryption encryption() throws UnsupportedEncodingException {
        byte[] bytes = key.substring(0, 16).getBytes("UTF-8");
        return new AESEncryption(bytes);
    }

    //@Bean
    public GZIPContentEncodingFilter gzipContentEncodingFilter() {
        return new GZIPContentEncodingFilter(false);
    }

    @Bean
    public DecryptClientFilter decryptClientFilter(IEncryption encryption) {
        return new DecryptClientFilter(encryption);
    }

    @Bean
    public LoggingFilter loggingFilter() {
        return new LoggingFilter();
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean(Collection<ClientFilter> filters) {
        return new FilterRegistrationBean(new ProxyFilter(filters, proxy));
    }

    public static void main(String[] args) {
        SpringApplication.run(EurekaProxyApplication.class, args);
    }

}
