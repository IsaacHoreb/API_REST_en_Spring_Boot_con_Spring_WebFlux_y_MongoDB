package com.api.rest.springboot.webflux.repository;

import com.api.rest.springboot.webflux.document.Cliente;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

//Segundo
public interface ClienteRepository extends ReactiveMongoRepository<Cliente, String> {

}
