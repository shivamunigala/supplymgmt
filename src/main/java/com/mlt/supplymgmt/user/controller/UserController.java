package com.mlt.supplymgmt.user.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.mlt.supplymgmt.user.model.Response;
import com.mlt.supplymgmt.user.model.User;
import com.mlt.supplymgmt.user.util.Status;

@RestController
@RequestMapping("/supplymgmt")
public class UserController {

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	private final static String USER_INSERTION = "insert into supply_mgmt.users(id, name, username, password, role) values(:id, :name, :username, :password, :role)";
	
	@PostMapping("/users")	
	public ResponseEntity<Response> createUser(@RequestBody User user){
		
		String userId = UUID.randomUUID().toString();
		
		Map<String, Object> params = new HashMap<>();
		params.put("id", userId);
		params.put("password", user.getPassword());
		params.put("name", user.getName());
		params.put("username", user.getUsername());
		params.put("role", user.getRole());
		
		jdbcTemplate.update(USER_INSERTION, params);
		
		String userUri = "http://localhost:8080/supplymgmt/users/"+userId;
		Response resp = new Response();
		resp.setStatus(Status.SUCCESS);
		resp.setResource(userUri);
		
		ResponseEntity<Response> response = new ResponseEntity<>(resp, HttpStatus.OK);
		return response;
	}
	
	private final static String USER_DETAILS = "select id, name, username, role from  supply_mgmt.users where id = :id";
	
	@GetMapping("/users/{id}")
	public ResponseEntity<Response> getUser(@PathVariable String id){
		
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		
		User user = jdbcTemplate.queryForObject(USER_DETAILS, params, new UserMapper());
		
		Response resp = new Response();
		resp.setStatus(Status.SUCCESS);
		resp.setUser(user);
		
		ResponseEntity<Response> response = new ResponseEntity<>(resp, HttpStatus.OK);
		return response;
	}
	
	private final static String ALLUSER_DETAILS = "select id, name, username, role from  supply_mgmt.users";
	
	@GetMapping("/users")
	public ResponseEntity<Response> getAllUser(){
		
		List<User> users = jdbcTemplate.query(ALLUSER_DETAILS, new UserMapper());
		
		Response resp = new Response();
		resp.setStatus(Status.SUCCESS);
		resp.setUsers(users);
		
		ResponseEntity<Response> response = new ResponseEntity<>(resp, HttpStatus.OK);
		return response;
	}
	
	private final static String DELETE_DETAILS = "delete from supply_mgmt.users where id= :id ";
	
	@DeleteMapping("/users/{id}")
	public ResponseEntity<Response> deleteUser(@PathVariable String id){
		
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		
		jdbcTemplate.update(DELETE_DETAILS, params);
		
		Response resp = new Response();
		resp.setStatus(Status.SUCCESS);
		
		ResponseEntity<Response> response = new ResponseEntity<>(resp, HttpStatus.OK);
		return response;
	}
	
	private class UserMapper implements RowMapper<User> {

		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			User user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setRole(rs.getString("role"));
			user.setUsername(rs.getString("username"));
			
			return user;
		}
		
	}
}

