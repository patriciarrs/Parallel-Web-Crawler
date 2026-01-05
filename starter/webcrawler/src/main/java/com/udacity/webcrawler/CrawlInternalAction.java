package com.udacity.webcrawler;

import com.udacity.webcrawler.parser.PageParser;
import com.udacity.webcrawler.parser.PageParserFactory;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveAction;
import java.util.regex.Pattern;

public class CrawlInternalAction extends RecursiveAction {
    private final Clock clock;
    private final PageParserFactory parserFactory;
    private final int maxDepth;
    private final List<Pattern> ignoredUrls;
    private final Instant deadline;
    private final String url;
    private final Set<String> visitedUrls;
    private final Map<String, Integer> counts;

    public CrawlInternalAction(Clock clock,
                               PageParserFactory parserFactory,
                               @MaxDepth int maxDepth,
                               @IgnoredUrls List<Pattern> ignoredUrls,
                               Instant deadline,
                               String url,
                               Set<String> visitedUrls,
                               Map<String, Integer> counts) {
        this.clock = clock;
        this.parserFactory = parserFactory;
        this.maxDepth = maxDepth;
        this.ignoredUrls = ignoredUrls;
        this.deadline = deadline;
        this.url = url;
        this.visitedUrls = visitedUrls;
        this.counts = counts;
    }

    @Override
    protected void compute() {
        if (maxDepth == 0 || clock.instant().isAfter(deadline)) {
            return;
        }

        for (Pattern pattern : ignoredUrls) {
            if (pattern.matcher(url).matches()) {
                return;
            }
        }

        if (visitedUrls.contains(url)) {
            return;
        }

        visitedUrls.add(url);

        PageParser.Result result = parserFactory.get(url).parse();

        for (Map.Entry<String, Integer> e : result.getWordCounts().entrySet()) {
            if (counts.containsKey(e.getKey())) {
                counts.put(e.getKey(), e.getValue() + counts.get(e.getKey()));
            } else {
                counts.put(e.getKey(), e.getValue());
            }
        }

        List<String> subLinks = result.getLinks();

        List<CrawlInternalAction> subtasks = subLinks.stream()
                .map(link -> new CrawlInternalAction(clock,
                        parserFactory,
                        maxDepth - 1,
                        ignoredUrls,
                        deadline,
                        link,
                        visitedUrls,
                        counts)).toList();

        invokeAll(subtasks);
    }
}
