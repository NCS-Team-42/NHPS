package com.team42.NHPS.api.pharmacy.utils;

import com.team42.NHPS.api.pharmacy.data.InventoryEntity;
import com.team42.NHPS.api.pharmacy.shared.InventoryDto;
import org.springframework.core.ParameterizedTypeReference;

public interface UtilsService {
    <T> T webClientGet(String url, String uri, String authorization, ParameterizedTypeReference<T> responseTypeReference);
    <T, G> T webClientPost(String url, String uri, G requestBody, String authorization, ParameterizedTypeReference<T> responseTypeReference, ParameterizedTypeReference<G> requestTypeReference);
    InventoryDto mapEntityToDto(InventoryEntity inventoryEntity);
    InventoryEntity mapDtoToEntity(InventoryDto inventoryDto);
}
