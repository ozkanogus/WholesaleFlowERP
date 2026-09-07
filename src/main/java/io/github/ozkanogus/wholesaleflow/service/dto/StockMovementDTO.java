package io.github.ozkanogus.wholesaleflow.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import jakarta.validation.constraints.NotNull;

import io.github.ozkanogus.wholesaleflow.domain.Grocery;
import io.github.ozkanogus.wholesaleflow.domain.Product;
import io.github.ozkanogus.wholesaleflow.domain.enumeration.OperationType;

public class StockMovementDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;

	@NotNull
	private BigDecimal count;

	@NotNull
	private Long operationId;

	@NotNull
	private OperationType operationType;

	@NotNull
	private Grocery grocery;

	@NotNull
	private Product product;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getCount() {
		return count;
	}

	public void setCount(BigDecimal count) {
		this.count = count;
	}

	public Long getOperationId() {
		return operationId;
	}

	public void setOperationId(Long operationId) {
		this.operationId = operationId;
	}

	public OperationType getOperationType() {
		return operationType;
	}

	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
	}

	public Grocery getGrocery() {
		return grocery;
	}

	public void setGrocery(Grocery grocery) {
		this.grocery = grocery;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof StockMovementDTO)) {
			return false;
		}

		StockMovementDTO stockMovementDTO = (StockMovementDTO) o;
		if (this.id == null) {
			return false;
		}
		return Objects.equals(this.id, stockMovementDTO.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

	@Override
	public String toString() {
		return "StockMovementDTO{" + "id=" + getId() + ", count=" + getCount() + ", operationType='"
				+ getOperationType() + "'" + "}";
	}
}
