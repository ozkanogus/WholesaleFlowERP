package tr.com.erpsample.grocery.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tr.com.erpsample.grocery.domain.Product;
import tr.com.erpsample.grocery.domain.enumeration.Unit;
import tr.com.erpsample.grocery.repository.ProductRepository;
import tr.com.erpsample.grocery.service.dto.ProductDTO;
import tr.com.erpsample.grocery.service.mapper.ProductMapper;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

	@InjectMocks
	private ProductService service;

	@Mock
	private ProductRepository repository;

	@Mock
	private ProductMapper mapper;

	@Test
	void givenProductDto_thenSave_expectGivenDto() {

		// given
		ProductDTO dto = ProductDTO.builder().id(-1L).name("given Product Dto").unit(Unit.KG).build();

		Product entity = Product.builder().id(-1L).name("given Product Entity").unit(Unit.KG).build();

		// when
		when(mapper.toEntity(dto)).thenReturn(entity);
		when(repository.save(entity)).thenReturn(entity);
		when(mapper.toDto(entity)).thenReturn(dto);

		// then
		ProductDTO result = service.save(dto);

		assertEquals(dto, result);
		
	}
}
