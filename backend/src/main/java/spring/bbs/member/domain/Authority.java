package spring.bbs.member.domain;

public enum Authority {
    ROLE_USER {
        @Override
        public String toString() {return "ROLE_USER";}
    }
    , ROLE_ADMIN{
        @Override
        public String toString() {return "ROLE_ADMIN";}
    }
}
