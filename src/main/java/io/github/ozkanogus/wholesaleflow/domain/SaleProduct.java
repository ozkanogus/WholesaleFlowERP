package io.github.ozkanogus.wholesaleflow.domain;

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

/**
 * A SaleProduct.
 */
@Entity
@Table(name = "sale_product")
public class SaleProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private SaleProductId id;

    @NotNull
    @Column(name = "count", precision = 21, scale = 2, nullable = false)
    private BigDecimal count;

    @NotNull
    @Column(name = "price", precision = 21, scale = 2, nullable = false)
    private BigDecimal price;

    @ManyToOne(optional = false)
    @MapsId("saleId")
    @NotNull
    @JsonIgnoreProperties(value = { "products", "grocery" }, allowSetters = true)
    private Sale sale;

    @ManyToOne(optional = false)
    @MapsId("productId")
    @NotNull
    private Product product;

    public SaleProduct() {
        this.id = new SaleProductId();
    }

    public SaleProduct(BigDecimal count, BigDecimal price, Sale sale, Product product) {
        this.count = count;
        this.price = price;
        this.sale = sale;
        this.product = product;
    }

    public SaleProductId getId() {
        return id;
    }

    public SaleProduct id(Long saleId, Long productId) {
        setId(new SaleProductId(saleId, productId));
        return this;
    }

    public void setId(SaleProductId id) {
        this.id = id;
    }

    public BigDecimal getCount() {
        return this.count;
    }

    public SaleProduct count(BigDecimal count) {
        this.setCount(count);
        return this;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public SaleProduct price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Sale getSale() {
        return this.sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
        if (sale != null) {
            this.id.setSaleId(sale.getId());
        }
    }

    public SaleProduct sale(Sale sale) {
        this.setSale(sale);
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

    public SaleProduct product(Product product) {
        this.setProduct(product);
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SaleProduct)) {
            return false;
        }
        return id != null && id.equals(((SaleProduct) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SaleProduct{" +
            "id=" + getId() +
            ", count=" + getCount() +
            ", price=" + getPrice() +
            "}";
    }
}
