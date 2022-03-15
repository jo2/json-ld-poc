package io.blogtrack.poc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PocApplication {

    public static void main(String[] args) {
        LdParser ldParser = new LdParser();

        List<Metadata> metadata = Stream.of(
                        "https://rieckpil.de/difference-between-mock-and-mockbean-spring-boot-applications/"
                )
                .map(ldParser::getMetadataFromArticleUrl)
                .collect(Collectors.toList());

        metadata.forEach(System.out::println);
    }
}
