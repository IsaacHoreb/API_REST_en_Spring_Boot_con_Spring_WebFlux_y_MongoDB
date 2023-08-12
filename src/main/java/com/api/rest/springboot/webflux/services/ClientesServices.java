//Tercero
package com.api.rest.springboot.webflux.services;

import com.api.rest.springboot.webflux.document.Cliente;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientesServices {

    //Flux and Mono, son Observables
    //Flux, es un fujo, ya que me en entregara muchos elementos
    //Mono, es un flujo pero de uno en uno sera entregado

    public Flux<Cliente> findAll(); //Para mostrar todos los cliente

    public Mono<Cliente> findById(String id); //Mostrar cliente por uno

    public Mono<Cliente> Save(Cliente cliente); //Guardar clientes

    public Mono<Void> delete(Cliente cliente); //Eliminar
}
