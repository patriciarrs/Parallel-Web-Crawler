package com.udacity.webcrawler;

import com.udacity.webcrawler.parser.PageParserFactory;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class CrawlInternalActionFactory {
    private final Clock clock;
    private final PageParserFactory parserFactory;
    private final List<Pattern> ignoredUrls;
    private final Instant deadline;
    private final Set<String> visitedUrls;
    private final Map<String, Integer> counts;

    public CrawlInternalActionFactory(Clock clock, PageParserFactory parserFactory, List<Pattern> ignoredUrls,
                                      Instant deadline, Set<String> visitedUrls, Map<String, Integer> counts) {
        this.clock = clock;
        this.parserFactory = parserFactory;
        this.ignoredUrls = ignoredUrls;
        this.deadline = deadline;
        this.visitedUrls = visitedUrls;
        this.counts = counts;
    }

    public CrawlInternalAction create(String url, int maxDepth) {
        /*
         * We can just pass `this` directly since the factory holds shared state
         * (instead of creating a new factory instance each time).
         */
        return new CrawlInternalAction(this, url, maxDepth);
    }
    
    public Clock getClock() {
        return clock;
    }

    public PageParserFactory getParserFactory() {
        return parserFactory;
    }

    public List<Pattern> getIgnoredUrls() {
        return ignoredUrls;
    }

    public Instant getDeadline() {
        return deadline;
    }

    public Set<String> getVisitedUrls() {
        return visitedUrls;
    }

    public Map<String, Integer> getCounts() {
        return counts;
    }
}
