package me.sssaang.grpcspring

import io.grpc.stub.StreamObserver
import me.sssaang.sample.Sample.SampleRequest
import me.sssaang.sample.Sample.SampleResponse
import me.sssaang.sample.SampleServiceGrpc.SampleServiceImplBase
import org.lognet.springboot.grpc.GRpcService

@GRpcService
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
                .setMessage("message streaming ${i}: ${request!!.message}")
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

}