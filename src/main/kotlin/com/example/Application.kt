package com.example

import com.example.di.AppModule
import com.example.routing.configureRouting
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement
import software.amazon.awssdk.services.dynamodb.model.KeyType
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType

/**
 * Main application module that configures Ktor server with DI, routing, and database setup.
 */
fun Application.mainModule() {
    log.info("Initializing application module")

    val conf = environment.config
    val region = Region.of(conf.property("aws.region").getString())
    val endpoint = conf.propertyOrNull("aws.endpoint")?.getString()
    val tableName = conf.propertyOrNull("aws.tableName")?.getString() ?: "quotes"

    log.info("Configuring AWS DynamoDB with region: $region, tableName: $tableName")

    val appModule = AppModule().createAppModuleWithDynamo(
        region = region,
        tableName = tableName,
        endpointOverride = endpoint
    )

    log.info("Installing Koin dependency injection")
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
    log.info("Koin installed successfully")
    
    val dynamoDbClient = get<DynamoDbClient>()
    val jsonConfig = get<Json>()
    
    createTableIfNotExists(dynamoDbClient, tableName)

    log.info("Installing ContentNegotiation plugin")
    install(ContentNegotiation) {
        json(jsonConfig)
        ignoreType<FreeMarkerContent>()
    }
    
    configureRouting()
    log.info("Application module initialization complete")
}

/**
 * Creates DynamoDB table if it doesn't exist.
 * @param dynamoDbClient The DynamoDB client
 * @param tableName The name of the table to create
 */
private fun createTableIfNotExists(dynamoDbClient: DynamoDbClient, tableName: String) {
    try {
        val listTablesRequest = ListTablesRequest.builder().build()
        val tables = dynamoDbClient.listTables(listTablesRequest).tableNames()

        if (!tables.contains(tableName)) {
            val createTableRequest = CreateTableRequest.builder()
                .tableName(tableName)
                .keySchema(
                    KeySchemaElement.builder()
                        .attributeName("id")
                        .keyType(KeyType.HASH)
                        .build()
                )
                .attributeDefinitions(
                    AttributeDefinition.builder()
                        .attributeName("id")
                        .attributeType(ScalarAttributeType.S)
                        .build()
                )
                .provisionedThroughput(
                    ProvisionedThroughput.builder()
                        .readCapacityUnits(5)
                        .writeCapacityUnits(5)
                        .build()
                )
                .build()

            dynamoDbClient.createTable(createTableRequest)
        }
    } catch (e: Exception) {
        throw RuntimeException("Failed to create DynamoDB table: $tableName", e)
    }
}
