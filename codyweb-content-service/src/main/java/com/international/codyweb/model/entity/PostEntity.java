package com.international.codyweb.model.entity;


import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.international.codyweb.model.AuditModel;
import lombok.*;

@Entity
@Table(name = "posts")
@Getter @Setter @NoArgsConstructor
public class PostEntity extends AuditModel{

	
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue(strategy = GenerationType.SEQUENCE) 
	private Long id;

	@NotNull
	@Size(max = 100)
	@Column(unique = true)
	private String title;

	@NotNull
	@Size(max = 250)
	private String category;


	@NotNull
	@Lob
	private String content;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;
	
	@JsonBackReference
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private List<PostMedia> media;

}