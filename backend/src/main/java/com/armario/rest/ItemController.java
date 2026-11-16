package com.armario.rest;

import com.armario.config.LibraryConfig;
import com.sistemabase.capturaorden.application.usecase.CapturarItemUseCase;
import com.sistemabase.capturaorden.application.usecase.GenerarOrdenesUseCase;
import com.sistemabase.capturaorden.application.usecase.ListarItemsUseCase;
import com.sistemabase.capturaorden.domain.enums.CategoriaItem;
import com.sistemabase.capturaorden.domain.enums.TipoObjeto;
import com.sistemabase.capturaorden.domain.enums.TipoUbicacion;
import com.sistemabase.capturaorden.domain.enums.UnidadMedida;
import com.sistemabase.capturaorden.domain.model.Item;
import com.sistemabase.capturaorden.domain.model.Orden;
import com.sistemabase.capturaorden.domain.model.Ubicacion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class ItemController {

    private final CapturarItemUseCase capturar;
    private final ListarItemsUseCase listar;
    private final GenerarOrdenesUseCase generar;

    public ItemController(CapturarItemUseCase capturar, ListarItemsUseCase listar, GenerarOrdenesUseCase generar) {
        this.capturar = capturar;
        this.listar = listar;
        this.generar = generar;
    }

    @GetMapping("/items")
    public List<Item> items() {
        return listar.todos();
    }

    @PostMapping("/items")
    public Item capturar(@RequestBody CrearItemRequest req) {
        Ubicacion ubicacion = new Ubicacion(null, req.ubicacionNombre(), TipoUbicacion.ARMARIO);
        Item item = new Item(null, req.nombre(), CategoriaItem.ROPA, TipoObjeto.ROPA,
                req.cantidad() == null ? BigDecimal.ONE : req.cantidad(),
                UnidadMedida.UNIDAD, ubicacion);
        return capturar.ejecutar(item);
    }

    @GetMapping("/items/ordenes")
    public List<Orden> generarOrdenes() {
        return generar.ejecutar();
    }

    public record CrearItemRequest(String nombre, BigDecimal cantidad, String ubicacionNombre) {}
}