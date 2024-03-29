package com.devsuperior.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {
	
	@Autowired
	private MockMvc mockmvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private ProductService service;
	
	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private PageImpl<ProductDTO> page;
	private ProductDTO productDTO;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 4L;
		productDTO = Factory.createProductDTO();
		page = new PageImpl<>(List.of(productDTO));
		
		when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);
		
		when(service.insert(any())).thenReturn(productDTO);
		
		when(service.findById(existingId)).thenReturn(productDTO);
		when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
		
		when(service.update(any(), eq(existingId))).thenReturn(productDTO);
		when(service.update(any(), eq(nonExistingId))).thenThrow(ResourceNotFoundException.class);
		
	    doNothing().when(service).delete(existingId);
		doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
		doThrow(DatabaseException.class).when(service).delete(dependentId);
	}
	
	@Test
	public void findAllPagedShouldReturnPage() throws Exception {
		
		ResultActions result =
		                     mockmvc.perform(get("/products")
				             .accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
	}
	
	@Test
	public void findByIdShouldReturnProductWheIdExists() throws Exception {
		
		ResultActions result =
		                     mockmvc.perform(get("/products/{id}", existingId)
				             .accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWheIdDoesNotExists() throws Exception {
		
		 ResultActions result =
		                     mockmvc.perform(get("/products/{id}", nonExistingId)
				             .accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound()); 
	}

	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		 ResultActions result =
                 mockmvc.perform(put("/products/{id}", existingId)
                 .content(jsonBody)	
                 .contentType(MediaType.APPLICATION_JSON)
	             .accept(MediaType.APPLICATION_JSON));
		 
		 result.andExpect(status().isOk());
		 result.andExpect(jsonPath("$.id").exists());
		 result.andExpect(jsonPath("$.name").exists());
		 result.andExpect(jsonPath("$.description").exists());
		
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		 ResultActions result =
                mockmvc.perform(put("/products/{id}", nonExistingId)
                .content(jsonBody)	
                .contentType(MediaType.APPLICATION_JSON)
	             .accept(MediaType.APPLICATION_JSON));
		 
		 result.andExpect(status().isNotFound());
		 
	}
	
	@Test
	public void insertShouldReturnProductDTOAndStatusCreated() throws Exception {
	
       String jsonBody = objectMapper.writeValueAsString(productDTO);
       
		 ResultActions result =
                 mockmvc.perform(post("/products")
                		 .content(jsonBody)	
                         .contentType(MediaType.APPLICATION_JSON)
	                     .accept(MediaType.APPLICATION_JSON));

         result.andExpect(status().isCreated()); 
         result.andExpect(jsonPath("$.id").exists());
		 result.andExpect(jsonPath("$.name").exists());
		 result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
		
		 ResultActions result =
                 mockmvc.perform(delete("/products/{id}", existingId)
	             .accept(MediaType.APPLICATION_JSON));

         result.andExpect(status().isNoContent()); 
		
	}
	
	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		
		 ResultActions result =
                 mockmvc.perform(delete("/products/{id}", nonExistingId)
	             .accept(MediaType.APPLICATION_JSON));

         result.andExpect(status().isNotFound()); 
		
	}
}
