# Service-oriented architecture

## Лабораторная работа №1

Запустить из корня проекта

```shell
python -m http.server ${port}
```

Swagger будет лежать по пути `http://localhost:${port}/src-swagger-ui/`

## Лабораторная работа №2

### Инфраструктура 

Два сервиса будут хостятся на двух инстансах Wildfly. В первом - music service, во втором - grammy service и ui.

### Технологии

Music Service: JAX-RS

Grammy Service: Spring MVC REST


### Порядок действий

1. Поместить в `/grammy-service` pom.xml с нужной конфигурацией, который лежит в [resources/lab2/pom.xml](resources/lab2/pom.xml)
2. Поместить в `/soa-frontend` старую сборка фронта, которая лежит в [resources/lab2/ui.war](resources/lab2/ui.war).
3. Скачать Wildfly версии 30.0.0 Final
4. Установить системные переменные
   ```bash
   export WILDFLY_HOME=[путь до wildfly]
   export INST_ROOT=[путь до директории, где сервисы будут хостятся]
   export PASS=[любой пароль]
   export IP="127.0.0.1"
   ```
5. Произвести работу с сертификатами на 1 уровень выше, чем сам проект
   ```bash
   > cd ..
   > keytool -genkeypair   -alias server   -keyalg RSA -keysize 2048 -validity 825   -storetype PKCS12   -keystore server.p12   -storepass "$PASS" -keypass "$PASS"   -dname "CN=$HOST"   -ext "SAN=dns:$HOST,ip:$IP,dns:localhost,ip:127.0.0.1"
   > keytool -genkeypair   -alias server2   -keyalg RSA -keysize 2048 -validity 825   -storetype PKCS12   -keystore server2.p12   -storepass "$PASS" -keypass "$PASS"   -dname "CN=$HOST"   -ext "SAN=dns:$HOST,ip:$IP,dns:localhost,ip:127.0.0.1"
   > keytool -exportcert -alias server -keystore ../server.p12 -storetype PKCS12 -storepass "$PASS" -rfc -file ../service1.crt
   ```
6. Сконфигурировать сервисы - `make config lab2`
7. Запустить первый сервис - `make first_lab2`
8. Запустить второй сервис и фронт - ` make second_lab2`
9. Принять сертификаты в браузере по пути `https:127.0.0.1:5252/music-bands` и `https:127.0.0.1:5314/ui` (если не приняты)
10. Фронт будет лежать по пути `https:127.0.0.1:5314/ui`


## Лабораторная работа №3

### Инфраструктура 

Все Spring Cloud сервисы будут работать в docker. Music Service, состоящий из ejb и web частей, будут также хоститься в Wildfly. Frontend будет хоститься на отдельном Wildfly.

### Технологии

Music Service: веб-приложение с веб-сервисом и EJB-jar с бизнес-компонентами. Consul

Grammy Service: Spring MVC REST, , Spring Cloud, Config Service, Service Discovery Eureka, Spring Load Balancer, Spring Api Gateway


### Порядок действий 

Дальше будет туториал по запуску сервисов, где docker будет работать локально, а все остальное - на Helios.


UI (Helios)
---

При подключении надо пробросить порт `5314`

1. Повторить п. 3-5 и подраздела [Лабораторная работа №2](#лабораторная-работа-2). К ним добавить `export INST_CLOUD=${INST_ROOT}/cloud`
2. В корне проекта запустить команду конфигурации фронта - `make deploy_ui_wildfly`
3. `export _JAVA_OPTIONS="-Xmx256m -XX:MaxMetaspaceSize=256m"`
4. Запускаем - `make start_ui_wildfly`

Фронт доступен по ссылке `https://localhost:5314`

Spring Cloud (Docker)
---

1. Повторить п. 1 из инструкции UI
2. Скопировать на уровень выше проекта все сертификаты, полученные при конфигурации UI
3. Сконфигурировать Spring Cloud сервисы- `make spring_config`
4. Запустить все сервисы - `docker-compose up -d`

Music Service (Helios)
---
При подключении надо пробросить порт `5252`

1. `export _JAVA_OPTIONS="-Xmx256m -XX:MaxMetaspaceSize=512m"`
2. Из корня запустить конфигурацию сервиса - `make -C  music-service config`
3. `make -C  music-service deploy`
4. `make -C music-service start`
