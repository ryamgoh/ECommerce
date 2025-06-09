package com.ryam.ecommerce.repository;

import com.ryam.ecommerce.entity.UserEntity;
import java.util.UUID;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends ListCrudRepository<UserEntity, UUID> {
  UserEntity findByUuid(UUID uuid);
}