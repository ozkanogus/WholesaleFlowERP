package tr.com.erpsample.grocery.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tr.com.erpsample.grocery.domain.Sale;
import tr.com.erpsample.grocery.service.dto.ProductDTO;

/**
 * Spring Data SQL repository for the Sale entity.
 */
@Repository
public interface SaleRepository extends JpaRepository<Sale, Long>, JpaSpecificationExecutor<ProductDTO> {

	@Query(value = "select sub.product_id from \r\n"
			+ "	(select b.product_id,sum(b.count) as count from public.sale a left join public.sale_product b on a.id = b.sale_id \r\n"
			+ "			where to_char(a.created_date,'YYYY-MM')  =to_char(now(),'YYYY-MM') and a.grocery_id=:paramGroceryId\r\n"
			+ "			group by b.product_id order by count desc, b.product_id asc \r\n"
			+ "			limit 3) sub order by sub.count desc, sub.product_id asc", nativeQuery = true)
	List<Long> findTopSoldThreeProduct(@Param("paramGroceryId") Long groceryId);
}
