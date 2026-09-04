package tr.com.erpsample.grocery.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A Sale.
 */
@Entity
@Table(name = "sale")
public class Sale extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @NotNull
    private Grocery grocery;

	@OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "sale", "product" }, allowSetters = true)
    private Set<SaleProduct> products = new HashSet<>();


    public Long getId() {
        return this.id;
    }

    public Sale id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Grocery getGrocery() {
        return this.grocery;
    }

    public void setGrocery(Grocery grocery) {
        this.grocery = grocery;
    }

    public Sale grocery(Grocery grocery) {
        this.setGrocery(grocery);
        return this;
    }

    public Set<SaleProduct> getProducts() {
        return this.products;
    }

    public void setProducts(Set<SaleProduct> saleProducts) {
        if (this.products != null) {
            this.products.forEach(i -> i.setSale(null));
        }
        if (saleProducts != null) {
            saleProducts.forEach(i -> i.setSale(this));
        }
        this.products = saleProducts;
    }

    public Sale products(Set<SaleProduct> saleProducts) {
        this.setProducts(saleProducts);
        return this;
    }

    public void addProduct(SaleProduct saleProduct) {
        this.products.add(saleProduct);
        saleProduct.sale(this);
        saleProduct.getId().setSaleId(this.id);
    }

    public Sale removeProduct(SaleProduct saleProduct) {
        this.products.remove(saleProduct);
        saleProduct.setSale(null);
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sale)) {
            return false;
        }
        return id != null && id.equals(((Sale) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Sale{" +
            "id=" + getId() +
            "}";
    }
}
