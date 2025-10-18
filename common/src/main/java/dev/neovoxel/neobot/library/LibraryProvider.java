package dev.neovoxel.neobot.library;

import dev.neovoxel.jarflow.JarFlow;
import dev.neovoxel.jarflow.dependency.Dependency;
import dev.neovoxel.jarflow.repository.Repository;
import dev.neovoxel.jarflow.util.ExternalLoader;
import dev.neovoxel.neobot.NeoBot;
import dev.neovoxel.neobot.util.ListUtil;
import dev.neovoxel.nsapi.util.DatabaseStorageType;

import java.io.File;

public interface LibraryProvider {
    default void loadBasicLibrary(NeoBot plugin) throws Throwable {
        ExternalLoader.setClassLoader(Thread.currentThread().getContextClassLoader());
        JarFlow.setLibDir(new File(plugin.getDataFolder(), "libs"));
        Dependency nbApi = Dependency.builder()
                .groupId("dev.neovoxel.nbapi")
                .artifactId("NeoBotAPI")
                .version("1.2.1")
                .build();
        Dependency hikariCp;
        if (Float.parseFloat(System.getProperty("java.specification.version")) < 11) {
            hikariCp = Dependency.builder()
                    .groupId("com.zaxxer")
                    .artifactId("HikariCP")
                    .version("4.0.3")
                    .build();
        } else {
            hikariCp = Dependency.builder()
                    .groupId("com.zaxxer")
                    .artifactId("HikariCP")
                    .version("7.0.2")
                    .build();
        }
        Dependency nsApi = Dependency.builder()
                .groupId("dev.neovoxel.nsapi")
                .artifactId("NeoStorageAPI")
                .version("1.0.0")
                .build();
        JarFlow.addRepository(Repository.mavenCentral());
        JarFlow.addRepository(Repository.builder().url("https://maven.aliyun.com/repository/public").build());
        JarFlow.loadDependencies(ListUtil.of(nbApi, hikariCp, nsApi));
        if (Float.parseFloat(System.getProperty("java.specification.version")) < 17) {
            Dependency js = Dependency.builder()
                    .groupId("org.graalvm.js")
                    .artifactId("js")
                    .version("22.0.0.2")
                    .build();
            JarFlow.loadDependency(js);
        } else {
            Dependency js = Dependency.builder()
                    .groupId("org.graalvm.polyglot")
                    .artifactId("js")
                    .version("25.0.0")
                    .build();
            Dependency polyglot = Dependency.builder()
                    .groupId("org.graalvm.polyglot")
                    .artifactId("polyglot")
                    .version("25.0.0")
                    .build();
            JarFlow.loadDependencies(ListUtil.of(js, polyglot));
        }
    }

    default void loadStorageLibrary(DatabaseStorageType type) throws Throwable {
        Dependency storage;
        if (type == DatabaseStorageType.SQLITE) {
            storage = Dependency.builder()
                    .groupId("org.xerial")
                    .artifactId("sqlite-jdbc")
                    .version("3.50.3.0")
                    .build();
        } else if (type == DatabaseStorageType.H2) {
            storage = Dependency.builder()
                    .groupId("com.h2database")
                    .artifactId("h2")
                    .version("2.4.240")
                    .build();
        } else if (type == DatabaseStorageType.MYSQL) {
            storage = Dependency.builder()
                    .groupId("com.mysql")
                    .artifactId("mysql-connector-j")
                    .version("8.2.0")
                    .build();
        } else if (type == DatabaseStorageType.MARIADB) {
            storage = Dependency.builder()
                    .groupId("org.mariadb.jdbc")
                    .artifactId("mariadb-java-client")
                    .version("3.1.2")
                    .build();
        } else {
            storage = Dependency.builder()
                    .groupId("org.postgresql")
                    .artifactId("postgresql")
                    .version("42.7.8")
                    .build();
        }
        JarFlow.loadDependency(storage);
    }
}
