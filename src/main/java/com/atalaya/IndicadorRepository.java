package com.atalaya;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.modelodatos.Indicador;

@Repository
public interface IndicadorRepository extends MongoRepository<Indicador, String> {

	  public Indicador findByNombre(String nombre);
	  public List<Indicador> findByFuente(String fuente);

	}
