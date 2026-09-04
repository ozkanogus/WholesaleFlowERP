package tr.com.erpsample.grocery.initializer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import tr.com.erpsample.grocery.domain.Grocery;
import tr.com.erpsample.grocery.domain.Product;
import tr.com.erpsample.grocery.domain.enumeration.Unit;
import tr.com.erpsample.grocery.repository.GroceryRepository;
import tr.com.erpsample.grocery.repository.ProductRepository;
import tr.com.erpsample.grocery.service.PurchaseService;
import tr.com.erpsample.grocery.service.SaleService;
import tr.com.erpsample.grocery.service.dto.ProductSalePurchaseDTO;
import tr.com.erpsample.grocery.service.dto.PurchaseDTO;
import tr.com.erpsample.grocery.service.dto.SaleDTO;
import tr.com.erpsample.grocery.service.mapper.GroceryMapper;

@Component
@ConditionalOnProperty(name = "grocery.demo-data.enabled", havingValue = "true")
public class DataPopulator implements CommandLineRunner {

	@Autowired
	private GroceryMapper groceryMapper;

	@Autowired
	private GroceryRepository groceryRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private PurchaseService purchaseService;

	@Autowired
	private SaleService saleService;

	@Override
	public void run(String... args) throws Exception {

		Grocery grocery;
		Product product;
		Optional<Grocery> optionalGrocery;
		Optional<Product> optionalProduct;
		ArrayList<Grocery> groceryList = new ArrayList<Grocery>();
		ArrayList<Product> productList = new ArrayList<Product>();
		for (int i = 0; i < 10; i++) {
			grocery = new Grocery();
			grocery.setName("Grocery " + (i + 1));
			optionalGrocery = groceryRepository.findByNameIgnoreCase(grocery.getName());
			if (!optionalGrocery.isPresent())
				groceryList.add(groceryRepository.save(grocery));

			product = new Product();
			product.setName("Product " + (i + 1));
			product.setUnit(Unit.values()[i % 3]);
			optionalProduct = productRepository.findByNameIgnoreCase(product.getName());
			if (!optionalProduct.isPresent())
				productList.add(productRepository.save(product));
		}

		populateSales(groceryList, productList);

		populateSPurchases(groceryList, productList);

	}

	private void populateSPurchases(ArrayList<Grocery> groceryList, ArrayList<Product> productList) {
		PurchaseDTO purchaseDTO;
		Set<ProductSalePurchaseDTO> products;
		ProductSalePurchaseDTO productSalePurchaseDTO;
		for (Grocery g : groceryList) {
			purchaseDTO = new PurchaseDTO();
			products = new HashSet<ProductSalePurchaseDTO>();
			purchaseDTO.setGrocery(groceryMapper.toDto(g));
			for (Product p : productList) {
				productSalePurchaseDTO = new ProductSalePurchaseDTO();
				productSalePurchaseDTO.setProductId(p.getId());
				productSalePurchaseDTO.setCount(new BigDecimal(BigInteger.valueOf(new Random().nextInt(11, 21))));
				productSalePurchaseDTO
						.setPrice(new BigDecimal(BigInteger.valueOf(new Random().nextInt(10001, 20001)), 2));
				products.add(productSalePurchaseDTO);
			}
			purchaseDTO.setProducts(products);
			purchaseService.save(purchaseDTO);
		}

	}

	private void populateSales(ArrayList<Grocery> groceryList, ArrayList<Product> productList) {
		SaleDTO saleDTO;
		Set<ProductSalePurchaseDTO> products;
		ProductSalePurchaseDTO productSalePurchaseDTO;
		for (int i = 0; i < 3; i++) {
			for (Grocery g : groceryList) {
				saleDTO = new SaleDTO();
				products = new HashSet<ProductSalePurchaseDTO>();
				saleDTO.setGrocery(groceryMapper.toDto(g));
				for (Product p : productList) {
					productSalePurchaseDTO = new ProductSalePurchaseDTO();
					productSalePurchaseDTO.setProductId(p.getId());
					productSalePurchaseDTO.setCount(new BigDecimal(BigInteger.valueOf(new Random().nextInt(1, 11))));
					productSalePurchaseDTO
							.setPrice(new BigDecimal(BigInteger.valueOf(new Random().nextInt(1000, 10001)), 2));
					products.add(productSalePurchaseDTO);
				}
				saleDTO.setProducts(products);
				saleService.save(saleDTO);
			}
		}

	}
}
