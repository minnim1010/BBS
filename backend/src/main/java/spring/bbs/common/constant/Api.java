package spring.bbs.common.constant;

public class Api {

    private Api() {
    }

    public static final String FRONT_ORIGIN = "http://localhost:8080";

    public static final String URI_PREFIX = "/api";
    public static final String VERSION = "/v1";

    public static class Domain {

        private Domain() {
        }

        public static final String AUTH = URI_PREFIX + VERSION + "/auth";
        public static final String POST = URI_PREFIX + VERSION + "/posts";
        public static final String MEMBER = URI_PREFIX + VERSION + "/members";
        public static final String COMMENT = URI_PREFIX + VERSION + "/comments";
    }
}
