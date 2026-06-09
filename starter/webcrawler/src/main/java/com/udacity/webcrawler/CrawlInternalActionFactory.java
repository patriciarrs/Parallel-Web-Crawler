package com.udacity.webcrawler;

import com.udacity.webcrawler.parser.PageParserFactory;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public record CrawlInternalActionFactory(Clock clock, PageParserFactory parserFactory, List<Pattern> ignoredUrls,
                                         Instant deadline, Set<String> visitedUrls, Map<String, Integer> counts) {

    public CrawlInternalAction create(String url, int maxDepth) {
        /*
         * We can just pass `this` directly since the factory holds shared state
         * (instead of creating a new factory instance each time).
         */
        return new CrawlInternalAction(this, url, maxDepth);
    }
}
