package ru.itmo.soa.gateway.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetAddress;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_LOADBALANCER_RESPONSE_ATTR;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter implements GlobalFilter, Ordered {

   private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

   @PostConstruct
   public void init() {
      log.info("gateway logging filter initialized");
   }

   @Override
   public int getOrder() {
      return Ordered.HIGHEST_PRECEDENCE;
   }

   @Override
   public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
      ServerHttpRequest request = exchange.getRequest();
      String method = request.getMethod() != null ? request.getMethod().name() : "?";
      String path = request.getURI().getPath();

      String hostnameTmp = System.getenv("HOSTNAME");
      if (hostnameTmp == null || hostnameTmp.isEmpty()) {
         try {
            hostnameTmp = InetAddress.getLocalHost().getHostName();
         } catch (Exception ignored) {
            hostnameTmp = "unknown-host";
         }
      }
      final String hostname = hostnameTmp;

      return chain.filter(exchange)
            .doFinally(signalType -> {
               ServerHttpResponse response = exchange.getResponse();
               Integer status = response.getStatusCode() != null ? response.getStatusCode().value() : null;

               @SuppressWarnings("unchecked")
               Response<ServiceInstance> lbResponse = (Response<ServiceInstance>) exchange
                     .getAttribute(GATEWAY_LOADBALANCER_RESPONSE_ATTR);

               if (lbResponse != null && lbResponse.hasServer() && lbResponse.getServer() != null) {
                  ServiceInstance chosen = lbResponse.getServer();
                  String target = chosen.getHost() + ":" + chosen.getPort();
                  log.info("gateway instance={} routed {} {} -> {} status {}", hostname, method, path, target, status);
               } else {
                  log.info("gateway instance={} handled {} {} -> status {}", hostname, method, path, status);
               }
            });
   }
}
