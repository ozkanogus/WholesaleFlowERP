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


@Entity
@Table(name = "purchase")
public class Purchase extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @NotNull
    private Grocery grocery;

	@OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "purchase", "product" }, allowSetters = true)
	private Set<PurchaseProduct> products = new HashSet<>();


    public Long getId() {
        return this.id;
    }

    public Purchase id(Long id) {
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

    public Purchase grocery(Grocery grocery) {
        this.setGrocery(grocery);
        return this;
    }

	public Set<PurchaseProduct> getProducts() {
        return this.products;
    }

	public void setProducts(Set<PurchaseProduct> purchaseProducts) {
        if (this.products != null) {
			this.products.forEach(i -> i.setPurchase(null));
        }
		if (purchaseProducts != null) {
			purchaseProducts.forEach(i -> i.setPurchase(this));
        }
		this.products = purchaseProducts;
    }

	public Purchase products(Set<PurchaseProduct> purchaseProducts) {
		this.setProducts(purchaseProducts);
        return this;
    }

	public void addProduct(PurchaseProduct purchaseProduct) {
		this.products.add(purchaseProduct);
		purchaseProduct.purchase(this);
		purchaseProduct.getId().setPurchaseId(this.id);
    }

	public Purchase removeProduct(PurchaseProduct purchaseProduct) {
		this.products.remove(purchaseProduct);
		purchaseProduct.setPurchase(null);
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Purchase)) {
            return false;
        }
        return id != null && id.equals(((Purchase) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
		return "Purchase{" +
            "id=" + getId() +
            "}";
    }
}
