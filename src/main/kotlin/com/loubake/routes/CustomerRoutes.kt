package com.loubake.routes

import com.loubake.data.FakeDatabase
import com.loubake.models.Customer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.customerRouting() {
    route("/customer") {
        get {
            if (FakeDatabase.customerStorage.isNotEmpty()) {
                call.respond(FakeDatabase.customerStorage)
            } else {
                call.respondText(
                    "No customers found",
                    status = HttpStatusCode.OK
                )
            }
        }

        get("{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.OK
            )
            val customer = FakeDatabase.customerStorage.find { it.id == id } ?: return@get call.respondText(
                "No customer with id: $id",
                status = HttpStatusCode.NotFound
            )
            call.respond(customer)
        }

        post {
            val customer = call.receive<Customer>()
            if (FakeDatabase.customerStorage.find { it.id == customer.id } != null) {
                call.respondText(
                    "Customer with id: ${customer.id} already exists... Customer not stored",
                    status = HttpStatusCode.BadRequest
                )
            } else {
                FakeDatabase.customerStorage.add(customer)
                call.respondText(
                    "Customer stored correctly",
                    status = HttpStatusCode.Created
                )
            }
        }

        delete("{id?}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (FakeDatabase.customerStorage.removeIf { it.id == id }) {
                call.respondText(
                    "Customer removed correctly",
                    status = HttpStatusCode.Accepted
                )
            } else {
                call.respondText(
                    "Not Found",
                    status = HttpStatusCode.NotFound
                )
            }
        }
    }
}
