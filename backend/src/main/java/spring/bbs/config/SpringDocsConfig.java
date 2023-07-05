package spring.bbs.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocsConfig {

    @Bean
    public OpenAPI openAPI(@Value("${springdoc.version}") String version) {

        Info info = new Info()
                .title("BBS API")
                .version(version)
                .description("전자 게시판 API");

        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}