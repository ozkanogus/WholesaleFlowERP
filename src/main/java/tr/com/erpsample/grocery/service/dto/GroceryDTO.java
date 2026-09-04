package tr.com.erpsample.grocery.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link tr.com.erpsample.grocery.domain.Grocery} entity.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroceryDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private Instant createdDate;

    private Instant lastModifiedDate;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GroceryDTO)) {
            return false;
        }

        GroceryDTO groceryDTO = (GroceryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, groceryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }


    @Override
    public String toString() {
        return "GroceryDTO{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", createdDate=" + createdDate +
            ", lastModifiedDate=" + lastModifiedDate +
            '}';
    }
}
