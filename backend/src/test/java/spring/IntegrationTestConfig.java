package spring;


import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.TestClassOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import spring.profileResolver.ProfileConfiguration;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ProfileConfiguration
@AutoConfigureMockMvc
@SpringBootTest
public abstract class IntegrationTestConfig {
}
