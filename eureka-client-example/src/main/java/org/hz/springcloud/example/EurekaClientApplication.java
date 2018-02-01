package org.hz.springcloud.example;

import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.shared.transport.jersey.EurekaJerseyClient;
import com.netflix.discovery.shared.transport.jersey.EurekaJerseyClientImpl;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.GZIPContentEncodingFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.io.UnsupportedEncodingException;
import java.util.Collection;


@SpringBootApplication
@EnableDiscoveryClient
public class EurekaClientApplication {

    @Bean
    public LoggingFilter loggingFilter() {
        return new LoggingFilter();
    }

    public String key = "DiscoveryClient_EUREKA-CLIENT/localhost";

    @Bean
    public IEncryption encryption() throws UnsupportedEncodingException {
        byte[] bytes = key.substring(0, 16).getBytes("UTF-8");
        return new AESEncryption(bytes);
    }

    @Bean
    public EncryptClientFilter encryptClientFilter(IEncryption encryption) {
        return new EncryptClientFilter(encryption);
    }

    //@Bean
    public GZIPContentEncodingFilter gzipContentEncodingFilter() {
        return new GZIPContentEncodingFilter(false);
    }

    @Bean
    public DiscoveryClient.DiscoveryClientOptionalArgs discoveryClientOptionalArgs(Collection<ClientFilter> filters) {
        DiscoveryClient.DiscoveryClientOptionalArgs args = new DiscoveryClient.DiscoveryClientOptionalArgs();
        EurekaJerseyClientImpl.EurekaJerseyClientBuilder clientBuilder = new EurekaJerseyClientImpl.EurekaJerseyClientBuilder()
                .withClientName("DiscoveryClient-HTTPClient")
                .withUserAgent("Java-EurekaClient")
                .withConnectionTimeout(5000)
                .withReadTimeout(8000)
                .withMaxConnectionsPerHost(50)
                .withMaxTotalConnections(200)
                .withConnectionIdleTimeout(30000);
        EurekaJerseyClient jerseyClient = clientBuilder.build();
        args.setEurekaJerseyClient(jerseyClient);
        args.setAdditionalFilters(filters);
        return args;
    }

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientApplication.class, args);
    }

}
