package spring.bbs.post.service;

import java.util.Arrays;

public enum SearchScope {
    TITLE_AND_CONTENT("전체"),
    TITLE("제목"),
    AUTHOR("작성자");

    private final String displayName;

    SearchScope(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static boolean contains(String value) {
        return Arrays.stream(values())
            .anyMatch(scope -> scope.getDisplayName().equalsIgnoreCase(value));
    }
}
