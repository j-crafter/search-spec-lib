package eu.rimbaud.libs.search.search;

import eu.rimbaud.libs.search.SearchSpecification;
import eu.rimbaud.libs.search.entity.Book;
import eu.rimbaud.libs.search.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FluentSpecificationTest {

    @Autowired
    BookRepository bookRepository;

    @BeforeEach
    void beforeEach() {
        final var book1Title = "Le Petit Prince";
        final var book2Title = "Charlie et la Chocolaterie ";
        bookRepository.saveAllAndFlush(List.of(
                Book.builder()
                        .title(book1Title)
                        .publicationDate(LocalDate.of(1943, 4, 6))
                        .build(),
                Book.builder()
                        .title(book2Title)
                        .publicationDate(LocalDate.of(1964, 1, 1))
                        .build()
        ));
    }

    @Test
    void givenTitle_whenSearchWithEq_thenReturnsBook() {
        final var bookTitle = "Le Petit Prince";
        final var spec = new SearchSpecification<Book>()
                .add(Book.Fields.title).eq(bookTitle);
        final var book = bookRepository.findOne(spec);
        assertThat(book).isNotEmpty().get()
                .satisfies(b -> assertThat(bookTitle).isEqualTo(b.getTitle()));
    }

    @Test
    void givenWrongTitle_whenSearchWithEq_thenReturnsEmpty() {
        final var wrongTitle = "Les Malheurs de Sophie";
        final var spec = new SearchSpecification<Book>()
                .add(Book.Fields.title).eq(wrongTitle);
        final var book = bookRepository.findOne(spec);
        assertThat(book).isEmpty();
    }

    @Test
    void givenTitle_whenSearchWithNe_thenReturnsOtherBook() {
        final var book1Title = "Le Petit Prince";
        final var book2Title = "Charlie et la Chocolaterie ";
        final var spec = new SearchSpecification<Book>()
                .add(Book.Fields.title).ne(book1Title);
        final var book = bookRepository.findOne(spec);
        assertThat(book).isNotEmpty().get()
                .satisfies(b -> assertThat(book2Title).isEqualTo(b.getTitle()));
    }

    @Test
    void givenWrongTitle_whenSearchWithNe_thenReturnsBook() {
        final var book1Title = "Le Petit Prince";
        final var book2Title = "Charlie et la Chocolaterie ";
        final var spec = new SearchSpecification<Book>()
                .add(Book.Fields.title).ne(book1Title);
        final var book = bookRepository.findOne(spec);
        assertThat(book).isNotEmpty().get()
                .satisfies(b -> assertThat(book2Title).isEqualTo(b.getTitle()));
    }

    @Test
    void givenTitle_whenSearchWithLike_thenReturnsBook() {
        final var spec = new SearchSpecification<Book>()
                .add(Book.Fields.title).like("et");
        final var books = bookRepository.findAll(spec);
        assertThat(books).isNotEmpty()
                .hasSize(2);
    }

    @Test
    void givenWrongTitle_whenSearchWithLike_thenReturnsEmpty() {
        final var spec = new SearchSpecification<Book>()
                .add(Book.Fields.title).like("Grand");
        final var books = bookRepository.findAll(spec);
        assertThat(books).isEmpty();
    }

    @Test
    void givenTitle_whenSearchWithIn_thenReturnsBook() {
        final var bookTitle = "Le Petit Prince";
        final var wrongTitle = "Les Malheurs de Sophie";
        final var spec = new SearchSpecification<Book>()
                .add(Book.Fields.title).in(List.of(bookTitle, wrongTitle));
        final var books = bookRepository.findAll(spec);
        assertThat(books).isNotEmpty()
                .hasSize(1)
                .satisfies(b -> assertThat(bookTitle).isEqualTo(b.get(0).getTitle()));
    }

    @Test
    void givenEmptyList_whenSearchWithIn_thenReturnsAll() {
        final var spec = new SearchSpecification<Book>()
                .add(Book.Fields.title).in(List.of());
        final var books = bookRepository.findAll(spec);
        assertThat(books).isNotEmpty()
                .hasSize(2);
    }

    @Test
    void givenWrongTitle_whenSearchWithIn_thenReturnsEmpty() {
        final var bookTitle = "Les Malheurs de Sophie";
        final var spec = new SearchSpecification<Book>()
                .add(Book.Fields.title).in(List.of(bookTitle));
        final var books = bookRepository.findAll(spec);
        assertThat(books).isEmpty();
    }

    @Test
    void givenEmptyList_whenSearchWithStrictlyIn_thenReturnsEmpty() {
        final var spec = new SearchSpecification<Book>()
                .add(Book.Fields.title).strictlyIn(List.of());
        final var books = bookRepository.findAll(spec);
        assertThat(books).isEmpty();
    }

    @Test
    void givenTitle_whenSearchWithNotIn_thenReturnsBook() {
        final var book1Title = "Le Petit Prince";
        final var wrongTitle = "Les Malheurs de Sophie";
        final var book2Title = "Charlie et la Chocolaterie ";
        final var spec = new SearchSpecification<Book>()
                .add(Book.Fields.title).notIn(List.of(book1Title, wrongTitle));
        final var books = bookRepository.findAll(spec);
        assertThat(books).isNotEmpty()
                .hasSize(1)
                .satisfies(b -> assertThat(book2Title).isEqualTo(b.get(0).getTitle()));
    }

    @Test
    void givenWrongTitle_whenSearchWithNotIn_thenReturnsEmpty() {
        final var book1Title = "Le Petit Prince";
        final var book2Title = "Charlie et la Chocolaterie ";
        final var spec = new SearchSpecification<Book>()
                .add(Book.Fields.title).notIn(List.of(book1Title, book2Title));
        final var books = bookRepository.findAll(spec);
        assertThat(books).isEmpty();
    }

    @Test
    void givenDate_whenSearchWithGt_thenReturnsBook() {
        final var bookTitle = "Charlie et la Chocolaterie ";
        final var spec = new SearchSpecification<Book>()
                .add(Book.Fields.publicationDate).gt(LocalDate.of(1950, 1, 1));
        final var books = bookRepository.findAll(spec);
        assertThat(books).isNotEmpty()
                .hasSize(1)
                .satisfies(b -> assertThat(bookTitle).isEqualTo(b.get(0).getTitle()));
    }

    @Test
    void givenWrongDate_whenSearchWithGt_thenReturnsEmpty() {
        final var spec = new SearchSpecification<Book>()
                .add(Book.Fields.publicationDate).gt(LocalDate.of(2000, 1, 1));
        final var books = bookRepository.findAll(spec);
        assertThat(books).isEmpty();
    }

    @Test
    void givenDate_whenSearchWithGte_thenReturnsBook() {
        final var bookTitle = "Charlie et la Chocolaterie ";
        final var spec = new SearchSpecification<Book>()
                .add(Book.Fields.publicationDate).gte(LocalDate.of(1964, 1, 1));
        final var books = bookRepository.findAll(spec);
        assertThat(books).isNotEmpty()
                .hasSize(1)
                .satisfies(b -> assertThat(bookTitle).isEqualTo(b.get(0).getTitle()));
    }

    @Test
    void givenWrongDate_whenSearchWithGte_thenReturnsEmpty() {
        final var spec = new SearchSpecification<Book>()
                .add(Book.Fields.publicationDate).gte(LocalDate.of(2000, 1, 1));
        final var books = bookRepository.findAll(spec);
        assertThat(books).isEmpty();
    }

    @Test
    void givenDate_whenSearchWithLt_thenReturnsBook() {
        final var bookTitle = "Le Petit Prince";
        final var spec = new SearchSpecification<Book>()
                .add(Book.Fields.publicationDate).lt(LocalDate.of(1950, 1, 1));
        final var books = bookRepository.findAll(spec);
        assertThat(books).isNotEmpty()
                .hasSize(1)
                .satisfies(b -> assertThat(bookTitle).isEqualTo(b.get(0).getTitle()));
    }

    @Test
    void givenWrongDate_whenSearchWithLt_thenReturnsEmpty() {
        final var spec = new SearchSpecification<Book>()
                .add(Book.Fields.publicationDate).lt(LocalDate.of(1900, 1, 1));
        final var books = bookRepository.findAll(spec);
        assertThat(books).isEmpty();
    }

    @Test
    void givenDate_whenSearchWithLte_thenReturnsBook() {
        final var bookTitle = "Le Petit Prince";
        final var spec = new SearchSpecification<Book>()
                .add(Book.Fields.publicationDate).lte(LocalDate.of(1943, 4, 6));
        final var books = bookRepository.findAll(spec);
        assertThat(books).isNotEmpty()
                .hasSize(1)
                .satisfies(b -> assertThat(bookTitle).isEqualTo(b.get(0).getTitle()));
    }

    @Test
    void givenWrongDate_whenSearchWithLte_thenReturnsEmpty() {
        final var spec = new SearchSpecification<Book>()
                .add(Book.Fields.publicationDate).lte(LocalDate.of(1900, 1, 1));
        final var books = bookRepository.findAll(spec);
        assertThat(books).isEmpty();
    }
}
