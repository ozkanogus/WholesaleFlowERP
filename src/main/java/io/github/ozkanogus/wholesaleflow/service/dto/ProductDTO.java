package io.github.ozkanogus.wholesaleflow.service.dto;

import java.io.Serializable;
import java.util.Objects;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import io.github.ozkanogus.wholesaleflow.domain.enumeration.Unit;

/**
 * A DTO for the {@link io.github.ozkanogus.wholesaleflow.domain.Product} entity.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO implements Serializable {

	private Long id;

	@NotNull
	private String name;

	@NotNull
	private Unit unit;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ProductDTO)) {
			return false;
		}

		ProductDTO productDTO = (ProductDTO) o;
		if (this.id == null) {
			return false;
		}
		return Objects.equals(this.id, productDTO.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

	// prettier-ignore
	@Override
	public String toString() {
		return "ProductDTO{" + "id=" + getId() + ", name='" + getName() + "'" + ", unit='" + getUnit() + "'" + "}";
	}
}
