package io.github.sweehaw.websupports.page;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sweehaw
 */
public class PagingSpecificationTools {

    private Specification<Object> specification;
    private HashMap<String, Object> map;

    @SuppressWarnings("unchecked")
    public Predicate[] getPredicateList(Root<Object> root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicates = new ArrayList<>();
        for (Specification specification : getSpecifications()) {
            Predicate p = specification.toPredicate(root, criteriaQuery, criteriaBuilder);
            predicates.add(p);
        }

        Predicate[] predicateArr = new Predicate[predicates.size()];
        predicateArr = predicates.toArray(predicateArr);

        return predicateArr;
    }

    public Specification<Object> getSpecification() {

        List<Specification<Object>> specifications = this.getSpecifications();
        int integer = 0;
        int zero = 0;

        for (Specification<Object> s : specifications) {
            this.specification = integer == zero
                    ? Specification.where(s)
                    : this.specification.and(s);
            integer++;
        }
        return this.specification;
    }

    private List<Specification<Object>> getSpecifications() {

        List<Specification<Object>> specifications = new ArrayList<>();
        for (Map.Entry<String, Object> entry : this.map.entrySet()) {

            String k = entry.getKey();
            Object v = entry.getValue();

            specifications.add(v instanceof PagingDateCriteria
                    ? new PagingSpecification(k, "~", v)
                    : new PagingSpecification(k, ":", v)
            );
        }

        return specifications;
    }
}
