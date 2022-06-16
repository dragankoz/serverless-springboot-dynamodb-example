package com.company.aws.example.product.handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import com.company.aws.example.product.persistence.repositories.ProductRepository;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.DescribeTableEnhancedResponse;

@Slf4j
public abstract class AbstractProductsFunctionTest {
    private static Random random = new Random();

    private static Process process;

    protected abstract DynamoDbEnhancedClient getDbClient();

    @BeforeEach
    public void clearDB() {
        try {
            DescribeTableEnhancedResponse response = getDbClient().table(ProductRepository.TABLE_NAME, ProductRepository.TABLE_SCHEMA).describeTable();
            log.info("Table exists, deleting [{}]", response.table().tableName());
            getDbClient().table(ProductRepository.TABLE_NAME, ProductRepository.TABLE_SCHEMA).deleteTable();
        } catch (Exception ex) {
            log.info("No table exists, lets create");
        }
        getDbClient().table(ProductRepository.TABLE_NAME, ProductRepository.TABLE_SCHEMA).createTable();
    }

    @BeforeAll
    public static void setupBefore() throws Exception {
        int port = allocateRandomPort("dynamodb", null);
        System.setProperty("amazon.dynamodb.endpoint", "http://localhost:" + port);

//        System.getProperties().forEach((key, value) -> log.info("prop k: {} v: {}", key, value));
//        System.getenv().forEach((key, value) -> log.info("env k: {} v: {}", key, value));

        File dynamodbDir;
        if (StringUtils.hasText(System.getProperties().getProperty(" dynamodb-local-dir"))) {
            dynamodbDir = new File(System.getProperties().getProperty(" dynamodb-local-dir"));
        } else if (StringUtils.hasText(System.getProperties().getProperty("basedir")))  {
            // We're running under maven
            dynamodbDir = new File(System.getProperties().getProperty("basedir") + File.separator + "target" + File.separator + "dynamodb-local");
        } else if (StringUtils.hasText(System.getenv().get("MAVEN_PROJECTBASEDIR"))) {
            // @TODO We're running under native test
            dynamodbDir = new File(System.getenv().get("MAVEN_PROJECTBASEDIR") + File.separator + "target" + File.separator + "dynamodb-local");
        } else {
            // We're running under intellij
            String endMatching = "target" + File.separator + "test-classes";
            String targetDir = Arrays.stream(System.getProperty("java.class.path").split(File.pathSeparator))
                    .filter(s -> s.endsWith(endMatching))
                    .findFirst()
                    .map(s -> s.replace(endMatching, "target"))
                    .orElse(null);
            if (targetDir == null) {
                throw new IllegalStateException("Could not locate project /target dir");
            }
            dynamodbDir = new File(targetDir + File.separator + "dynamodb-local");
        }
        ProcessBuilder processBuilder = new ProcessBuilder("java",
                                                           "-Djava.library.path=" + dynamodbDir.getAbsolutePath() + File.separator + "DynamoDBLocal_lib",
                                                           "-jar",
                                                           dynamodbDir.getAbsolutePath() + File.separator + "DynamoDBLocal.jar",
                                                           "-sharedDb",
                                                           "-inMemory",
                                                           "-port",
                                                           String.valueOf(port))
                .inheritIO()
                .directory(dynamodbDir);
        log.info("Starting dynamodb local: [{}]", String.join(" ", processBuilder.command()));
        process = processBuilder.start();
    }

    @AfterAll
    public static void tearDownAfter() {
        if (process != null) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Shutdown DynamoDBLocal");
                process.destroy();
                try {
                    process.waitFor(3, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    log.error("DynamoDBLocal Process did not terminate after 3 seconds.");
                }
                log.info("DynamoDBLocal isAlive [{}]", process.isAlive());
            }));
        }
    }

    public static int generateRandomIntIntRange(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public static synchronized int allocateRandomPort(String serviceName, String portOverride) {
        String currentSetting = System.getProperty(serviceName);
        int openPort = -1;
        if (!StringUtils.hasText(portOverride)) {
            if (!StringUtils.hasText(currentSetting)) {
                for (int cnt = 0; cnt < 10; ++cnt) {
                    int randomNum = generateRandomIntIntRange(49152, 65535);
                    try (ServerSocket socket = new ServerSocket(randomNum)) {
                        openPort = socket.getLocalPort();
                        break;
                    } catch (IOException ioex) {
                        log.info("Found used port [{}], retrying", randomNum);
                    }
                }
                if (openPort < 0) {
                    log.warn("Could not find open port for [{}]", serviceName);
                }
            } else {
                openPort = Integer.parseInt(currentSetting.split(":")[1]);
                log.info("Found open port [{}]:[{}]", serviceName, openPort);
            }
        } else {
            // Try to open open override port
            int port = Integer.parseInt(portOverride);
            try (ServerSocket socket = new ServerSocket(port)) {
                openPort = socket.getLocalPort();
            } catch (IOException ioex) {
                log.warn("Could not find open port for [{}]", serviceName);
            }
        }
        if (StringUtils.hasText(currentSetting)) {
            if (!currentSetting.equals("localhost:" + openPort)) {
                System.setProperty(serviceName, "localhost:" + openPort);
                log.info("Overriding endpoint {} {}", serviceName, System.getProperty(serviceName));
            }
        } else {
            System.setProperty(serviceName, "localhost:" + openPort);
            log.info("Setting endpoint {} {}", serviceName, System.getProperty(serviceName));
        }
        return openPort;
    }

    public static String getTestContent(Class<?> clazz, String fileName, String fileExt) throws FileNotFoundException {
        return readClasspathResource("classpath:" + String.format("%s/%s%s", clazz.getSimpleName(), fileName, fileExt == null ? "" : "." + fileExt));
    }

    public static String readClasspathResource(String classpathResource) throws FileNotFoundException {
        return new Scanner(ResourceUtils.getFile(classpathResource)).useDelimiter("\\Z").next();
    }

}