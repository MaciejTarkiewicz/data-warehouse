package com.warehouse.configuration

import io.micronaut.test.support.TestPropertyProvider
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.DockerImageName
import spock.lang.Shared
import spock.lang.Specification

@Testcontainers
abstract class TestContainerFixture extends Specification implements TestPropertyProvider {

    private static final String MONGO__HOST = "MONGO_HOST"
    private static final String MONGO__PORT = "MONGO_PORT"
    private static final int MONGO_PORT = 27017

    private static final String MONGO_IMAGE_NAME = "mongo:4.0.10"

    @Shared
    MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE_NAME))

    @Override
    Map<String, String> getProperties() {
        Map.of(MONGO__HOST, mongoDBContainer.containerIpAddress as String,
                MONGO__PORT, "${mongoDBContainer.getMappedPort(MONGO_PORT)}" as String
        )
    }
}
