package tr.com.erpsample.grocery.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tr.com.erpsample.grocery.domain.Grocery;
import tr.com.erpsample.grocery.repository.GroceryRepository;
import tr.com.erpsample.grocery.service.dto.GroceryDTO;
import tr.com.erpsample.grocery.service.mapper.GroceryMapper;

@ExtendWith(MockitoExtension.class)
public class GroceryServiceTest {

	@InjectMocks
	private GroceryService service;

	@Mock
	private GroceryRepository repository;

	@Mock
	private GroceryMapper mapper;

	@Test
	void givenGroceryDto_thenSave_expectGivenDto() {

		// given
		GroceryDTO dto = GroceryDTO.builder().id(-1L).name("given Grocery Dto").build();

		Grocery entity = Grocery.builder().id(-1L).name("given Grocery Entity").build();

		// when
		when(mapper.toEntity(dto)).thenReturn(entity);
		when(repository.save(entity)).thenReturn(entity);
		when(mapper.toDto(entity)).thenReturn(dto);

		// then
		GroceryDTO result = service.save(dto);

		assertEquals(dto, result);
	}

	@Test
	void givenGroceryId_thenFindOne_expectDto() {

		// given
		Long groceryId = -1L;
		GroceryDTO dto = GroceryDTO.builder().id(-1L).name("given Grocery Dto").build();

		Grocery entity = Grocery.builder().id(-1L).name("given Grocery Entity").build();

		Optional<GroceryDTO> optionalDto = Optional.ofNullable(dto);

		// when
		when(mapper.toDto(entity)).thenReturn(dto);
		when(repository.findById(anyLong())).thenReturn(Optional.of(entity));

		// then
		Optional<GroceryDTO> result = service.findOne(groceryId);

		assertEquals(optionalDto, result);
	}
	
	
}
