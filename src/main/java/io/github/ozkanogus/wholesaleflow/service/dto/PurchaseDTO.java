package io.github.ozkanogus.wholesaleflow.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;


public class PurchaseDTO implements Serializable {

    private Long id;

    private GroceryDTO grocery;

    private Instant createdDate = Instant.now();

    private Instant lastModifiedDate = Instant.now();

	private Set<ProductSalePurchaseDTO> products;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GroceryDTO getGrocery() {
        return grocery;
    }

    public void setGrocery(GroceryDTO grocery) {
        this.grocery = grocery;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

	public Set<ProductSalePurchaseDTO> getProducts() {
        return products;
    }

	public void setProducts(Set<ProductSalePurchaseDTO> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PurchaseDTO)) {
            return false;
        }

        PurchaseDTO purchaseDTO = (PurchaseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, purchaseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
		return "PurchaseDTO{"
				+
            "id=" + id +
            ", grocery=" + grocery +
            ", createdDate=" + createdDate +
            ", lastModifiedDate=" + lastModifiedDate +
            '}';
    }
}
