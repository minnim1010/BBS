package spring.bbs.common.util;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class QuerydslUtil {
    public static OrderSpecifier[] getOrderSpecifier(Sort sort, PathBuilder pathBuilder) {
        List<OrderSpecifier> orders = new ArrayList<>();

        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String property = order.getProperty();
            orders.add(new OrderSpecifier(direction, pathBuilder.get(property)));
        });

        return orders.stream().toArray(OrderSpecifier[]::new);
    }
}
