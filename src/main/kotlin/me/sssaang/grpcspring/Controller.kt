package me.sssaang.grpcspring

import me.sssaang.sample.Sample.SampleRequest
import me.sssaang.sample.Sample.SampleResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class Controller(private val service: Service) {

    @GetMapping("/hi/{name}")
    fun handleHi(@PathVariable("name") name: String,) = service.hi(name)

    @GetMapping("/message/{message}")
    suspend fun handleMessageReceive(@PathVariable("message") message: String): String =
        "message ${message}"
}