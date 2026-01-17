package com.udacity.webcrawler;

import com.udacity.webcrawler.parser.PageParser.Result;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveAction;
import java.util.regex.Pattern;

public class CrawlInternalAction extends RecursiveAction {
    private final CrawlInternalActionFactory crawlInternalActionFactory;
    private final int maxDepth;
    private final String url;

    public CrawlInternalAction(CrawlInternalActionFactory crawlInternalActionFactory, String url, int maxDepth) {
        this.crawlInternalActionFactory = crawlInternalActionFactory;
        this.maxDepth = maxDepth;
        this.url = url;
    }

    @Override
    protected void compute() {
        Clock clock = crawlInternalActionFactory.getClock();
        Instant deadline = crawlInternalActionFactory.getDeadline();

        if (maxDepth == 0 || clock.instant().isAfter(deadline)) {
            return;
        }

        List<Pattern> ignoredUrls = crawlInternalActionFactory.getIgnoredUrls();
        for (Pattern pattern : ignoredUrls) {
            if (pattern.matcher(url).matches()) {
                return;
            }
        }

        Set<String> visitedUrls = crawlInternalActionFactory.getVisitedUrls();
        if (!visitedUrls.add(url)) {
            return;
        }

        Result result = crawlInternalActionFactory.getParserFactory().get(url).parse();
        Set<Map.Entry<String, Integer>> entries = result.getWordCounts().entrySet();

        for (Map.Entry<String, Integer> e : entries) {
            Map<String, Integer> counts = crawlInternalActionFactory.getCounts();

            counts.compute(e.getKey(), (k, v) -> (v == null) ? e.getValue() : v + e.getValue());
        }

        List<String> subLinks = result.getLinks();

        List<CrawlInternalAction> subtasks = subLinks.stream()
                .map(link -> crawlInternalActionFactory.create(link, maxDepth - 1)).toList();

        invokeAll(subtasks);
    }
}
