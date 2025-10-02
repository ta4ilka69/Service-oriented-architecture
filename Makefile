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

first:
	$(WILDFLY_HOME)/bin/standalone.sh -c standalone.xml -Djboss.server.base.dir=$(INST_ROOT)/service1

second:
	$(WILDFLY_HOME)/bin/standalone.sh -c standalone.xml -Djboss.server.base.dir=$(INST_ROOT)/service2 -Djboss.socket.binding.port-offset=52 -Djavax.net.ssl.trustStore=$(INST_ROOT)/service2/configuration/truststore.jks -Djavax.net.ssl.trustStorePassword=$(PASS) -Dmusic.service.base-url=https://localhost:5252

.PHONY: config first second

