package com.international.codyweb.core.security.services;


import java.util.*;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.international.codyweb.core.user.model.Role;
import com.international.codyweb.core.user.model.User;
import com.international.codyweb.core.user.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	@Autowired
	UserRepository userRepository;
	
	//Build user authentication object
	@Transactional
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
		
		// we can use this in case we want to activate account after customer verified the account
//		boolean enabled = !user.isAccountVerified();		
		 UserDetails userDetail = UserDetailsImpl.withUsername(user.getEmail())
	                .password(user.getPassword())
	                .disabled(user.isLoginDisabled())
	                .authorities(getAuthorities(user)).build();

	        return userDetail;
		
//		return UserDetailsImpl.build(user);
	}
	
	 private Collection <GrantedAuthority> getAuthorities(User user){
	        Set <Role> userRoles = user.getRoles();
	        Collection<GrantedAuthority> authorities = new ArrayList<>(userRoles.size());
	        for(Role userRole : userRoles){
	            authorities.add(new SimpleGrantedAuthority( (String) userRole.getName().name()));
	        }

	        return authorities;
	    }
	
	

}