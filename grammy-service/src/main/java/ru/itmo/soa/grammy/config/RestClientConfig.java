package ru.itmo.soa.grammy.config;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.apache.hc.client5.http.config.RequestConfig;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestClientConfig {

        @Bean
        public XmlMapper xmlMapper() {
                return new XmlMapper();
        }

        @Bean
        public RestTemplate restTemplate(RestTemplateBuilder builder, XmlMapper xmlMapper)
                        throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
                TrustStrategy trustAll = (chain, authType) -> true;
                SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, trustAll).build();
                SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext,
                                NoopHostnameVerifier.INSTANCE);

                Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                                .<ConnectionSocketFactory>create()
                                .register("https", sslSocketFactory)
                                .register("http", new PlainConnectionSocketFactory())
                                .build();

                PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                                socketFactoryRegistry);

                RequestConfig requestConfig = RequestConfig.custom()
                                .setConnectionRequestTimeout(Timeout.ofSeconds(5))
                                .setResponseTimeout(10, TimeUnit.SECONDS)
                                .build();

                CloseableHttpClient httpClient = HttpClients.custom()
                                .setConnectionManager(connectionManager)
                                .setDefaultRequestConfig(requestConfig)
                                .build();

                HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
                                httpClient);

                RestTemplate restTemplate = builder
                                .requestFactory(() -> requestFactory)
                                .build();

                List<HttpMessageConverter<?>> converters = new ArrayList<>(restTemplate.getMessageConverters());
                converters.removeIf(c -> c instanceof MappingJackson2XmlHttpMessageConverter);
                converters.add(new MappingJackson2XmlHttpMessageConverter(xmlMapper));
                restTemplate.setMessageConverters(converters);
                return restTemplate;
        }
}