package spring.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import spring.bbs.category.repository.CategoryRepository;
import spring.bbs.category.repository.CategoryRepositoryHandler;

@TestConfiguration
@EnableJpaAuditing
public class TestConfig {

    @Autowired
    private CategoryRepository categoryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public CategoryRepositoryHandler categoryRepositoryHandler(){
        return new CategoryRepositoryHandler(categoryRepository);
    }
}
