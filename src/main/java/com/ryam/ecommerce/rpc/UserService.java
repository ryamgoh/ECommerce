package com.ryam.ecommerce.rpc;

import com.ryam.ecommerce.entity.UserEntity;
import com.ryam.ecommerce.internal.proto.UserProto;
import com.ryam.ecommerce.internal.proto.UserProto.User;
import com.ryam.ecommerce.internal.proto.UserProto.GetUserRequest;
import com.ryam.ecommerce.internal.proto.UserProto.GetUserResponse;
import com.ryam.ecommerce.internal.proto.UserProto.CreateUserRequest;
import com.ryam.ecommerce.internal.proto.UserProto.CreateUserResponse;
import com.ryam.ecommerce.internal.proto.UserServiceGrpc;
import com.ryam.ecommerce.repository.UserRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@GrpcService
public class UserService extends UserServiceGrpc.UserServiceImplBase {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {
        try {
            // Convert gRPC request to User entity
            User userRequest = request.getUser();
            UserEntity userEntity = UserEntity.fromProto(userRequest);

            // Save to database
            UserEntity savedUserEntity = userRepository.save(userEntity);

            // Convert saved entity back to gRPC response
            User responseUser = savedUserEntity.toProto();

            // Send response
            responseObserver.onNext(CreateUserResponse.newBuilder()
                    .setUser(responseUser)
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error creating user: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void getUser(GetUserRequest request, StreamObserver<GetUserResponse> responseObserver) {
        try {
            String uuid = request.getUuid();
            UserEntity userEntity = userRepository.findByUuid(UUID.fromString(uuid));
            User user = userEntity.toProto();
            responseObserver.onNext(GetUserResponse.newBuilder()
                    .setUser(user)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error getting user: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void updateUser(UserProto.UpdateUserRequest request, StreamObserver<UserProto.UpdateUserResponse> responseObserver) {
        try {
            var uuid = request.getUuid();
            if (uuid.isEmpty()) {
                throw new IllegalArgumentException("User uuid is required");
            }
            var existingUser = userRepository.findByUuid(UUID.fromString(uuid));
            // if userRepository has uuid already, then all good. if not exist, throw
            if (existingUser == null) {
                throw new IllegalArgumentException(String.format("User with uuid %s not found", uuid));
            }

            // Use builder pattern to update fields
            var userBuilder = existingUser.toBuilder();
            if (request.hasFirstName()) {
                userBuilder.firstName(request.getFirstName());
            }
            if (request.hasLastName()) {
                userBuilder.lastName(request.getLastName());
            }
            if (request.hasAge()) {
                userBuilder.age(request.getAge());
            }
            if (request.hasMiddleName()) {
                userBuilder.middleName(request.getMiddleName());
            }
            if (request.hasPhone()) {
                userBuilder.phone(request.getPhone());
            }
            if (request.hasEmail()) {
                userBuilder.email(request.getEmail());
            }

            UserEntity savedUserEntity = userRepository.save(userBuilder.build());

            User updatedUser = savedUserEntity.toProto();
            responseObserver.onNext(UserProto.UpdateUserResponse.newBuilder()
                    .setUser(updatedUser)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error updating user: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void deleteUser(UserProto.DeleteUserRequest request, StreamObserver<UserProto.SuccessResponse> responseObserver) {
        try {
            String uuid = request.getUuid();
            UserEntity userEntity = userRepository.findByUuid(UUID.fromString(uuid));
            if (userEntity == null) {
                throw new IllegalArgumentException(String.format("User with uuid %s not found", uuid));
            }
            userRepository.delete(userEntity);
            responseObserver.onNext(UserProto.SuccessResponse.newBuilder()
                    .setSuccess(true)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error deleting user: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void getAllUsers(UserProto.GetAllUsersRequest request, StreamObserver<UserProto.GetAllUsersResponse> responseObserver) {
        try {
            List<UserEntity> userEntities = userRepository.findAll();
            UserProto.GetAllUsersResponse response = UserProto.GetAllUsersResponse.newBuilder()
                    .addAllUsers(userEntities.stream().map(UserEntity::toProto).toList())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error getting all users: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }
}
