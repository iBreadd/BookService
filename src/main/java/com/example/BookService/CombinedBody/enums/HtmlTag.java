package com.example.BookService.CombinedBody.enums;

import lombok.Getter;

@Getter
public enum HtmlTag {
    SECTION("section"),
    H2("h2"),
    H3("h3"),
    H4("h4"),
    H5("h5"),
    TABLE("table"),
    TH("th"),
    TR("tr"),
    TD("td"),
    B("b"),
    I("i"),
    UL("ul"),
    LI("li"),
    OL("ol"),
    P("p"),
    A("a");

    private final String tag;

    HtmlTag(String tag) {
        this.tag = tag;
    }

}
