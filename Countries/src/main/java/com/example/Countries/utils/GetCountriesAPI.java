package com.example.Countries.utils;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class GetCountriesAPI {

    @Autowired
    WebClient.Builder builder;

    public CompletableFuture<String> getCountryData(String url) {
        WebClient webClient = builder.clientConnector(new ReactorClientHttpConnector(httpClient())).build();

        CompletableFuture<String> responseBody = webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .toFuture().orTimeout(30000, TimeUnit.MILLISECONDS);

        return responseBody;
    }

    private HttpClient httpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000) // 30 seconds
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(30))) // 30 seconds
                .secure(sslContextSpec -> sslContextSpec.sslContext(createSSLContext()))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000); // 30 seconds
    }

    private SslContext createSSLContext() {
        try {
            return SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE) // Use for testing only, to accept any certificate
                    .build();
        } catch (SSLException e) {
            // Handle SSL context creation failure
            e.printStackTrace();
            return null;
        }
    }
}
