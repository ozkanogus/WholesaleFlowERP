package io.github.ozkanogus.wholesaleflow.service.mapper;

import org.mapstruct.Mapper;

import io.github.ozkanogus.wholesaleflow.domain.StockMovement;
import io.github.ozkanogus.wholesaleflow.service.dto.StockMovementDTO;

/**
 * Mapper for the entity {@link StockMovement} and its DTO
 * {@link StockMovementDTO}.
 */
@Mapper(componentModel = "spring")
public interface StockMovementMapper extends EntityMapper<StockMovementDTO, StockMovement> {
}
