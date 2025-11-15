MULE_HOME ?= /home/studs/s367854/mule-enterprise-standalone-4.10.1
MULE_TRUSTSTORE ?= $(MULE_HOME)/conf/truststore.jks
MULE_TRUSTSTORE_PASS ?= q23886000
MULE_CERT ?= ../service1.crt
MULE_APP_NAME ?= music-proxy

config:
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


mule-truststore:
	keytool -importcert -alias service1 -file $(MULE_CERT) -keystore $(MULE_TRUSTSTORE) -storepass $(MULE_TRUSTSTORE_PASS) -noprompt || true
	keytool -list -keystore $(MULE_TRUSTSTORE) -storepass $(MULE_TRUSTSTORE_PASS) | grep service1 || true

mule-start:
	$(MULE_HOME)/bin/mule start

mule-console:
	$(MULE_HOME)/bin/mule console

mule-stop:
	$(MULE_HOME)/bin/mule stop

mule-keystore:
	cp ../server2.p12 $(MULE_HOME)/conf/server2.p12

mule-deploy:
	rm -rf $(MULE_HOME)/apps/$(MULE_APP_NAME)
	# Prefer packaged app if available
	if [ -f ./mule-proxy/target/$(MULE_APP_NAME)-1.0.0-mule-application.jar ]; then \
		cp ./mule-proxy/target/$(MULE_APP_NAME)-1.0.0-mule-application.jar $(MULE_HOME)/apps/; \
	elif [ -f ./mule-proxy/$(MULE_APP_NAME)-1.0.0-mule-application.jar ]; then \
		cp ./mule-proxy/$(MULE_APP_NAME)-1.0.0-mule-application.jar $(MULE_HOME)/apps/; \
	else \
		mkdir -p $(MULE_HOME)/apps/$(MULE_APP_NAME); \
		cp -r ./mule-proxy/* $(MULE_HOME)/apps/$(MULE_APP_NAME)/; \
	fi

mule-plugins:
	# Try to collect connector plugins from runtime if present
	mkdir -p $(MULE_HOME)/apps/$(MULE_APP_NAME)/plugins
	HTTP_JAR=$$(find $(MULE_HOME) -type f -name "mule-http-connector-*.jar" | head -n1); \
	WSC_JAR=$$(find $(MULE_HOME) -type f -name "mule-wsc-connector-*.jar" | head -n1); \
	if [ -n "$$HTTP_JAR" ]; then cp "$$HTTP_JAR" $(MULE_HOME)/apps/$(MULE_APP_NAME)/plugins/; fi; \
	if [ -n "$$WSC_JAR" ]; then cp "$$WSC_JAR" $(MULE_HOME)/apps/$(MULE_APP_NAME)/plugins/; fi

mule-build:
	cd ./mule-proxy && mvn -q -DskipTests package && cd ..

mule: mule-truststore mule-keystore mule-deploy mule-plugins mule-start

first:
	$(WILDFLY_HOME)/bin/standalone.sh -c standalone.xml -Djboss.server.base.dir=$(INST_ROOT)/service1

second:
	$(WILDFLY_HOME)/bin/standalone.sh -c standalone.xml -Djboss.server.base.dir=$(INST_ROOT)/service2 -Djboss.socket.binding.port-offset=52 -Djavax.net.ssl.trustStore=$(INST_ROOT)/service2/configuration/truststore.jks -Djavax.net.ssl.trustStorePassword=$(PASS) -Dmusic.service.base-url=https://localhost:5252

.PHONY: config first second mule mule-start mule-stop mule-console mule-truststore mule-deploy mule-keystore

