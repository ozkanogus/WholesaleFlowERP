package tr.com.erpsample.grocery.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table(name = "purchase_product")
public class PurchaseProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
	private PurchaseProductId id;

    @NotNull
    @Column(name = "count", precision = 21, scale = 2, nullable = false)
    private BigDecimal count;

    @NotNull
    @Column(name = "price", precision = 21, scale = 2, nullable = false)
    private BigDecimal price;

    @ManyToOne(optional = false)
	@MapsId("purchaseId")
    @NotNull
    @JsonIgnoreProperties(value = { "products", "grocery" }, allowSetters = true)
	private Purchase purchase;

    @ManyToOne(optional = false)
    @MapsId("productId")
    @NotNull
    private Product product;

    public PurchaseProduct() {
		this.id = new PurchaseProductId();
    }

	public PurchaseProduct(BigDecimal count, BigDecimal price, Purchase purchase, Product product) {
        this.count = count;
        this.price = price;
		this.purchase = purchase;
        this.product = product;
    }

	public PurchaseProductId getId() {
        return id;
    }

	public PurchaseProduct id(Long purchaseId, Long productId) {
		setId(new PurchaseProductId(purchaseId, productId));
        return this;
    }

	public void setId(PurchaseProductId id) {
        this.id = id;
    }

    public BigDecimal getCount() {
        return this.count;
    }

    public PurchaseProduct count(BigDecimal count) {
        this.setCount(count);
        return this;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public PurchaseProduct price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

	public Purchase getPurchase() {
		return this.purchase;
    }

	public void setPurchase(Purchase purchase) {
		this.purchase = purchase;
		if (purchase != null) {
			this.id.setPurchaseId(purchase.getId());
        }
    }

	public PurchaseProduct purchase(Purchase purchase) {
		this.setPurchase(purchase);
        return this;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
        if (product != null) {
            this.id.setProductId(product.getId());
        }
    }

    public PurchaseProduct product(Product product) {
        this.setProduct(product);
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PurchaseProduct)) {
            return false;
        }
        return id != null && id.equals(((PurchaseProduct) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
		return "PurchaseProduct{" +
            "id=" + getId() +
            ", count=" + getCount() +
            ", price=" + getPrice() +
            "}";
    }
}
