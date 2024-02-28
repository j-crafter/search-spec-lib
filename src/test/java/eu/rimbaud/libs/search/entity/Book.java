package eu.rimbaud.libs.search.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;

@Builder
@FieldNameConstants
@Getter
@Entity
@Table
public class Book {

    @Id
    @GeneratedValue
    private final Long id;

    private final String author;

    private final String title;

    private final LocalDate publicationDate;

}
