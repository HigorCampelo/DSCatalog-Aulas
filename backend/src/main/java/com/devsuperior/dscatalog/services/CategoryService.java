package com.devsuperior.dscatalog.services;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {
    
	@Autowired
	private CategoryRepository repository;
	
	@Transactional(readOnly = true )
	public List<CategoryDTO> findAll(){
		List<Category> list = repository.findAll();
		
		return list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
			
		/*List<CategoryDTO> listDto = new ArrayList<>();
		for (Category cat : list) {
			listDto.add(new CategoryDTO(cat));
		}
		return listDto;
		*/
	}
	
	@Transactional(readOnly = true )
	public Page<CategoryDTO> findAllPaged(Pageable pageable){
		Page<Category> list = repository.findAll(pageable);
		return list.map(x -> new CategoryDTO(x));
	}

	@Transactional(readOnly = true )
	public CategoryDTO findById(Long id) {
		Optional<Category> obj = repository.findById(id);
		Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Id não encontrado"));
		return new CategoryDTO(entity);
	}
	
	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category category = new Category();
		category.setName(dto.getName());
	    return new CategoryDTO(repository.save(category));
	}
	
	@Transactional
	public CategoryDTO update(CategoryDTO dto, Long id) {
		try {
		    @SuppressWarnings("deprecation")
			Category category = repository.getOne(id);
		    category.setName(dto.getName());
		    return new CategoryDTO(repository.save(category));
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
}
