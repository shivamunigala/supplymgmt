package com.mlt.supplymgmt.user.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mlt.supplymgmt.user.exception.BadUserInputException;
import com.mlt.supplymgmt.user.model.Response;
import com.mlt.supplymgmt.user.model.User;
import com.mlt.supplymgmt.user.util.Status;
import com.mlt.supplymgmt.user.util.UserInputValidator;
import com.mlt.supplymgmt.user.util.UserRole;

@RestController
@RequestMapping("/supplymgmt/users")
public class UserController {

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	private final static String USER_INSERTION = "insert into supply_mgmt.users(id, name, username, password, role) values(:id, :name, :username, :password, :role)";
	
	@PostMapping	
	public ResponseEntity<Response> createUser(@RequestBody User user){
		
		ResponseEntity<Response> response;
		Response resp = new Response();
		resp.setStatus(Status.FAILURE);
		
		try {
			
			UserInputValidator.validateCreateUserInput(user);
			
			String userId = UUID.randomUUID().toString();
			
			Map<String, Object> params = new HashMap<>();
			params.put("id", userId);
			params.put("password", user.getPassword());
			params.put("name", user.getName());
			params.put("username", user.getUsername());
			params.put("role", user.getRole().toString());
			
			jdbcTemplate.update(USER_INSERTION, params);
			
			String userUri = "http://localhost:8080/supplymgmt/users/"+userId;
			
			resp.setStatus(Status.SUCCESS);
			resp.setResource(userUri);
			resp.setMessage("User created successfully");
			
			response = new ResponseEntity<>(resp, HttpStatus.OK);
		} catch(DuplicateKeyException e) {
			e.printStackTrace();
			
			resp.setMessage("Username alreay exists");
			response = new ResponseEntity<Response>(resp, HttpStatus.BAD_REQUEST);
		} catch(BadUserInputException e) {
			e.printStackTrace();
			
			resp.setMessage(e.getMessage());
			response = new ResponseEntity<Response>(resp, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			
			resp.setMessage("Unknown error occurred while creating user");
			response = new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	private final static String USER_DETAILS = "select id, name, username, role from  supply_mgmt.users where id = :id";
	
	@GetMapping("/{id}")
	public ResponseEntity<Response> getUser(@PathVariable String id){
		
		ResponseEntity<Response> response;
		Response resp = new Response();
		resp.setStatus(Status.FAILURE);
		
		try {
			
			UserInputValidator.validateUserId(id);
			
			Map<String, Object> params = new HashMap<>();
			params.put("id", id);
			
			User user = jdbcTemplate.queryForObject(USER_DETAILS, params, new UserMapper());
		
			response = new ResponseEntity<Response>(resp, HttpStatus.OK);
			resp.setStatus(Status.SUCCESS);
			resp.setUser(user);
		}catch(BadUserInputException | EmptyResultDataAccessException  e){
			e.printStackTrace();
			
			resp.setMessage(e.getMessage());
			response = new ResponseEntity<Response>(resp, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			
			resp.setMessage("Unknown Exception while fetching user");
			response = new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	private final static String ALLUSER_DETAILS = "select id, name, username, role from  supply_mgmt.users";
	
	@GetMapping
	public ResponseEntity<Response> getAllUser(){
		
		ResponseEntity<Response> response;
		Response resp = new Response();	
		resp.setStatus(Status.FAILURE);
		try {
			List<User> users = jdbcTemplate.query(ALLUSER_DETAILS, new UserMapper());
		
		
			resp.setStatus(Status.SUCCESS);
			resp.setUsers(users);
			response = new ResponseEntity<>(resp, HttpStatus.OK);
		}catch(Exception e) {
			
			resp.setMessage("Unknown Exception");
			response = new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	private final static String DELETE_DETAILS = "delete from supply_mgmt.users where id= :id ";
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Response> deleteUser(@PathVariable String id){
		
		ResponseEntity<Response> response;
		Response resp = new Response();
		resp.setStatus(Status.FAILURE);
		try {
			
			UserInputValidator.validateUserId(id);
			
			Map<String, Object> params = new HashMap<>();
			params.put("id", id);
		
			jdbcTemplate.update(DELETE_DETAILS, params);
		
			resp.setStatus(Status.SUCCESS);
		
			response = new ResponseEntity<>(resp, HttpStatus.OK);
		}catch(BadUserInputException | EmptyResultDataAccessException  e){
			e.printStackTrace();
			
			resp.setMessage(e.getMessage());
			response = new ResponseEntity<Response>(resp, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			
			resp.setMessage("Unknown Exception while fetching user");
			response = new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
	
	private class UserMapper implements RowMapper<User> {

		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			User user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setRole(UserRole.valueOf(rs.getString("role")));
			user.setUsername(rs.getString("username"));
			
			return user;
		}
		
	}
}

