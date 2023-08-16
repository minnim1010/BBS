package spring.bbs.base.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWritten is a Querydsl query type for Written
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QWritten extends EntityPathBase<Written> {

    private static final long serialVersionUID = -751595666L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWritten written = new QWritten("written");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final spring.bbs.member.domain.QMember author;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdTime = _super.createdTime;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedTime = _super.lastModifiedTime;

    public QWritten(String variable) {
        this(Written.class, forVariable(variable), INITS);
    }

    public QWritten(Path<? extends Written> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWritten(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWritten(PathMetadata metadata, PathInits inits) {
        this(Written.class, metadata, inits);
    }

    public QWritten(Class<? extends Written> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new spring.bbs.member.domain.QMember(forProperty("author")) : null;
    }

}

