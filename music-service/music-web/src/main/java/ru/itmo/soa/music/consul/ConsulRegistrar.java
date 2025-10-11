package ru.itmo.soa.music.consul;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

@Singleton
@Startup
public class ConsulRegistrar {

    private String consulUrl;
    private String serviceId;
    private Timer timer;

    @PostConstruct
    public void init() {
        String consulHost = System.getProperty("consul.host", System.getenv().getOrDefault("CONSUL_HOST", "127.0.0.1"));
        String consulPort = System.getProperty("consul.port", System.getenv().getOrDefault("CONSUL_PORT", "8500"));
        String serviceName = System.getProperty("service.name", System.getenv().getOrDefault("SERVICE_NAME", "music-service"));
        String serviceAddress = System.getProperty("service.address", System.getenv().getOrDefault("SERVICE_ADDRESS", "127.0.0.1"));
        String servicePort = System.getProperty("service.port", System.getenv().getOrDefault("SERVICE_PORT", "5252"));

        this.consulUrl = "http://" + consulHost + ":" + consulPort;
        this.serviceId = serviceName + "-" + servicePort;

        String registerPayload = "{\n" +
                "  \"ID\": \"" + serviceId + "\",\n" +
                "  \"Name\": \"" + serviceName + "\",\n" +
                "  \"Address\": \"" + serviceAddress + "\",\n" +
                "  \"Port\": " + servicePort + ",\n" +
                "  \"Tags\": [\"https\"],\n" +
                "  \"Check\": {\n" +
                "    \"TTL\": \"15s\",\n" +
                "    \"DeregisterCriticalServiceAfter\": \"1m\"\n" +
                "  }\n" +
                "}";

        try {
            httpPut(consulUrl + "/v1/agent/service/register", registerPayload);
        } catch (IOException e) {
            // Best-effort; service can still run without consul
        }

        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    httpPut(consulUrl + "/v1/agent/check/pass/service:" + serviceId, "OK");
                } catch (IOException ignored) {
                }
            }
        }, 5000, 5000);
    }

    @PreDestroy
    public void shutdown() {
        if (timer != null) {
            timer.cancel();
        }
        try {
            httpPut(consulUrl + "/v1/agent/service/deregister/" + serviceId, "");
        } catch (IOException ignored) {
        }
    }

    private void httpPut(String url, String body) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("PUT");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        if (body != null && !body.isEmpty()) {
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            conn.getOutputStream().write(bytes);
        }
        int code = conn.getResponseCode();
        conn.disconnect();
    }
}
