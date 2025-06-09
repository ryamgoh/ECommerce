package com.ryam.ecommerce.entity;

import com.google.protobuf.Timestamp;
import com.ryam.ecommerce.internal.proto.UserProto.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(columnDefinition = "uuid", updatable = false)
  UUID uuid;

  @Column(name = "first_name", nullable = false)
  String firstName;

  @Column
  String middleName;

  @Column(name = "last_name", nullable = false)
  String lastName;

  @Column(name = "age", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
  Integer age;

  @Column(name = "phone", unique = true)
  String phone;

  @Column(name = "email", unique = true)
  String email;

  @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
  LocalDateTime updatedAt;

  /**
   * fromProto method that converts a protobuf {@link User} object to a {@link UserEntity} object.
   *
   * @param protoUser The protobuf {@link User} object
   * @return The {@link UserEntity} object
   */
  public static UserEntity fromProto(User protoUser) {
    UserEntity.UserEntityBuilder user = UserEntity.builder();
    user.firstName(protoUser.getFirstName());
    user.lastName(protoUser.getLastName());
    user.age(protoUser.getAge());
    // Only set UUID if it's provided and not empty
    if (!protoUser.getUuid().isEmpty()) {
      user.uuid(UUID.fromString(protoUser.getUuid()));
    }
    // Handle timestamps
    if (protoUser.hasCreatedAt()) {
      Instant createdInstant = Instant.ofEpochSecond(
          protoUser.getCreatedAt().getSeconds(), protoUser.getCreatedAt().getNanos());
      user.createdAt(LocalDateTime.ofInstant(createdInstant, ZoneOffset.UTC));
    }
    if (protoUser.hasUpdatedAt()) {
      Instant updatedInstant = Instant.ofEpochSecond(
          protoUser.getUpdatedAt().getSeconds(), protoUser.getUpdatedAt().getNanos());
      user.updatedAt(LocalDateTime.ofInstant(updatedInstant, ZoneOffset.UTC));
    }
    if (protoUser.hasMiddleName()) {
      user.middleName(protoUser.getMiddleName());
    }
    if (protoUser.hasPhone()) {
      user.phone(protoUser.getPhone());
    }
    if (protoUser.hasEmail()) {
      user.email(protoUser.getEmail());
    }
    return user.build();
  }

  /**
   * toProto method that converts a {@link UserEntity} object to a protobuf {@link User} object.
   *
   * @return The protobuf {@link User} object
   */
  public User toProto() {
    var builder = User.newBuilder().setUuid(this.uuid.toString()).setFirstName(this.firstName).setLastName(this.lastName);

    if (this.createdAt != null) {
      builder.setCreatedAt(Timestamp.newBuilder().setSeconds(this.createdAt.toEpochSecond(ZoneOffset.UTC)).setNanos(this.createdAt.getNano()).build());
    }
    if (this.updatedAt != null) {
      builder.setUpdatedAt(Timestamp.newBuilder().setSeconds(this.updatedAt.toEpochSecond(ZoneOffset.UTC)).setNanos(this.updatedAt.getNano()).build());
    }

    if (this.age != null) {
      builder.setAge(this.age);
    }
    if (this.middleName != null) {
      builder.setMiddleName(this.middleName);
    }
    if (this.phone != null) {
      builder.setPhone(this.phone);
    }
    if (this.email != null) {
      builder.setEmail(this.email);
    }

    return builder.build();
  }
}
