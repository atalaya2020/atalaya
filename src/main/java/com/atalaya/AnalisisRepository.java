package com.atalaya;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.modelodatos.Analisis;

@Repository
public interface AnalisisRepository extends MongoRepository<Analisis, String> {

	  public Analisis findByNombre(String nombre);
	  public List<Analisis> findByDepartamento(String fuente);

	}
