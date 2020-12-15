package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.repository.MongoRepository;

import datamodel.Indicador;

public interface IndicadorRepositorio extends MongoRepository<Indicador, String> {

	  public Indicador findByName(String name);
	  public List<Indicador> findByFuente(String fuente);

	}
