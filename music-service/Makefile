config:
	mkdir -p $(INST_ROOT)/service1
	cp -r $(WILDFLY_HOME)/standalone/configuration $(INST_ROOT)/service1/
	cp -r $(WILDFLY_HOME)/standalone/deployments   $(INST_ROOT)/service1/

	cp ../resources/standalone.xml $(INST_ROOT)/service1/configuration/standalone.xml
	cp ../server.p12 $(INST_ROOT)/service1/configuration/application.keystore

build:
	mvn -q -DskipTests -f ./pom.xml clean package

deploy: build
	cp ./music-ejb/target/music-ejb-1.0.0-SNAPSHOT.jar $(INST_ROOT)/service1/deployments/music-ejb.jar
	cp ./music-web/target/music-web.war $(INST_ROOT)/service1/deployments/music-web.war

start:
	$(WILDFLY_HOME)/bin/standalone.sh -c standalone.xml -Djboss.server.base.dir=$(INST_ROOT)/service1 \
		-Dservice.name=music-service -Dservice.address=localhost -Dservice.port=5252 \
		-Dconsul.host=127.0.0.1 -Dconsul.port=8500

stop:
	$(WILDFLY_HOME)/bin/jboss-cli.sh --connect command=:shutdown || true

redeploy:
	rm -f $(INST_ROOT)/service1/deployments/music-ejb.jar || true
	rm -f $(INST_ROOT)/service1/deployments/music-web.war || true
	$(MAKE) deploy

.PHONY: config build deploy start stop redeploy


