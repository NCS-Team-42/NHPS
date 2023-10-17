package com.team42.NHPS.api.pharmacy.ui.controllers;

import com.team42.NHPS.api.pharmacy.service.PharmacyService;
import com.team42.NHPS.api.pharmacy.shared.PharmacyDto;
import com.team42.NHPS.api.pharmacy.shared.PharmacyUserMappingDto;
import com.team42.NHPS.api.pharmacy.ui.model.CreatePharmacyRequestModel;
import com.team42.NHPS.api.pharmacy.ui.model.PharmacyResponseModel;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pharmacies")
public class PharmacyController {

    @Value("${token.secret}")
    private String token;
    @Value("${server.port}")
    private String port;
    private PharmacyService pharmacyService;
    private Environment environment;
    private ModelMapper modelMapper;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public PharmacyController(PharmacyService pharmacyService, Environment environment, ModelMapper modelMapper) {
        this.pharmacyService = pharmacyService;
        this.environment = environment;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<PharmacyResponseModel>> getAllPharmacy() {
        List<PharmacyDto> pharmacyDtoList = pharmacyService.getAllPharmacy();
        List<PharmacyResponseModel> foundPharmacies = new ArrayList<>();
        pharmacyDtoList.forEach(pharmacyDto -> foundPharmacies.add(modelMapper.map(pharmacyDto, PharmacyResponseModel.class)));
        return ResponseEntity.status(HttpStatus.OK).body(foundPharmacies);
    }

    @GetMapping("/{pharmacyId}")
    public ResponseEntity<PharmacyResponseModel> getPharmacy(@PathVariable String pharmacyId) {
        PharmacyDto pharmacyDto = pharmacyService.getPharmacy(pharmacyId);
        PharmacyResponseModel foundPharmacy = modelMapper.map(pharmacyDto, PharmacyResponseModel.class);
        return ResponseEntity.status(HttpStatus.OK).body(foundPharmacy);
    }

    @PostMapping
    public ResponseEntity<PharmacyResponseModel> createPharmacy(@Valid @RequestBody CreatePharmacyRequestModel pharmacyRequestModel) {

        PharmacyDto createdPharmacyDto = pharmacyService.createPharmacy(modelMapper.map(pharmacyRequestModel, PharmacyDto.class));

        PharmacyResponseModel foundPharmacy = modelMapper.map(createdPharmacyDto, PharmacyResponseModel.class);
        return ResponseEntity.status(HttpStatus.OK).body(foundPharmacy);
    }

    @PostMapping("/{pharmacyId}/user/{userId}")
    public ResponseEntity<Map<String, String>> mapUserToPharmacy(@PathVariable String pharmacyId, @PathVariable String userId, @RequestHeader("Authorization") String authorization) {
        Map<String, String> map = pharmacyService.mapPharmacyToUser(new PharmacyUserMappingDto(pharmacyId, userId), authorization);
        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    @GetMapping("/status/check")
    public String status(@RequestHeader("Authorization") String authorizationHeader) {
        String returnValue = "Working on port " + port + " with token " + token + ".\nToken from environment "
                + environment.getProperty("token.secret") + "\nAuthorizationHeader = " + authorizationHeader
                + ".\nMy application environment = " + environment.getProperty("myapplication.environment");
        log.info(returnValue);
        return returnValue;
    }

    @GetMapping("/ip")
    public String getIp() {
        String returnValue;

        try {
            InetAddress ipAddr = InetAddress.getLocalHost();
            returnValue = ipAddr.getHostAddress();
        } catch (UnknownHostException ex) {
            returnValue = ex.getLocalizedMessage();
        }

        return returnValue;
    }

    @GetMapping("/api/common/hello")
    public String hello(@RequestHeader("Authorization") String authnHeader) {
        String uri = "http://localhost:8083/medication/api/common/hello";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authnHeader);
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        return ("Hello from Pharmacies. " + restTemplate.exchange(uri, HttpMethod.GET, entity, String.class).getBody());
    }
}
