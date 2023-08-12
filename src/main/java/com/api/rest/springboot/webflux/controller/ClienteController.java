//Quinto -- *Pero ya debimos haber ingresado los datos en el archivos <<properties>>
package com.api.rest.springboot.webflux.controller;

import com.api.rest.springboot.webflux.document.Cliente;
import com.api.rest.springboot.webflux.services.ClientesServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClientesServices clientesServices;

    //Esto es para guardar las fotos por medio
    //de la ruta que ingresamos en el archivo <<properties>>
    @Value("${config.uploads.path}")
    private String path;

    //Registrar cliente con foto
    @PostMapping("/registrarClienteConFotos")
    public Mono<ResponseEntity<Cliente>> registrarClienteConFoto(Cliente cliente, @RequestPart FilePart file) {

        cliente.setFoto(UUID.randomUUID().
                toString() + " - " + file.filename()
                .replace(" ", "")
                .replace(":", "")
                .replace("//", ""));

        return file.transferTo(new File(path + cliente.getFoto())).then(clientesServices.Save(cliente))
                .map(c -> ResponseEntity.created(URI.create("/api/clientes".concat(c.getId())))
                        .contentType(MediaType.APPLICATION_JSON_UTF8).body(c));
    }

    //Metodo para subir fotos
    @PostMapping("/upload/{id}")
    public Mono<ResponseEntity<Cliente>> subirFotos(@PathVariable String id, @RequestPart FilePart file) {
        return clientesServices.findById(id).flatMap(c -> {
            c.setFoto(UUID.randomUUID().toString() + "-" + file.filename()
                    .replace(" ", "")
                    .replace(":", "")
                    .replace("//", ""));

            return file.transferTo(new File(path + c.getFoto())).then(clientesServices.Save(c));
        }).map(c -> ResponseEntity.ok(c)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Cliente>>> listarClientes() {
        return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(clientesServices.findAll()));
    }


    //Para obtener/ver detalles del Cliente
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Cliente>> verDetallesDelCliente(@PathVariable String id) {
        return clientesServices.findById(id).map(c -> ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8).body(c)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    //Guardar detalles del cliente
    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> guardarCliente(@Valid @RequestBody Mono<Cliente> monoCliente) {
        Map<String, Object> respuesta = new HashMap<>();

        return monoCliente.flatMap(cliente -> {
            return clientesServices.Save(cliente).map(c -> {
                respuesta.put("clientes", c);
                respuesta.put("mensaje", "Cliente guardardo con éxito");
                respuesta.put("timestamp", new Date());
                return ResponseEntity.created(URI.create("/api/clientes/".concat(c.getId())))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .body(respuesta);
            });
        }).onErrorResume(t -> {
            if (t instanceof WebExchangeBindException) {
                WebExchangeBindException bindException = (WebExchangeBindException) t;

                List<String> errores = bindException.getFieldErrors()
                        .stream()
                        .map(fieldError -> "El campo : " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                        .collect(Collectors.toList());

                respuesta.put("errores", errores);
                respuesta.put("timestamp", new Date());
                respuesta.put("status", HttpStatus.BAD_REQUEST.value());

                return Mono.just(ResponseEntity.badRequest().body(respuesta));
            } else {
                // Manejar otros tipos de errores aquí si es necesario
                return Mono.error(t);
            }
        });
    }

    //Edito/Modifica un Cliente
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Cliente>> editarClientes(@RequestBody Cliente cliente, @PathVariable String id) {

        return clientesServices.findById(id).flatMap(c -> {
                    c.setNombre(cliente.getNombre());
                    c.setApellidos(cliente.getApellidos());
                    c.setEdad(cliente.getEdad());
                    c.setSueldo(cliente.getSueldo());
                    return clientesServices.Save(c);
                }).map(c -> ResponseEntity.created(URI.create("/api/clientes/".concat(c.getId())))
                        .contentType(MediaType.APPLICATION_JSON_UTF8).body(c))
                .defaultIfEmpty(ResponseEntity.notFound().build());

    }


}

