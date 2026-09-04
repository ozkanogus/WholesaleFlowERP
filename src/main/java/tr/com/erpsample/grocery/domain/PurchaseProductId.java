package tr.com.erpsample.grocery.domain;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PurchaseProductId implements Serializable {

	@Column(name = "purchase_id")
	private Long purchaseId;

	@Column(name = "product_id")
	private Long productId;

	public PurchaseProductId() {
	}

	public PurchaseProductId(Long purchaseId, Long productId) {
		this.purchaseId = purchaseId;
		this.productId = productId;
	}

	public Long getPurchaseId() {
		return purchaseId;
	}

	public void setPurchaseId(Long purchaseId) {
		this.purchaseId = purchaseId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PurchaseProductId that = (PurchaseProductId) o;
		return Objects.equals(purchaseId, that.purchaseId) && Objects.equals(productId, that.productId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(purchaseId, productId);
	}

	@Override
	public String toString() {
		return "PurchaseProductId{" + "purchaseId=" + purchaseId + ", productId=" + productId + '}';
	}

}
