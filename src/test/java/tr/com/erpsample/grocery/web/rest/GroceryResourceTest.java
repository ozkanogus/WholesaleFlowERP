package tr.com.erpsample.grocery.web.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import tr.com.erpsample.grocery.repository.GroceryRepository;
import tr.com.erpsample.grocery.service.GroceryService;
import tr.com.erpsample.grocery.service.dto.GroceryDTO;
import tr.com.erpsample.grocery.web.rest.errors.BadRequestAlertException;

@ExtendWith(MockitoExtension.class)
class GroceryResourceTest {

	@Mock
	private GroceryService service;

	@Mock
	private GroceryRepository repository;

	@InjectMocks
	private GroceryResource resource;

	@Test
	void createReturnsCreatedResourceAndLocation() throws URISyntaxException {
		GroceryDTO request = GroceryDTO.builder().name("Central Market").build();
		GroceryDTO saved = GroceryDTO.builder().id(42L).name("Central Market").build();
		when(service.save(request)).thenReturn(saved);

		ResponseEntity<GroceryDTO> response = resource.createGrocery(request);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("/api/groceries/42", response.getHeaders().getLocation().toString());
		assertEquals(saved, response.getBody());
	}

	@Test
	void createRejectsClientSuppliedId() {
		GroceryDTO request = GroceryDTO.builder().id(42L).name("Central Market").build();

		assertThrows(BadRequestAlertException.class, () -> resource.createGrocery(request));
	}

	@Test
	void updateRejectsUnknownEntity() {
		GroceryDTO request = GroceryDTO.builder().id(42L).name("Central Market").build();
		when(repository.existsById(42L)).thenReturn(false);

		assertThrows(BadRequestAlertException.class, () -> resource.updateGrocery(request));
	}

	@Test
	void missingEntityCurrentlyReturnsOkWithEmptyBody() {
		when(service.findOne(42L)).thenReturn(Optional.empty());

		ResponseEntity<GroceryDTO> response = resource.getProduct(42L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Test
	void deleteReturnsNoContent() {
		ResponseEntity<Void> response = resource.deleteGrocery(42L);

		verify(service).delete(42L);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}
}
