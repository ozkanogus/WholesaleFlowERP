package io.github.ozkanogus.wholesaleflow.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.github.ozkanogus.wholesaleflow.domain.Sale;
import io.github.ozkanogus.wholesaleflow.domain.SaleProduct;
import io.github.ozkanogus.wholesaleflow.service.dto.ProductSalePurchaseDTO;
import io.github.ozkanogus.wholesaleflow.service.dto.SaleDTO;

/**
 * Mapper for the entity {@link Sale} and its DTO {@link SaleDTO}.
 */
@Mapper(componentModel = "spring", uses = { GroceryMapper.class })
public interface SaleMapper extends EntityMapper<SaleDTO, Sale> {

	@Mapping(target = "products", ignore = true)
	@Mapping(target = "removeProduct", ignore = true)
	Sale toEntity(SaleDTO dto);

	@Mapping(target = "grocery", source = "grocery", qualifiedByName = "id")
	SaleDTO toDto(Sale s);

	@Mapping(target = "productId", source = "product.id")
	ProductSalePurchaseDTO map(SaleProduct saleProduct);

}
