package com.team42.NHPS.api.pharmacy.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PharmacyUserMappingRepository extends CrudRepository<PharmacyUserMappingEntity, String> {
}
