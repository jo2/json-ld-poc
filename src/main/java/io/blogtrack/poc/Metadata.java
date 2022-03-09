package io.blogtrack.poc;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Metadata {
    private String author;
    private Integer wordCount;
    private LocalDate publicationDate;
    private String title;
}
