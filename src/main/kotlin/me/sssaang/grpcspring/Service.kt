package me.sssaang.grpcspring

import io.grpc.stub.StreamObserver
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.sssaang.sample.Sample.SampleRequest
import me.sssaang.sample.Sample.SampleResponse
import me.sssaang.sample.SampleServiceGrpc.SampleServiceImplBase
import me.sssaang.sample.SampleServiceGrpcKt.SampleServiceCoroutineImplBase
import org.lognet.springboot.grpc.GRpcService
import org.springframework.stereotype.Service

//@GRpcService
@Service
class Service: SampleServiceImplBase() {
    fun hi(name: String): String {
        return "hello, ${name}"
    }

    override fun sampleCall(
        request: SampleRequest,
        responseObserver: StreamObserver<SampleResponse>,
    ) {
        val res = SampleResponse
            .newBuilder()
            .setMessage("message received ${request.message}")
            .build()
        responseObserver.onNext(res)
        responseObserver.onCompleted()
    }

    override fun sampleServerStream(
        request: SampleRequest,
        responseObserver: StreamObserver<SampleResponse>
    ) {
        for (i in 1..5) {
            val res: SampleResponse = SampleResponse
                .newBuilder()
                .setMessage("message streaming ${i}: ${request.message}")
                .build()

            responseObserver.onNext(res)
            Thread.sleep(1_000)
        }

        responseObserver.onNext(SampleResponse
            .newBuilder()
            .setMessage("Finished!!!!")
            .build())
        responseObserver.onCompleted()
    }

    override fun sampleClientStream(responseObserver: StreamObserver<SampleResponse>): StreamObserver<SampleRequest> {
        return object : StreamObserver<SampleRequest> {

            override fun onNext(req: SampleRequest) {
                println("message received from client stream ${req.message}")
            }

            override fun onCompleted() {
                responseObserver.onNext(
                    SampleResponse.newBuilder()
                        .setMessage("Finished receiving client message")
                        .build()
                )
                responseObserver.onCompleted()
                println("finished client stream")
            }

            override fun onError(t: Throwable) {
                throw t
            }
        }
    }

//    override fun sampleBidirectionalStream(responseObserver: StreamObserver<SampleResponse>): StreamObserver<SampleRequest> {
//        return object : StreamObserver<SampleRequest> {
//
//            override fun onNext(request: SampleRequest) {
//                println("received message from client: ${request.message}")
//                for (i in 1..5) {
//                    val res: SampleResponse = SampleResponse
//                        .newBuilder()
//                        .setMessage("message streaming ${i}: ${request.message}")
//                        .build()
//
//                    Thread.sleep(1_000)
//                    responseObserver.onNext(res)
//                    println("server streaming: $i")
//                }
//            }
//
//            override fun onCompleted() {
//                responseObserver.onCompleted()
//                println("finished server stream")
//            }
//
//            override fun onError(t: Throwable) {
//                throw t
//            }
//        }
//    }
}


@GRpcService
class CoroutineService: SampleServiceCoroutineImplBase() {
    override fun sampleBidirectionalStream(requests: Flow<SampleRequest>): Flow<SampleResponse> =
        flow {
            // could use transform, but it's currently experimental
            requests.collect { req ->
                println("received message from client: ${req.message}")
                for (i in 1..5) {
                    val res: SampleResponse = SampleResponse
                        .newBuilder()
                        .setMessage("message streaming ${i}: ${req.message}")
                        .build()

                    delay(1_000)
                    println("server streaming: $i")
                    emit(res)
                }
            }
        }
}
