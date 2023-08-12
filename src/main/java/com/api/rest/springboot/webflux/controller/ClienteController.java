//Quinto -- *Pero ya debimos haber ingresado los datos en el archivos <<properties>>
package com.api.rest.springboot.webflux.controller;

import com.api.rest.springboot.webflux.document.Cliente;
import com.api.rest.springboot.webflux.services.ClientesServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URI;
import java.util.UUID;

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

    

}
