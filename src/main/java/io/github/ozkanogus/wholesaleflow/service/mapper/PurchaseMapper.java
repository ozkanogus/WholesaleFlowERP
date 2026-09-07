package io.github.ozkanogus.wholesaleflow.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.github.ozkanogus.wholesaleflow.domain.Purchase;
import io.github.ozkanogus.wholesaleflow.domain.PurchaseProduct;
import io.github.ozkanogus.wholesaleflow.service.dto.ProductSalePurchaseDTO;
import io.github.ozkanogus.wholesaleflow.service.dto.PurchaseDTO;

/**
 * Mapper for the entity {@link Purchase} and its DTO {@link PurchaseDTO}.
 */
@Mapper(componentModel = "spring", uses = { GroceryMapper.class })
public interface PurchaseMapper extends EntityMapper<PurchaseDTO, Purchase> {

	@Mapping(target = "products", ignore = true)
	@Mapping(target = "removeProduct", ignore = true)
	Purchase toEntity(PurchaseDTO dto);

	@Mapping(target = "grocery", source = "grocery", qualifiedByName = "id")
	PurchaseDTO toDto(Purchase s);

	@Mapping(target = "productId", source = "product.id")
	ProductSalePurchaseDTO map(PurchaseProduct purchaseProduct);

}
