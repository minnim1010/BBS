package spring.bbs.member.domain;

public enum Authority {
    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN");

    private final String displayName;

    Authority(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
