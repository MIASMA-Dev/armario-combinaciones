package com.armario.config;

import com.sistemabase.capturaorden.application.usecase.CapturarItemUseCase;
import com.sistemabase.capturaorden.application.usecase.GenerarOrdenesUseCase;
import com.sistemabase.capturaorden.application.usecase.ListarItemsUseCase;
import com.sistemabase.capturaorden.domain.service.GeneradorOrdenes;
import com.sistemabase.capturaorden.domain.service.InactividadRegla;
import com.sistemabase.capturaorden.infrastructure.notification.LogNotificadorOrden;
import com.sistemabase.capturaorden.infrastructure.persistence.InMemoryItemRepository;
import com.sistemabase.capturaorden.infrastructure.persistence.InMemoryOrdenRepository;
import com.sistemabase.capturaorden.domain.port.ItemRepository;
import com.sistemabase.capturaorden.domain.port.OrdenRepository;
import com.sistemabase.capturaorden.domain.port.NotificadorOrden;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class LibraryConfig {

    @Bean
    public ItemRepository itemRepository() {
        return new InMemoryItemRepository();
    }

    @Bean
    public OrdenRepository ordenRepository() {
        return new InMemoryOrdenRepository();
    }

    @Bean
    public NotificadorOrden notificadorOrden() {
        return new LogNotificadorOrden();
    }

    @Bean
    public GeneradorOrdenes generadorOrdenes() {
        return new GeneradorOrdenes(List.of(new InactividadRegla(180)));
    }

    @Bean
    public CapturarItemUseCase capturarItemUseCase(ItemRepository itemRepository) {
        return new CapturarItemUseCase(itemRepository);
    }

    @Bean
    public ListarItemsUseCase listarItemsUseCase(ItemRepository itemRepository) {
        return new ListarItemsUseCase(itemRepository);
    }

    @Bean
    public GenerarOrdenesUseCase generarOrdenesUseCase(ItemRepository itemRepository,
                                                     OrdenRepository ordenRepository,
                                                     GeneradorOrdenes generador,
                                                     NotificadorOrden notificador) {
        return new GenerarOrdenesUseCase(itemRepository, ordenRepository, generador, notificador);
    }
}