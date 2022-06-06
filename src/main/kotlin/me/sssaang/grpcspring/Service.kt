package me.sssaang.grpcspring

import org.lognet.springboot.grpc.GRpcService
import io.grpc.stub.StreamObserver
import me.sssaang.sample.Sample.SampleRequest
import me.sssaang.sample.Sample.SampleResponse
import me.sssaang.sample.SampleServiceGrpc.SampleServiceImplBase

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

}