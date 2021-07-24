package com.international.codyweb.core.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.international.codyweb.core.user.model.Post;



@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
	
	@Query(value = "SELECT u FROM Posts u WHERE u.category = ?1", nativeQuery = true)
	List <Post> findByCategory(String category);
}