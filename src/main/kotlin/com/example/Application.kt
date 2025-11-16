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

fun Application.mainModule() {
    // immediate proof this function runs
    println("mainModule invoked")

    val conf = environment.config
    val region = Region.of(conf.property("aws.region").getString())
    val endpoint = conf.propertyOrNull("aws.endpoint")?.getString()
    val tableName = conf.propertyOrNull("aws.tableName")?.getString() ?: "quotes"

    val appModule = AppModule().createAppModuleWithDynamo(
        region = region,
        tableName = tableName,
        endpointOverride = endpoint
    )

    log.warn("Installing Koin")
    install(Koin) {
        slf4jLogger() // show Koin startup logs
        modules(appModule)
    }
    log.warn("Koin installed")
    val dynamoDbClient = get<DynamoDbClient>()
    val jsonConfig = get<Json>()
    createTableIfNotExists(dynamoDbClient, tableName)

    // Install ContentNegotiation and use the Json bean from Koin
    install(ContentNegotiation) {
        json(jsonConfig)
        ignoreType<FreeMarkerContent>()
    }
    configureRouting()
}

private fun createTableIfNotExists(dynamoDbClient: DynamoDbClient, tableName: String) {
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
}
