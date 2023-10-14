/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team42.NHPS.api.users.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.team42.NHPS.api.users.ui.model.PatientsResponseModel;

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

		usersRepository.save(userEntity);

		return modelMapper.map(userEntity, UserDto.class);
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

		UserEntity userEntity = usersRepository.findByUserId(userId);

		if (userEntity == null)
			throw new UsersServiceException("User not found");

		usersRepository.delete(userEntity);

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
//				"Patients web service endpoint called and recieved " + albumsListResponse.getBody().size() + " items");
//
//		return albumsListResponse.getBody();
//	}

}
