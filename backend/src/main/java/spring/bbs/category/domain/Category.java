package spring.bbs.category.domain;

import jakarta.persistence.*;
import spring.bbs.written.post.domain.Post;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="CATEGORY",
        indexes = @Index(name="idx__name", columnList = "name", unique = true))
public class Category {
    @Id @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Post> posts = new ArrayList<>();

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Post> getPosts() {
        return posts;
    }
}
