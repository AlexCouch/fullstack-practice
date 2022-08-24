package org.example.application

import ShoppingListItem
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

fun HTML.index() {
    head {
        title("Hello from Ktor!")
    }
    body {
        div {
            id = "root"
        }
        script(src = "fullstack-practice.js") {}
    }
}

val client = KMongo.createClient().coroutine
val database = client.getDatabase("shoppingList")
val collection = database.getCollection<ShoppingListItem>()

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        install(ContentNegotiation){
            json()
        }
        install(CORS){
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Delete)
            anyHost()
        }
        install(Compression){
            gzip()
        }
        routing{
            route("/"){
                get{
                    call.respondHtml { index() }
                }
            }
            static("/"){
                resources("")
            }
            route(ShoppingListItem.path) {
                get{
                    call.respond(collection.find().toList())
                }
                post {
                    collection.insertOne(call.receive<ShoppingListItem>())
                    call.respond(HttpStatusCode.OK)
                }
                delete("/{id}"){
                    val id = call.parameters["id"]?.toInt() ?: error("Invalid delete request")
                    collection.deleteOne(ShoppingListItem::id eq id)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }.start(wait = true)
}