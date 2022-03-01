package com.devsuperior.dscatalog.services;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {
    
	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CategoryRepository catRepository;
	
	@Transactional(readOnly = true )
	public List<ProductDTO> findAll(){
		List<Product> list = repository.findAll();
		
		return list.stream().map(x -> new ProductDTO(x)).collect(Collectors.toList());
			
		/*List<ProductDTO> listDto = new ArrayList<>();
		for (Product cat : list) {
			listDto.add(new ProductDTO(cat));
		}
		return listDto;
		*/
	}
	
	@Transactional(readOnly = true )
	public Page<ProductDTO> findAllPaged(PageRequest pageRequest){
		Page<Product> list = repository.findAll(pageRequest);
		
		return list.map(x -> new ProductDTO(x));
	}

	@Transactional(readOnly = true )
	public ProductDTO findById(Long id) {
		Optional<Product> obj = repository.findById(id);
		Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Id não encontrado"));
		return new ProductDTO(entity, entity.getCategories());
	}
	
	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product product = new Product();
		copyDtoToEntity(dto, product);
		product = repository.save(product);
	    return new ProductDTO(product);
	}
	
	@Transactional
	public ProductDTO update(ProductDTO dto, Long id) {
		try {
		    @SuppressWarnings("deprecation")
			Product product = repository.getOne(id);
		    copyDtoToEntity(dto, product);
		    return new ProductDTO(repository.save(product));
		}
		catch(EntityNotFoundException e) {
			 throw new ResourceNotFoundException("Id not found " + id);
		}
		
	}
	
	public void delete( Long id) {
		try {
			repository.deleteById(id);
		}
		catch(EmptyResultDataAccessException e) {
			 throw new ResourceNotFoundException("Id not found " + id);
		}
		catch(DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity Violation");
		}
		
	}
	
	private void copyDtoToEntity(ProductDTO dto, Product product) {
		product.setName(dto.getName());
		product.setDescription(dto.getDescription());
		product.setPrice(dto.getPrice());
		product.setImgUrl(dto.getImgUrl());
		product.setDate(dto.getDate());
		
		product.getCategories().clear();
		
		for (CategoryDTO catDto : dto.getCategories()) {
			@SuppressWarnings("deprecation")
			Category category = catRepository.getOne(catDto.getId());
			product.getCategories().add(category);
		}
	}
}
