package com.team42.NHPS.api.pharmacy.utils;

import com.team42.NHPS.api.pharmacy.data.InventoryEntity;
import com.team42.NHPS.api.pharmacy.shared.InventoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UtilsServiceImpl implements UtilsService{
    private final Environment env;

    @Autowired
    public UtilsServiceImpl(Environment env) {
        this.env = env;
    }

    public <T> T webClientGet(String url, String uri, String authorization, ParameterizedTypeReference<T> responseTypeReference) {
        return WebClient.create(url)
                .method(HttpMethod.GET)
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .retrieve()
                .bodyToMono(responseTypeReference)
                .block();
    }

    public <T, G> T webClientPost(String url, String uri, G requestBody, String authorization, ParameterizedTypeReference<T> responseTypeReference, ParameterizedTypeReference<G> requestTypeReference) {
        return WebClient.create(url)
                .method(HttpMethod.POST)
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .body(Mono.just(requestBody), requestTypeReference)
                .retrieve()
                .bodyToMono(responseTypeReference)
                .block();
    }

    @Override
    public InventoryDto mapEntityToDto(InventoryEntity inventoryEntity) {
        InventoryDto inventoryDto = new InventoryDto();
        inventoryDto.setPharmacyId(inventoryEntity.getPharmacyMedicationKey().getPharmacyId());
        inventoryDto.setMedicineId(inventoryEntity.getPharmacyMedicationKey().getMedicationId());
        inventoryDto.setQuantity(inventoryEntity.getQuantity());
        inventoryDto.setVelocityInWeekly(inventoryEntity.getVelocityInWeekly());
        inventoryDto.setVelocityOutWeekly(inventoryEntity.getVelocityOutWeekly());
        return inventoryDto;
    }

    @Override
    public InventoryEntity mapDtoToEntity(InventoryDto inventoryDto) {
        InventoryEntity inventoryEntity = new InventoryEntity();

        InventoryEntity.PharmacyMedicationKey pharmacyMedicationKey = new InventoryEntity.PharmacyMedicationKey();
        pharmacyMedicationKey.setMedicationId(inventoryDto.getMedicineId());
        pharmacyMedicationKey.setPharmacyId(inventoryDto.getPharmacyId());
        inventoryEntity.setPharmacyMedicationKey(pharmacyMedicationKey);
        inventoryEntity.setQuantity(inventoryDto.getQuantity());
        inventoryEntity.setVelocityInWeekly(inventoryDto.getVelocityInWeekly());
        inventoryEntity.setVelocityOutWeekly(inventoryDto.getVelocityOutWeekly());
        return inventoryEntity;
    }
}
