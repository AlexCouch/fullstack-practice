import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.kotlinx.serializer.*
import io.ktor.client.request.*
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.document
import kotlinx.browser.window
import react.create
import react.dom.client.createRoot
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.body
import react.dom.render

val endpoint = window.location.origin

val jsonClient = HttpClient{
    install(ContentNegotiation){
        json()
    }
}

suspend fun getShoppingList(): List<ShoppingListItem>{
    return jsonClient.get(endpoint + ShoppingListItem.path).body()
}

suspend fun addShoppingListItem(shoppingListItem: ShoppingListItem){
    jsonClient.post(endpoint + ShoppingListItem.path){
        contentType(ContentType.Application.Json)
        setBody(shoppingListItem)
    }
}

suspend fun deleteShoppingListItem(shoppingListItem: ShoppingListItem){
    jsonClient.delete(endpoint + ShoppingListItem.path + "/${shoppingListItem.id}")
}

fun main() {
    val container = document.getElementById("root") ?: error("Couldn't find container!")
    render(App.create(), container)
}