//package spring.bbs;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.util.FileCopyUtils;
//
//import java.io.IOException;
//import java.io.InputStreamReader;
//
//@Component
//@RequiredArgsConstructor
//public class SQLRunner implements ApplicationRunner {
//
//    private final JdbcTemplate jdbcTemplate;
//
//    @Override
//    public void run(ApplicationArguments args){
//        Resource resource = new ClassPathResource("ddl.sql");
//        try {
//            String sqlScript = FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream()));
//            jdbcTemplate.execute(sqlScript);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
