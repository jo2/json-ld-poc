package io.blogtrack.poc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LdParserTest {

    private final LdParser ldParser = new LdParser();

    @Test
    void testRieckpil() {
        assertEquals(
                Metadata.builder()
                        .author("Philip Riecks")
                        .publicationDate(LocalDate.of(2020, 11, 10))
                        .title("@Mock vs. @MockBean When Testing Spring Boot Applications")
                        .wordCount(1107)
                        .build(),
                ldParser.getMetadataFromArticleUrl("https://rieckpil.de/difference-between-mock-and-mockbean-spring-boot-applications/"));
    }

    @Test
    void testPatrickcoombe() {
        assertEquals(
                Metadata.builder()
                        .author("Steve")
                        .publicationDate(LocalDate.of(2015, 9, 20))
                        .title("14 Ways Json Can Improve Your SEO")
                        .wordCount(null)
                        .build(),
                ldParser.getMetadataFromArticleUrl("https://gist.githubusercontent.com/patrickcoombe/9d0f6b1a4c566ac2ca16/raw/075e1ba582407554c8c075aaea41707d8bca0b9d/blog-posting.js"));
    }

    @Test
    void testJoydeepdeb() {
        assertEquals(
                Metadata.builder()
                        .author("Joydeep Deb")
                        .publicationDate(LocalDate.of(2020, 3, 29))
                        .title("JSON-LD Schema for Google Rich Result Features")
                        .wordCount(null)
                        .build(),
                ldParser.getMetadataFromArticleUrl("https://www.joydeepdeb.com/blog/json-ld-schema.html"));
    }

    @Test
    void testShaunpoore() {
        assertEquals(
                Metadata.builder()
                .author("Shaun Poore")
                        .publicationDate(LocalDate.of(2021, 4, 18))
                        .title("Code Needed to Add Schema.org To Your Blog Post (And Why It&#8217;s Worth It!)")
                        .wordCount(947)
                        .build(),
                ldParser.getMetadataFromArticleUrl("https://www.shaunpoore.com/jsonld-schema-for-blogpost/"));
    }

    @Test
    void testArdianta() {
        assertEquals(
                Metadata.builder()
                        .author("<data:post.author/>")
                        .publicationDate(null)
                        .title("<data:post.title/>")
                        .wordCount(null)
                        .build(),
                ldParser.getMetadataFromArticleUrl("https://gist.githubusercontent.com/ardianta/0d3b3ded059f7e6d07b3528793376b5e/raw/586587e7f0784d8a43fedacd59572a7f6dcedead/json-ld-blogger.xml"));
    }
}
