# Search Specifications library
Spring boot library that provides fluent API to build specifications using operators. This aims to be used in advanced search-like features.

## Table of contents
* [Quick start](#quick-start)
* [Usage](#usage)

## Quick start

Maven:
```xml
<dependency>
    <groupId>eu.rimbaud.libs</groupId>
    <artifactId>search-spec-lib</artifactId>
</dependency>
```

Gradle:
```groovy
dependencies {
    implementation 'eu.rimbaud.libs:search-spec-lib'
}
```

## Usage
<sup>[back to table of contents](#table-of-contents)</sup>
* [Basic](#basic)
* [Advanced](#advanced)

### Basic

```java
Specification<Book> spec = new SearchSpecification<>()
        .add("title").like("le")
        .add("publicationDate").gt(LocalDate.of(2024, 1, 1));
Collection<Book> books = bookRepository.findAll(spec);
```

To auto generate field names, instead of using plain text attribute names, it is recommended to use:
- [hibernate-jpamodelgen](https://hibernate.org/orm/tooling/)
- or Lombok [@FieldNameConstants](https://projectlombok.org/features/experimental/FieldNameConstants) (experimental)

The query would like this.

```java
Specification<Book> spec = new SearchSpecification<>()
        .add(Book.Fields.TITLE).like("le")
        .add(Book.Fields.PUBLICATION_DATE).gt(LocalDate.of(2024, 1, 1));
Collection<Book> books = bookRepository.findAll(spec);
```

### Advanced

`SearchSpecification` is the entry point to the library and the only public implementation.

The defined conditions are applied using `&&` logical expressions only.

The supported operators are located in [`SearchOperationEnum`](src/main/java/eu/rimbaud/libs/search/SearchOperationEnum.java):
- equal
- not equal
- like
- in
- not in
- greater than (only for dates)
- greater or equal than (only for dates)
- lower than (only for dates)
- lower or equal than (only for dates)

If a criterion has to be specified on an entity relationship, you can set the path to the field to filter:

```java
Specification<Book> spec = new SearchSpecification<>()
        .add(Book.Fields.TITLE).like("le")
        .add(Book.Fields.AUTHORS, Author.Fields.NAME).eq("Conan Doyle");
Collection<Book> books = bookRepository.findAll(spec);
```
