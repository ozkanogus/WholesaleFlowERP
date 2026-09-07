package io.github.ozkanogus.wholesaleflow.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import io.github.ozkanogus.wholesaleflow.domain.enumeration.OperationType;

@Entity
@Table(name = "stock_movement")
public class StockMovement extends AbstractAuditingEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
	@SequenceGenerator(name = "sequenceGenerator")
	@Column(name = "id")
	private Long id;

	@NotNull
	@Column(name = "count", precision = 21, scale = 2, nullable = false)
	private BigDecimal count;

	@NotNull
	@Column(name = "operationId")
	private Long operationId;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "operationType", nullable = false)
	private OperationType operationType;

	@ManyToOne(optional = false)
	@NotNull
	private Grocery grocery;

	@ManyToOne(optional = false)
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
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof StockMovement)) {
			return false;
		}
		return id != null && id.equals(((StockMovement) o).id);
	}

	@Override
	public String toString() {
		return "StockMovement{" + "id=" + getId() + ", count=" + getCount() + ", operationType='" + getOperationType()
				+ "'" + "}";
	}

}
