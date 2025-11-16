package com.example.di

import com.example.controller.QuoteController
import com.example.repository.DynamoQuoteDao
import com.example.repository.QuoteDao
import com.example.service.QuoteService
import com.example.service.QuoteServiceImpl
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI

class AppModule {
    fun createAppModuleWithDynamo(
        region: Region = Region.US_EAST_1,
        tableName: String = "quotes",
        endpointOverride: String? = null
    ) = module {
        // DynamoDB client configuration
        single {
            val builder = DynamoDbClient.builder()
                .region(region)

            if (!endpointOverride.isNullOrBlank()) {
                builder.endpointOverride(URI.create(endpointOverride))
                // Use dummy credentials for local development
                builder.credentialsProvider(
                    StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("dummy", "dummy")
                    )
                )
            } else {
                // Use the default provider for other environments (e.g., production)
                builder.credentialsProvider(DefaultCredentialsProvider.create())
            }

            builder.build()
        }

        // DynamoDB enhanced client
        single {
            DynamoDbEnhancedClient.builder()
                .dynamoDbClient(get<DynamoDbClient>())
                .build()
        }

        // Repository layer
        single<QuoteDao> {
            DynamoQuoteDao(get(), tableName)
        }

        // Service layer
        single<QuoteService> {
            QuoteServiceImpl(get())
        }

        // Controller layer
        single {
            QuoteController(get())
        }

        // JSON configuration
        single {
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        }
    }
}
