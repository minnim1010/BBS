package spring.bbs.common.constant;

public class Api {

    private Api() {
    }

    public static final String URI_PREFIX = "/api";
    public static final String VERSION = "/v1";

    public static class Domain {

        private Domain() {
        }

        public static final String POST = "/posts";
        public static final String MEMBER = "/members";
        public static final String COMMENT = "/comments";
    }
}
