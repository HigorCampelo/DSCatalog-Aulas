package com.devsuperior.dscatalog.resources;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;

@RestController
@RequestMapping(value = "/products")
public class ProductResource {
    
	@Autowired
	private ProductService service;
	
	//Sem paginação
	/*@GetMapping
	public ResponseEntity<List<ProductDTO>> findAll(){
		//List<Product> list = service.findAll();
		return ResponseEntity.ok().body(service.findAll());
	}*/
	
	@GetMapping
	public ResponseEntity<Page<ProductDTO>> findAllPaged(Pageable pageable){
		// Parametros: page, size, sort
		
		Page<ProductDTO> list = service.findAllPaged(pageable);
		return ResponseEntity.ok().body(list);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> findById(@PathVariable Long id){
		//List<Product> list = service.findAll();
		return ResponseEntity.ok().body(service.findById(id));
	}
	
	@PostMapping
	public ResponseEntity<ProductDTO> insert(@Valid @RequestBody ProductDTO dto){
		dto = service.insert(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
				  .path("/{id}").buildAndExpand(dto.getId()).toUri();
		return ResponseEntity.created(uri).body(dto);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> update(@Valid @RequestBody ProductDTO dto, @PathVariable Long id ){
		return ResponseEntity.ok().body(service.update(dto, id));
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> delete( @PathVariable Long id ){
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
