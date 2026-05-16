package it.unibo.almamensa.data.repositories

import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import it.unibo.almamensa.BuildConfig
import it.unibo.almamensa.data.model.Canteen
import it.unibo.almamensa.data.model.CanteenDistance
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

interface DistanceRepository {
    suspend fun getDistances(userLat: Double, userLon: Double, canteens: List<Canteen>): List<CanteenDistance>
}

class DistanceRepositoryImpl(private val httpClient: HttpClient) : DistanceRepository {
    override suspend fun getDistances(userLat: Double, userLon: Double, canteens: List<Canteen>): List<CanteenDistance> {
        val locations = buildJsonArray {
            add(buildJsonArray { add(userLon); add(userLat) })
            canteens.forEach { add(buildJsonArray { add(it.longitude); add(it.latitude) }) }
        }
        val body = buildJsonObject {
            put("locations", locations)
            put("sources", buildJsonArray { add(0) })
            put("destinations", buildJsonArray { for (i in 1..canteens.size) add(i) })
            put("metrics", buildJsonArray { add("distance"); add("duration") })
        }
        val response = httpClient.post("https://api.openrouteservice.org/v2/matrix/foot-walking") {
            header("Authorization", BuildConfig.ORS_API_KEY)
            contentType(ContentType.Application.Json)
            setBody(body.toString())
        }
        val responseText = response.bodyAsText()
        val json = Json.parseToJsonElement(responseText)
        val distances = json.jsonObject["distances"]
            ?: throw Exception("ORS error: $responseText")
        val durations = json.jsonObject["durations"]
            ?: throw Exception("ORS error: $responseText")

        return canteens.mapIndexed { index, canteen ->
            CanteenDistance(
                canteen = canteen,
                distanceMeters = distances.jsonArray[0].jsonArray[index].jsonPrimitive.double,
                durationSeconds = durations.jsonArray[0].jsonArray[index].jsonPrimitive.double
            )
        }
    }
}