package com.ryam.ecommerce.rpc;

import com.ryam.ecommerce.internal.proto.Hello.HelloRequest;
import com.ryam.ecommerce.internal.proto.Hello.HelloResponse;
import com.ryam.ecommerce.internal.proto.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class HelloService extends HelloServiceGrpc.HelloServiceImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        String greeting = "Hello, " + request.getName() + "!";

        HelloResponse response = HelloResponse.newBuilder().
                setMessage(greeting).
                build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
