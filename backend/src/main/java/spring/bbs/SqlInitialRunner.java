package spring.bbs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;

@ConditionalOnProperty(name = "spring.jpa.hibernate.ddl-auto", havingValue = "create")
@Component
@RequiredArgsConstructor
@Slf4j
public class SqlInitialRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args){
        Resource resource = new ClassPathResource("ddl.sql");
        try {
            String sqlScript = FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream()));
            jdbcTemplate.execute(sqlScript);
            log.debug("Insert Category ('string', 'Java', 'Spring')");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
