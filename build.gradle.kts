plugins {
    `kotlin-conventions`
    id("delta-coverage-conventions")
}

allprojects {
    group = "systems.ajax.malov"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
        maven {
            setUrl("https://packages.confluent.io/maven/")
        }
        maven {
            url = uri(extra["repository"].toString())
            credentials(AwsCredentials::class.java) {
                accessKey = extra["AWS_ACCESS_KEY_ID"].toString()
                secretKey = extra["AWS_SECRET_ACCESS_KEY"].toString()
            }
        }
    }
}

tasks.named("check") {
    dependsOn("deltaCoverage")
    dependsOn("detektMain")
    dependsOn("detektTest")
}

allprojects {
    configurations.all {
        exclude(group = "org.mockito", module = "mockito-core")
    }
}
