package spring.bbs.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;

public class RequestResponseCreator<T> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ResourceLoader resourceLoader;
    final Class<T> typeParameterClass;
    private T resource;

    public RequestResponseCreator(Class<T> typeParameterClass, ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.typeParameterClass = typeParameterClass;
    }

    private String getPath(String resourcePath) throws Exception {
        Resource r = resourceLoader.getResource("classpath:" + resourcePath);
        return r.getURI().getPath();
    }

    public T get(String resourcePath) throws Exception {
        if (resource == null){
            String path = getPath(resourcePath);
            resource = objectMapper
                    .readValue(new File(path), typeParameterClass);
        }
        return resource;
    }
}
