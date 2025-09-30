package md.usm.bookstore.service;

import md.usm.bookstore.dto.AuthorDto;
import md.usm.bookstore.exception.StoreException;
import md.usm.bookstore.model.Author;
import md.usm.bookstore.repository.AuthorRepository;
import md.usm.bookstore.utils.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    private final Mapper mapper;

    public AuthorService(AuthorRepository authorRepository, Mapper mapper) {
        this.authorRepository = authorRepository;
        this.mapper = mapper;
    }

    @Transactional
    public AuthorDto create(AuthorDto dto) {
        Author author = mapper.toEntity(dto);
        Author saved = authorRepository.save(author);
        return mapper.toDto(saved);
    }

    public Page<AuthorDto> getAll(Pageable pageable) {
        Page<Author> authorsPage = authorRepository.findAllAuthors(pageable);
        List<Author> authorsWithBooks = authorRepository.fetchBooksForAuthors(authorsPage.getContent());

        List<AuthorDto> dtoList = authorsWithBooks.stream()
                .map(mapper::toDto)
                .toList();

        return new PageImpl<>(dtoList, pageable, authorsPage.getTotalElements());
    }

    public AuthorDto getById(Long id) {
        Author author = authorRepository.findByIdWithBooks(id);
        if (author == null) {
            throw new StoreException(
                    "Author not found with id " + id,
                    "NOT_FOUND",
                    HttpStatus.NOT_FOUND.value()
            );
        }
        return mapper.toDto(author);
    }

    public Author getEntityById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new StoreException(
                        "Author not found with id " + id,
                        "NOT_FOUND",
                        HttpStatus.NOT_FOUND.value()
                ));
    }

    @Transactional
    public AuthorDto update(Long id, AuthorDto dto) {
        Author existing = getEntityById(id);

        if (dto.firstName() != null) {
            existing.setFirstName(dto.firstName());
        }
        if (dto.lastName() != null) {
            existing.setLastName(dto.lastName());
        }

        authorRepository.save(existing);
        return getById(existing.getId());
    }

    public void delete(Long id) {
        Author existing = getEntityById(id);
        authorRepository.delete(existing);
    }

}
