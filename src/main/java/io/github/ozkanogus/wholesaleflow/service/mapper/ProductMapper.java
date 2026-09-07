package io.github.ozkanogus.wholesaleflow.service.mapper;

import org.mapstruct.Mapper;

import io.github.ozkanogus.wholesaleflow.domain.Product;
import io.github.ozkanogus.wholesaleflow.service.dto.ProductDTO;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
}
