plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
}

group = properties["group"]!!
version = properties["version"]!!

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.gradleup.shadow")

    group = properties["group"]!!
    version = properties["version"]!!

    repositories {
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        // basic
        implementation("dev.neovoxel.jarflow:JarFlow:1.3.1")
        compileOnly("org.java-websocket:Java-WebSocket:1.6.0")
        compileOnly("dev.neovoxel.nbapi:NeoBotAPI:1.2.1")
        compileOnly("org.json:json:20250517")
        compileOnly("org.slf4j:slf4j-api:2.0.17")
        compileOnly("org.graalvm.js:js:22.0.0.2")

        // storage
        compileOnly("dev.neovoxel.nsapi:NeoStorageAPI:1.0.0")
        compileOnly("com.zaxxer:HikariCP:4.0.3")
        compileOnly("com.mysql:mysql-connector-j:8.2.0")
        compileOnly("org.mariadb.jdbc:mariadb-java-client:3.5.6")
        compileOnly("org.postgresql:postgresql:42.7.8")
        compileOnly("com.h2database:h2:2.4.240")
        compileOnly("org.xerial:sqlite-jdbc:3.50.3.0")

        // annotations
        compileOnly("org.projectlombok:lombok:1.18.42")
        annotationProcessor("org.projectlombok:lombok:1.18.42")
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.jar {
        archiveFileName.set("NeoBot-${archiveFileName.get()}")
    }

    tasks.shadowJar {
        archiveFileName.set("NeoBot-${archiveFileName.get()}")
        relocate("org.bstats", "dev.neovoxel.neobot.libs.bstats")
        relocate("org.json", "dev.neovoxel.neobot.libs.json")
    }


}