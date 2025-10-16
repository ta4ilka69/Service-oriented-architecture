config_lab2:
	mkdir -p $(INST_ROOT)/service1
	mkdir -p $(INST_ROOT)/service2
	cp -r $(WILDFLY_HOME)/standalone/configuration $(INST_ROOT)/service1/
	cp -r $(WILDFLY_HOME)/standalone/deployments   $(INST_ROOT)/service1/

	cp -r $(WILDFLY_HOME)/standalone/configuration $(INST_ROOT)/service2/
	cp -r $(WILDFLY_HOME)/standalone/deployments   $(INST_ROOT)/service2/

	cp ./resources/standalone.xml $(INST_ROOT)/service1/configuration/standalone.xml
	cp ./resources/standalone-grammy.xml $(INST_ROOT)/service2/configuration/standalone.xml

	cd ./music-service && mvn -q -DskipTests package && cd ..
	cp ./music-service/target/music-service.war $(INST_ROOT)/service1/deployments/music-service.war

	cd ./grammy-service && mvn -q -DskipTests package && cd ..
	cp ./grammy-service/target/grammy-service.war $(INST_ROOT)/service2/deployments/grammy-service.war
	cp ./soa-frontend/ui.war $(INST_ROOT)/service2/deployments/ui.war

	cp ../server.p12 $(INST_ROOT)/service1/configuration/application.keystore
	cp ../server2.p12 $(INST_ROOT)/service2/configuration/application.keystore

	keytool -importcert -alias service1-cert -file ../service1.crt -keystore $(INST_ROOT)/service2/configuration/truststore.jks -storepass $(PASS) -noprompt

first_lab2:
	$(WILDFLY_HOME)/bin/standalone.sh -c standalone.xml -Djboss.server.base.dir=$(INST_ROOT)/service1

second_lab2:
	$(WILDFLY_HOME)/bin/standalone.sh -c standalone.xml -Djboss.server.base.dir=$(INST_ROOT)/service2 -Djboss.socket.binding.port-offset=52 -Djavax.net.ssl.trustStore=$(INST_ROOT)/service2/configuration/truststore.jks -Djavax.net.ssl.trustStorePassword=$(PASS) -Dmusic.service.base-url=https://localhost:5252


# ===== Spring Cloud =====
INST_CLOUD?=$(INST_ROOT)/cloud
LOG_DIR?=$(INST_CLOUD)/.logs
PID_DIR?=$(INST_CLOUD)/.pids

cloud_build:
	cd ./config-server && mvn -q -DskipTests package spring-boot:repackage && cd ..
	cd ./eureka-server && mvn -q -DskipTests package spring-boot:repackage && cd ..
	cd ./grammy-service && mvn -q -DskipTests package spring-boot:repackage && cd ..
	cd ./api-gateway && mvn -q -DskipTests package spring-boot:repackage && cd ..

cloud_config: cloud_build
	mkdir -p $(PID_DIR)
	mkdir -p $(LOG_DIR)
	mkdir -p $(INST_CLOUD)/config-server $(INST_CLOUD)/eureka-server $(INST_CLOUD)/grammy-service $(INST_CLOUD)/api-gateway
	mkdir -p $(INST_CLOUD)/grammy-service/configuration
	mkdir -p $(INST_CLOUD)/api-gateway/configuration
	cp ./config-server/target/config-server-*.jar $(INST_CLOUD)/config-server/
	cp ./eureka-server/target/eureka-server-*.jar $(INST_CLOUD)/eureka-server/
	cp ./grammy-service/target/grammy-service.jar $(INST_CLOUD)/grammy-service/
	cp ./api-gateway/target/api-gateway-*.jar $(INST_CLOUD)/api-gateway/
	rm -rf $(INST_CLOUD)/config-repo && cp -r ./config-repo $(INST_CLOUD)/
	cp ../server2.p12 $(INST_CLOUD)/grammy-service/configuration/application.keystore
	keytool -importcert -alias service1-cert -file ../service1.crt -keystore $(INST_CLOUD)/grammy-service/configuration/truststore.jks -storepass $(PASS) -noprompt || true
	cp ../server2.p12 $(INST_CLOUD)/api-gateway/configuration/application.keystore

