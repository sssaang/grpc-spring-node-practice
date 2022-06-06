package me.sssaang.grpcspring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GrpcSpringApplication

fun main(args: Array<String>) {
	runApplication<GrpcSpringApplication>(*args)
}
