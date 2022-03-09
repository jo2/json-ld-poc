package io.blogtrack.poc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;

public class PocApplication {

    private static final List<String> validCreativeWorks = List.of(
            "TechArticle",
            "BlogPosting",
            "Article",
            "CreativeWork"
    );

    private static final Configuration config = Configuration.builder()
            .jsonProvider(new JacksonJsonProvider())
            .mappingProvider(new JacksonMappingProvider())
            .options(EnumSet.of(Option.SUPPRESS_EXCEPTIONS))
            .build();

    public static void main(String[] args) {
        LdParser ldParser = new LdParser();

        List<Metadata> metadata = Stream.of(
                        "https://rieckpil.de/difference-between-mock-and-mockbean-spring-boot-applications/",
                        "https://rieckpil.de/run-java-tests-with-maven-silently-only-log-on-failure/",
                        "https://gist.githubusercontent.com/patrickcoombe/9d0f6b1a4c566ac2ca16/raw/075e1ba582407554c8c075aaea41707d8bca0b9d/blog-posting.js",
                        "https://www.joydeepdeb.com/blog/json-ld-schema.html",
                        "https://www.shaunpoore.com/jsonld-schema-for-blogpost/",
                        "https://gist.githubusercontent.com/ardianta/0d3b3ded059f7e6d07b3528793376b5e/raw/586587e7f0784d8a43fedacd59572a7f6dcedead/json-ld-blogger.xml"
                )
                .map(ldParser::getMetadataFromArticleUrl)
                .collect(Collectors.toList());

        metadata.forEach(System.out::println);
    }
}
