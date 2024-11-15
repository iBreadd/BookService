package com.example.BookService.CombinedBody.service;

import com.example.BookService.CombinedBody.enums.HtmlTag;
import com.example.BookService.CombinedBody.exception.InvalidInputException;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class ValidationService {
    private static final int FIRST_ELEMENT = 1;
    private static final int MAX_ALLOWED_COLUMNS = 5;
    private static final String NESTED_TABLE_ERROR = "Nested <table> elements are not allowed.";
    private static final String TABLE_BAD_STRUCTURE = "Table must contain only tr as child elements.";
    private static final String SECTION_START_MSG = "Customer Information has to start with <section>";
    private static final String H2_START_MSG = "Customer Information has to have an h2 as the first element of its section.";
    private static final String LOG_ERROR_MESSAGE = "Validation failed with message: {}";
    private static final String ANCHOR_HREF_ID_ERROR = "Id inside href has invalid format.";
    private static final String ANCHOR_HREF_LINK_ERROR = "Anchor Link inside href has invalid format.It should be in format [#^\\s]";
    private static final String ID = "id";
    private static final String TABLE_BAD_SYNTAX = "Table must have the same number of columns in each row.";
    private static final String TABLE_ROW_BAD_SYNTAX = "Table rows must contain only valid tags th and td.";
    private static final String TAGS_NOT_PROPERLY_CLOSED = "The following tags are not properly closed: %s";
    private static final String LOG_ERROR_MESSAGE_WITH_DATA = "Validation failed with message: {} and data: {}";
    private static final Set<String> allowedHtmlTags = Arrays.stream(HtmlTag.values())
            .map(HtmlTag::getTag)
            .collect(Collectors.toSet());
    private final Parser parser;
    private static final String INVALID_TAGS = "Invalid tags used in the html snippet: %s";
    public void validateHtmlSnippet(String htmlSnippet) throws InvalidInputException {
        log.info("Started validation for html snippet with hash {}", htmlSnippet.hashCode());

        Document document = parseSnippet(htmlSnippet);

        htmlSnippetTagValidator(htmlSnippet, document);

        List<Element> elements = getAllElements(document);
        List<Element> tables = elements.stream()
                .filter(el -> HtmlTag.TABLE.getTag().equalsIgnoreCase(el.tagName()))
                .toList();

        validateSnippetTags(elements);
        try {
            Element sectionElement = findElementByTag(elements, HtmlTag.SECTION.getTag());
            validateTextAroundSection(sectionElement);
            validateElementTag(elements.get(0), HtmlTag.SECTION.getTag(), SECTION_START_MSG);
            validateElementTag(sectionElement.child(0), HtmlTag.H2.getTag(), H2_START_MSG);
        } catch (NoSuchElementException | IndexOutOfBoundsException e) {
            throw new InvalidInputException(SECTION_START_MSG + "\n" + H2_START_MSG);
        }

        for (Element table : tables) {
            validateNoNestedTables(table);
            validateTableStructure(table);
        }

        allowAnchorDefinitions(elements);

        validateAnchorInsideLink(elements);

        log.info("Finished validation for html snippet with hash: {}", htmlSnippet.hashCode());
    }
    private Document parseSnippet(String htmlSnippet) {
        return parser.parseInput(htmlSnippet, "");
    }
    private void htmlSnippetTagValidator(String htmlSnippet, Document document) throws InvalidInputException {
        Map<String, Boolean> tagValidity = new HashMap<>();
        allowedHtmlTags
                .forEach(tag -> {
                    boolean isValid = countTagsInOriginalHtml(tag, htmlSnippet) == countTagsInParsedHtml(tag, document);
                    tagValidity.put(tag, isValid);
                });

        String invalidTags = tagValidity.entrySet().stream()
                .filter(entry -> !entry.getValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", "));

        if (!invalidTags.isBlank()) {
            log.error(LOG_ERROR_MESSAGE_WITH_DATA, TAGS_NOT_PROPERLY_CLOSED, invalidTags);
            throw new InvalidInputException(TAGS_NOT_PROPERLY_CLOSED, invalidTags);
        }
    }

    @NotNull
    private List<Element> getAllElements(Document document) {
        return document.getAllElements()
                .stream()
                .skip(FIRST_ELEMENT)
                .toList();
    }

    private void validateSnippetTags(List<Element> elements) throws InvalidInputException {
        List<String> foundElements = elements.stream()
                .map(el -> el.tagName().toLowerCase())
                .filter(name -> !allowedHtmlTags.contains(name))
                .toList();
        if (!foundElements.isEmpty()) {
            log.error(LOG_ERROR_MESSAGE_WITH_DATA, INVALID_TAGS, foundElements);
            throw new InvalidInputException(INVALID_TAGS, foundElements.toString());
        }
    }
    private Element findElementByTag(List<Element> elements, String tag) {
        return elements.stream()
                .filter(element -> tag.equalsIgnoreCase(element.tagName()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Element with tag <" + tag + "> not found."));
    }
    private void validateTextAroundSection(Element sectionElement) throws InvalidInputException {

        Node prevSibling = sectionElement.previousSibling();
        while (prevSibling != null) {
            if (prevSibling instanceof TextNode && !((TextNode) prevSibling).isBlank()) {
                throw new InvalidInputException("Text found before <section> element.");
            }
            prevSibling = prevSibling.previousSibling();
        }

        // Check for text after the <section> element
        Node nextSibling = sectionElement.nextSibling();
        while (nextSibling != null) {
            if (nextSibling instanceof TextNode && !((TextNode) nextSibling).isBlank()) {
                throw new InvalidInputException("Text found after </section> element.");
            }
            nextSibling = nextSibling.nextSibling();
        }
    }
    private void validateElementTag(Element element, String tag, String errorMessage) throws InvalidInputException {
        if (!element.tagName().equals(tag)) {
            log.error(LOG_ERROR_MESSAGE, errorMessage);
            throw new InvalidInputException(errorMessage);
        }
    }
    private void validateNoNestedTables(Element element) throws InvalidInputException {
        if (element.tagName().equalsIgnoreCase(HtmlTag.TABLE.getTag()) &&
                element.parents()
                        .stream()
                        .anyMatch(parent -> HtmlTag.TABLE.getTag().equalsIgnoreCase(parent.tagName()))) {
            log.error(LOG_ERROR_MESSAGE, NESTED_TABLE_ERROR);
            throw new InvalidInputException(NESTED_TABLE_ERROR);
        }
    }
    private void validateTableStructure(Element table) throws InvalidInputException {
        List<Element> tableRows = validateTableContent(table);
        for (Element tableRow : tableRows) {
            validateRowContent(tableRow);
        }
        validateTableRowStructure(tableRows);
    }
    private List<Element> validateTableContent(Element table) throws InvalidInputException {
        List<Element> tableRows = table.children().stream()
                .filter(el -> HtmlTag.TR.getTag().equalsIgnoreCase(el.tagName()))
                .toList();

        if (table.children().size() != tableRows.size()) {
            log.error(LOG_ERROR_MESSAGE, TABLE_BAD_STRUCTURE);
            throw new InvalidInputException(TABLE_BAD_STRUCTURE);
        }

        return tableRows;
    }
    private void allowAnchorDefinitions(List<Element> elements) throws InvalidInputException {
        List<Element> anchorh2Elements = elements.stream()
                .filter(el -> HtmlTag.H2.getTag().equalsIgnoreCase(el.tagName()) && el.hasAttr(ID))
                .toList();

        for (Element anchor : anchorh2Elements) {
            String id = anchor.attr(ID);
            if (!id.matches("^[a-zA-Z0-9_-]+$")) {
                log.error("Invalid anchor definition format found: {}", id);
                throw new InvalidInputException(ANCHOR_HREF_ID_ERROR);
            }
        }
    }
    private void validateAnchorInsideLink(List<Element> elements) throws InvalidInputException {
        List<Element> anchorElements = elements.stream()
                .filter(el -> HtmlTag.A.getTag().equalsIgnoreCase(el.tagName()))
                .toList();
        for (Element anchor : anchorElements) {
            String href = anchor.attr("href");
            if (!href.matches("#\\S+")) {
                log.error("Invalid anchor link format found: {}", href);
                throw new InvalidInputException(ANCHOR_HREF_LINK_ERROR);
            }
        }
    }
    private int countTagsInOriginalHtml(String tag, String html) {
        String tagOpenPattern = "<" + tag + "(\\s+[^>]*)?>";
        int openTags = html.split(tagOpenPattern, -1).length - 1;
        int closeTags = html.split("</" + tag + ">", -1).length - 1;
        return openTags + closeTags;
    }
    private int countTagsInParsedHtml(String tag, Document doc) {
        Elements elements = doc.select(tag);
        return elements.size() * 2;
    }

    private void validateRowContent(Element tableRow) throws InvalidInputException {
        List<String> invalidTags = tableRow.children().stream()
                .map(Element::tagName)
                .filter(name -> !HtmlTag.TH.getTag().equalsIgnoreCase(name) && !HtmlTag.TD.getTag().equalsIgnoreCase(name))
                .toList();

        if (!invalidTags.isEmpty()) {
            log.error(LOG_ERROR_MESSAGE, TABLE_ROW_BAD_SYNTAX);
            throw new InvalidInputException(TABLE_ROW_BAD_SYNTAX);
        }
    }
    private void validateTableRowStructure(List<Element> tableRows) throws InvalidInputException {
        int columns = tableRows.get(0).children().size();
        for (Element tableRow : tableRows) {
            if (tableRow.children().size() != columns || tableRow.children().size() > MAX_ALLOWED_COLUMNS) {
                log.error(LOG_ERROR_MESSAGE, TABLE_BAD_SYNTAX);
                throw new InvalidInputException(TABLE_BAD_SYNTAX);
            }
        }
    }
}
