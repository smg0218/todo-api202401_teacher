```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '2.7.12'
    id 'io.spring.dependency-management' version '1.0.15.RELEASE'
}

group = 'com.study'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    compileOnly 'org.projectlombok:lombok'
    runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'

    // 쿼리파라미터 추가 외부로그 남기기
    implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.5.8'

}

tasks.named('test') {
    useJUnitPlatform()
}
```