cloud_up: cloud_config
	java -jar $(INST_CLOUD)/eureka-server/eureka-server-*.jar > $(LOG_DIR)/eureka-server.log 2>&1 & echo $$! > $(PID_DIR)/eureka-server.pid
	sleep 20
	java -Dspring.cloud.config.server.native.search-locations=file:$(INST_CLOUD)/config-repo -jar $(INST_CLOUD)/config-server/config-server-*.jar > $(LOG_DIR)/config-server.log 2>&1 & echo $$! > $(PID_DIR)/config-server.pid
	sleep 20
	java -Djavax.net.ssl.trustStore=$(INST_CLOUD)/grammy-service/configuration/truststore.jks -Djavax.net.ssl.trustStorePassword=$(PASS) -jar $(INST_CLOUD)/grammy-service/grammy-service.jar > $(LOG_DIR)/grammy-service.log 2>&1 & echo $$! > $(PID_DIR)/grammy-service.pid
	sleep 20
	java -Dserver.ssl.enabled=true \
	     -Dserver.ssl.key-store=$(INST_CLOUD)/api-gateway/configuration/application.keystore \
	     -Dserver.ssl.key-store-type=PKCS12 \
	     -Dserver.ssl.key-store-password=$(PASS) \
	     -Dserver.ssl.key-alias=server2 \
	     -jar $(INST_CLOUD)/api-gateway/api-gateway-*.jar > $(LOG_DIR)/api-gateway.log 2>&1 & echo $$! > $(PID_DIR)/api-gateway.pid

cloud_down:
	-@[ -f $(PID_DIR)/api-gateway.pid ] && kill `cat $(PID_DIR)/api-gateway.pid` || true
	-@[ -f $(PID_DIR)/grammy-service.pid ] && kill `cat $(PID_DIR)/grammy-service.pid` || true
	-@[ -f $(PID_DIR)/eureka-server.pid ] && kill `cat $(PID_DIR)/eureka-server.pid` || true
	-@[ -f $(PID_DIR)/config-server.pid ] && kill `cat $(PID_DIR)/config-server.pid` || true
	rm -f $(PID_DIR)/*.pid
	rm -rf $(LOG_DIR)/*.log

cloud_logs:
	tail -f $(LOG_DIR)/*.log

ui_build_war:
	cd ./soa-frontend && npm run build && \
		rm -f ui.war && \
		mkdir -p target-ui && \
		cp -r dist/* target-ui/ && \
		cd target-ui && jar -cf ../ui.war . && cd .. && \
		rm -rf target-ui

ui_config:
	mkdir -p $(INST_ROOT)/service2
	cp -r $(WILDFLY_HOME)/standalone/configuration $(INST_ROOT)/service2/
	cp -r $(WILDFLY_HOME)/standalone/deployments   $(INST_ROOT)/service2/
	cp ./resources/standalone-grammy.xml $(INST_ROOT)/service2/configuration/standalone.xml

deploy_ui_wildfly: ui_config
	cp ./soa-frontend/ui.war $(INST_ROOT)/service2/deployments/ui.war

start_ui_wildfly: deploy_ui_wildfly
	$(WILDFLY_HOME)/bin/standalone.sh -c standalone.xml -Djboss.server.base.dir=$(INST_ROOT)/service2 -Djboss.socket.binding.port-offset=52 -Djavax.net.ssl.trustStore=$(INST_ROOT)/service2/configuration/truststore.jks -Djavax.net.ssl.trustStorePassword=$(PASS)


.PHONY: cloud_build cloud_up cloud_down cloud_logs ui_config ui_build_war deploy_ui_wildfly