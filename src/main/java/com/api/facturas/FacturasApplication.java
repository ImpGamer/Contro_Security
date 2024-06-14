package com.api.facturas;

import com.api.facturas.dto.ProductoDTO;
import com.api.facturas.mappers.ProductoMapper;
import com.api.facturas.pojo.Categoria;
import com.api.facturas.repository.CategoriaRepository;
import com.api.facturas.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.parameters.P;

import java.util.Optional;

@SpringBootApplication
public class FacturasApplication {

	public static void main(String[] args) {
		SpringApplication.run(FacturasApplication.class, args);
	}

}
