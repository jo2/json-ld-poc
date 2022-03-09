package io.blogtrack.poc;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
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

public class LdParser {

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

    public Metadata getMetadataFromArticleUrl(String url) {
        List<Map<String, Object>> graph = getJsonLdElements(url);

        Map<String, Object> creativeWork = graph.stream()
                .filter(element -> validCreativeWorks.contains(element.get("@type")))
                .findFirst()
                .orElseThrow();

        String author = "N/A";
        if (creativeWork.get("author") instanceof Map) {
            Map<String, Object> authorMap = ((Map<String, Object>) creativeWork.get("author"));
            if (authorMap.containsKey("@id")) {
                String authorId = (String) authorMap.get("@id");

                Map<String, Object> person = graph.stream()
                        .filter(element -> element.get("@id").equals(authorId))
                        .findFirst()
                        .orElseThrow();
                author = (String) person.get("name");
            } else {
                author = (String) authorMap.get("name");
            }
        }

        return Metadata.builder()
                .author(author)
                .publicationDate(getLocalDateFromPublishedDate(creativeWork))
                .title((String) creativeWork.get("headline"))
                .wordCount(getWordCountFromArticle(creativeWork))
                .build();
    }

    private List<Map<String, Object>> getJsonLdElements(String url) {
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new ClientException(e.getMessage());
        }

        List<Map<String, Object>> objectMap = new ArrayList<>();
        doc.select("script[type='application/ld+json']").dataNodes().stream()
                .map(DataNode::getWholeData).collect(Collectors.toList())
                .forEach(node -> {
                    DocumentContext context = JsonPath.using(config).parse(node);
                    if (checkIfGraph(context)) {
                        // parse graph to (sub) list
                        objectMap.addAll(context.read("$['@graph']"));
                    } else if (context.read("$['@type']") != null) {
                        // parse node directly
                        objectMap.add(context.read("$"));
                    } else if (context.read("$..['@type']") != null) {
                        // parse all nodes directly
                        objectMap.addAll(context.read("$"));
                    }
                });

        return objectMap;
    }

    private Integer getWordCountFromArticle(Map<String, Object> creativeWork) {
        Object wordCount = creativeWork.get("wordCount");
        return wordCount == null ? null : Integer.parseInt(wordCount.toString());
    }

    private LocalDate getLocalDateFromPublishedDate(Map<String, Object> creativeWork) {
        if (!creativeWork.containsKey("datePublished")) {
            return null;
        }

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .appendOptional(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"))
                .toFormatter();
        try {
            return LocalDate.parse((String) creativeWork.get("datePublished"), formatter.withLocale(Locale.ENGLISH));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private boolean checkIfGraph(DocumentContext context) {
        return context.read("$['@graph']") != null &&
                context.read("$['@graph']") instanceof List &&
                !((List<Map<String, Object>>) context.read("$['@graph']")).isEmpty();
    }
}
