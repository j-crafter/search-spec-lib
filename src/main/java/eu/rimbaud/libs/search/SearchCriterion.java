
package eu.rimbaud.libs.search;

import jakarta.persistence.criteria.Root;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

/**
 * Class used for building Spring {@link Specification} in an easy manner. It uses chaining for better readability.
 * A {@literal SearchCriterion} will result on one predicate on the resulting {@literal Specification}.
 *
 * @param <T> the type of the {@link Root} the resulting {@literal Specification} operates on.
 */
@Getter
@Setter
@ToString
public class SearchCriterion<T> {

    @ToString.Exclude
    private SearchSpecification<T> specifications;

    private boolean condition = true;
    private boolean strict = false;

    private String[] fields;
    private SearchOperationEnum operator;
    private Object value;
    private Comparable<?> comparable;

    /**
     * Construct a {@link SearchCriterion} from a field path
     *
     * @param fields         the field path as an array of string field
     * @param specifications the {@link Specification} for chaining
     */
    public SearchCriterion(String[] fields, SearchSpecification<T> specifications) {
        this.fields = fields;
        this.specifications = specifications;
    }

    /**
     * Apply an operator and a value on the {@link SearchCriterion}
     *
     * @param operator the operator
     * @param value    the related value
     * @return the {@link Specification} for chaining
     */
    private SearchSpecification<T> apply(SearchOperationEnum operator, Object value) {
        this.operator = operator;
        this.value = value;
        return this.specifications;
    }

    /**
     * The {@link SearchCriterion} will be added to the {@link Specification} predicate only of the condition is true, or ignored otherwise
     *
     * @param condition the condition
     * @return the {@link SearchCriterion} for chaining
     */
    public SearchCriterion<T> onlyIf(Boolean condition) {
        this.condition = Boolean.TRUE.equals(condition);
        return this;
    }

    /**
     * Check whether a value has been set, or in the case of a {@literal Collection}, if it is not empty
     *
     * @return true if a value has been set, or in the case of a {@literal Collection}, if it is not empty
     */
    public boolean hasValue() {
        return this.value != null
                && !(this.value instanceof Collection<?> coll && coll.isEmpty());
    }

    /**
     * Apply an "equals" operator and a value on the {@link SearchCriterion}
     *
     * @param value the related value
     * @return the {@link Specification} for chaining
     */
    public SearchSpecification<T> eq(Object value) {
        return this.apply(SearchOperationEnum.EQUALS, value);
    }

    /**
     * Apply a "not equals" operator and a value on the {@link SearchCriterion}
     *
     * @param value the related value
     * @return the {@link Specification} for chaining
     */
    public SearchSpecification<T> ne(Object value) {
        return this.apply(SearchOperationEnum.NOT_EQUAL, value);
    }

    /**
     * Apply a "like" operator and a {@literal String} value on the {@link SearchCriterion}
     *
     * @param value the related value
     * @return the {@link Specification} for chaining
     */
    public SearchSpecification<T> like(String value) {
        return this.apply(SearchOperationEnum.LIKE, value);
    }

    /**
     * Apply an "in" operator and a {@literal Collection} value on the {@link SearchCriterion}
     *
     * @param value the related value
     * @return the {@link Specification} for chaining
     */
    public SearchSpecification<T> in(Collection<?> value) {
        return this.apply(SearchOperationEnum.IN, value);
    }

    /**
     * Apply a "not in" operator and a {@literal Collection} value on the {@link SearchCriterion}
     *
     * @param value the related value
     * @return the {@link Specification} for chaining
     */
    public SearchSpecification<T> notIn(Collection<?> value) {
        return this.apply(SearchOperationEnum.NOT_IN, value);
    }

    /**
     * Apply a strict "in" operator and a {@literal Collection} value on the {@link SearchCriterion}.
     * If the collection is empty, the {@literal SearchCriterion} will still be applied.
     *
     * @param value the related value
     * @return the {@link Specification} for chaining
     */
    public SearchSpecification<T> strictlyIn(Collection<?> value) {
        this.strict = true;
        return this.apply(SearchOperationEnum.IN, value);
    }

    /**
     * Apply a "greater than" operator and a value on the {@link SearchCriterion}
     *
     * @param value the related value
     * @return the {@link Specification} for chaining
     */
    public <Y extends Comparable<? super Y>> SearchSpecification<T> gt(Y value) {
        return this.apply(SearchOperationEnum.GREATER_THAN, value);
    }

    /**
     * Apply a "greater than or equal to" operator and a value on the {@link SearchCriterion}
     *
     * @param value the related value
     * @return the {@link Specification} for chaining
     */
    public <Y extends Comparable<? super Y>> SearchSpecification<T> gte(Y value) {
        return this.apply(SearchOperationEnum.GREATER_THAN_EQUAL, value);
    }

    /**
     * Apply a "less than" operator and a value on the {@link SearchCriterion}
     *
     * @param value the related value
     * @return the {@link Specification} for chaining
     */
    public <Y extends Comparable<? super Y>> SearchSpecification<T> lt(Y value) {
        return this.apply(SearchOperationEnum.LESS_THAN, value);
    }

    /**
     * Apply a "less than or equal to" operator and a value on the {@link SearchCriterion}
     *
     * @param value the related value
     * @return the {@link Specification} for chaining
     */
    public <Y extends Comparable<? super Y>> SearchSpecification<T> lte(Y value) {
        return this.apply(SearchOperationEnum.LESS_THAN_EQUAL, value);
    }
}
