/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team42.NHPS.api.users.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.team42.NHPS.api.users.exception.ResourceNotFoundException;
import com.team42.NHPS.api.users.ui.model.PharmacyResponseModel;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.team42.NHPS.api.users.data.UserEntity;
import com.team42.NHPS.api.users.data.UsersRepository;
import com.team42.NHPS.api.users.shared.JwtUtil;
import com.team42.NHPS.api.users.shared.UserDto;
import com.team42.NHPS.api.users.shared.UsersServiceException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UsersServiceImpl implements UsersService {

    BCryptPasswordEncoder bCryptPasswordEncoder;
    UsersRepository usersRepository;
    JwtUtil jwtUtil;
    RestTemplate restTemplate;
    Environment environment;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UsersServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder, UsersRepository usersRepository,
                            JwtUtil jwtUtil, RestTemplate restTemplate, Environment environment) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.usersRepository = usersRepository;
        this.jwtUtil = jwtUtil;
        this.restTemplate = restTemplate;
        this.environment = environment;
    }

    @Override
    public UserDto createUser(UserDto userDto) {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);

        userEntity.setUserId(UUID.randomUUID().toString());
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));

        PharmacyResponseModel pharmacyResponseModel = getPharmacy(userDto.getPharmacyId());
        usersRepository.save(userEntity);

        UserDto foundUserDto = modelMapper.map(userEntity, UserDto.class);
        foundUserDto.setPharmacyName(pharmacyResponseModel.getPharmacyName());
        return foundUserDto;
    }


    @Override
    public UserDto getUserByEmail(String email) {
        UserEntity userEntity = usersRepository.findByEmail(email);

        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }

        return new ModelMapper().map(userEntity, UserDto.class);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = usersRepository.findByEmail(email);

        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true, // Email verification status
                true, true, true, new ArrayList<>());
    }

    @Override
    public void deleteUser(String userId, String authorizationHeader) {

        String userIdFromHeader = jwtUtil.getUserId(authorizationHeader);

        if (!userId.equalsIgnoreCase(userIdFromHeader)) {
            throw new UsersServiceException("Operation not allowed");
        }

        UserEntity userEntity = usersRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (userEntity == null)
            throw new UsersServiceException("User not found");

        usersRepository.delete(userEntity);

    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = usersRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return new ModelMapper().map(userEntity, UserDto.class);
    }


    @Override
    public List<UserDto> getUsers() {
        List<UserEntity> userEntities = (List<UserEntity>) usersRepository.findAll();

        if (userEntities == null || userEntities.isEmpty())
            return new ArrayList<>();

        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();

        List<UserDto> returnValue = new ModelMapper().map(userEntities, listType);

        return returnValue;
    }

    private PharmacyResponseModel getPharmacy(String pharmacyId) {
        WebClient client = WebClient.create(environment.getProperty("pharmacy.url"));
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = client.method(HttpMethod.GET);
        WebClient.RequestBodySpec bodySpec = uriSpec.uri("/" + pharmacyId);
        WebClient.ResponseSpec responseSpec = bodySpec.retrieve();
        Mono<PharmacyResponseModel> responseBody = responseSpec.bodyToMono(PharmacyResponseModel.class);
        return responseBody.block();
    }

//	@Override
//	public List<PatientsResponseModel> getUserAlbums(String jwt) {
//
//		String patientsUrl = environment.getProperty("patients.url");
//        logger.info("patientsUrl = " + patientsUrl);
//
//		HttpHeaders httpHeaders = new HttpHeaders();
//		httpHeaders.add("Authorization", "Bearer " + jwt);
//		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//
//		ResponseEntity<List<PatientsResponseModel>> albumsListResponse = restTemplate.exchange(patientsUrl, HttpMethod.GET,
//				new HttpEntity<>(httpHeaders), new ParameterizedTypeReference<>() {
//				});
//
//		logger.info(
//				"Patients web service endpoint called and received " + albumsListResponse.getBody().size() + " items");
//
//		return albumsListResponse.getBody();
//	}

}
