package spring.bbs.category.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import spring.bbs.category.domain.Category;
import spring.bbs.common.exceptionhandling.exception.DataNotFoundException;

@RequiredArgsConstructor
@Component
public class CategoryRepositoryHandler {
    private final CategoryRepository categoryRepository;

    public Category findByName(String name) {
        return categoryRepository.findByName(name).orElseThrow(
            () -> new DataNotFoundException("카테고리가 존재하지 않습니다."));
    }
}
