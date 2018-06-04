package io.github.sweehaw.websupports.page;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

/**
 * @author sweehaw
 */
@AllArgsConstructor
public class PagingSpecification implements Specification<Object> {

    private String key;
    private String operation;
    private Object value;

    @Override
    @SuppressWarnings("unchecked")
    public Predicate toPredicate(Root<Object> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        Path path = null;
        String[] split = this.key.split("\\.");

        for (String s : split) {
            path = path == null ? root.get(s) : path.get(s);
        }

        assert path != null;
        switch (this.operation) {
            case "=":
                return builder.equal(path, this.value.toString());

            case ">":
                return builder.greaterThan(path, this.value.toString());

            case "<":
                return builder.lessThan(path, this.value.toString());

            case ">=":
                return builder.greaterThanOrEqualTo(path, this.value.toString());

            case "<=":
                return builder.lessThanOrEqualTo(path, this.value.toString());

            case ":":
                return path.getJavaType() == String.class
                        ? builder.like(path, "%" + this.value + "%")
                        : builder.equal(path, this.value);
            case "~":
                PagingDateCriteria dateCriteria = (PagingDateCriteria) this.value;
                return builder.between(path, dateCriteria.getStartDate(), dateCriteria.getEndDate());

            default:
                return null;
        }
    }
}
