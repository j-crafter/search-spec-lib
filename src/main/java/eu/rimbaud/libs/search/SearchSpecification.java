
package eu.rimbaud.libs.search;

import jakarta.annotation.Nonnull;
import jakarta.persistence.criteria.*;
import lombok.ToString;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used for building Spring {@link Specification} in an easy manner. It uses chaining for better readability.
 * The {@literal SearchSpecification} is built by adding criteria. The criteria are added using {@link SearchCriterion}.
 *
 * @param <T> the type of the {@link Root} the resulting {@literal Specification} operates on.
 */
@ToString
public class SearchSpecification<T> implements Specification<T> {

    /**
     * The list of {@link SearchCriterion} that will be applied.
     */
    private final List<SearchCriterion<T>> criteria = new ArrayList<>();

    /**
     * Convert the {@literal SearchSpecification} into a {@link Predicate} itself being an addition of all criteria converted into {@literal Predicate}.
     * <p>A {@literal Predicate} will be added if the {@link SearchCriterion} fulfills the conditions:
     * <ul><li>has {@literal true} as <code>condition</code></li>
     * <li>is a <code>strict</code> or has a value</li></ul></p>
     *
     * @param root  must not be {@literal null}.
     * @param query must not be {@literal null}.
     * @param cb    must not be {@literal null}.
     * @return the resulting {@literal Predicate}
     */
    @Override
    public Predicate toPredicate(@Nonnull Root<T> root, @Nonnull CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.and(criteria.stream()
                .filter(SearchCriterion::isCondition)
                .filter(c -> c.isStrict() || c.hasValue())
                .map(c -> toPredicate(c, root, cb))
                .toArray(Predicate[]::new));
    }

    /**
     * Convert one {@link SearchCriterion} into a {@link Predicate}.
     *
     * @param sc   the {@literal SearchCriterion}
     * @param root must not be {@literal null}.
     * @param cb   must not be {@literal null}.
     * @return the resulting {@literal Predicate}
     */
    private Predicate toPredicate(SearchCriterion<T> sc, Root<T> root, CriteriaBuilder cb) {
        String[] fields = sc.getFields();
        Path<T> path = root;
        Join<?, T> join = null;
        String field = fields[0];
        for (var i = 1; i < fields.length; ++i) {
            if (join == null) {
                join = root.join(fields[0]);
                field = fields[1];
            } else {
                join = join.join(fields[i - 1]);
                field = fields[i];
            }
            path = join;
        }

        return toPredicate(sc, cb, path, field);
    }

    /**
     * Convert one {@link SearchCriterion} into a {@link Predicate}.
     *
     * @param sc    the {@literal SearchCriterion}
     * @param cb    must not be {@literal null}.
     * @param path  the simple or compound attribute {@link Path}
     * @param field the field the {@link Predicate} will be applied on
     * @return the resulting {@literal Predicate}
     */
    private Predicate toPredicate(SearchCriterion<T> sc, CriteriaBuilder cb, Path<?> path, String field) {
        return switch (sc.getOperator()) {
            case EQUALS -> cb.equal(path.get(field), sc.getValue());
            case NOT_EQUAL -> cb.notEqual(path.get(field), sc.getValue());
            case LIKE -> cb.like(cb.lower(path.get(field)), "%" + sc.getValue().toString().toLowerCase() + "%");
            case IN -> cb.in(path.get(field)).value(sc.getValue());
            case NOT_IN -> cb.in(path.get(field)).value(sc.getValue()).not();
            default -> toComparablePredicate(sc, cb, path, field);
        };
    }

    /**
     * Convert one {@link SearchCriterion} into a {@link Predicate}.
     *
     * @param <Y>   the comparable type, used for compare operators
     * @param sc    the {@literal SearchCriterion}
     * @param cb    must not be {@literal null}.
     * @param path  the simple or compound attribute {@link Path}
     * @param field the field the {@link Predicate} will be applied on
     * @return the resulting {@literal Predicate}
     */
    @SuppressWarnings("unchecked")
    private <Y extends Comparable<? super Y>> Predicate toComparablePredicate(SearchCriterion<T> sc, CriteriaBuilder cb, Path<?> path, String field) {
        return switch (sc.getOperator()) {
            case LESS_THAN -> cb.lessThan(path.get(field), (Y) sc.getValue());
            case LESS_THAN_EQUAL -> cb.lessThanOrEqualTo(path.get(field), (Y) sc.getValue());
            case GREATER_THAN -> cb.greaterThan(path.get(field), (Y) sc.getValue());
            case GREATER_THAN_EQUAL -> cb.greaterThanOrEqualTo(path.get(field), (Y) sc.getValue());
            default -> throw new UnsupportedOperationException("Operator not implemented yet: " + sc.getOperator());
        };
    }

    /**
     * Add a {@link SearchCriterion} to the {@link Specification}
     *
     * @param fields the field path that will result in a simple or compound attribute {@link Path}
     * @return the resulting {@literal SearchCriterion} for chaining
     */
    public SearchCriterion<T> add(String... fields) {
        var searchCriteria = new SearchCriterion<>(fields, this);
        criteria.add(searchCriteria);
        return searchCriteria;
    }
}
