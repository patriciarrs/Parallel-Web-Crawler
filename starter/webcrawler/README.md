# Parallel Web Crawler

Intro

## Index

## Getting Started

### Prerequisites

- 

### Quickstart

1. 

## Step 1. Crawler Configuration

The web crawler app reads in a JSON file to determine how it should run.

JSON Configuration Example:
```
{
  "startPages": ["http://example.com", "http://example.com/foo"],
  "ignoredUrls": ["http://example\\.com/.*"],
  "ignoredWords": ["^.{1,3}$"],
  "parallelism": 4,
  "implementationOverride": "com.udacity.webcrawler.SequentialWebCrawler",
  "maxDepth": 2,
  "timeoutSeconds": 7,
  "popularWordCount": 3,
  "profileOutputPath": "profileData.txt"
  "resultPath": "crawlResults.json"
}
```

- `startPages` - Starting point URLs.
- `ignoredUrls` - Which URLs should not be followed.
  - Here: the second starting page will be ignored.
- `ignoredWords` - Which words should not be counted toward the popular word count.
  - Here: words with 3 or fewer characters are ignored.
- `parallelism` - Parallelism that should be used.
  - 1: use legacy crawler.
  - < 1: default to the number of cores on the system.
- `implementationOverride` - Override for which implementation should be used.
  - Here: the legacy crawler will always be used (regardless of the value of the "parallelism" option).
  - Empty / unset: the "parallelism" option will be used.
  - String that is not the fully-qualified name of a class that implements the WebCrawler interface: the crawler will immediately fail.
- `maxDepth` - Max number of links the crawler is allowed to follow from the starting pages before it must stop.
  - Here: The crawler would only visit pages A, B, C, and D.

![Page Traversal](../../PageTraversal.png "Page Traversal")

- `timeoutSeconds` - Max amount of time the crawler is allowed to run, in seconds.
  - Once this amount of time has been reached, the crawler will finish processing any HTML it has already downloaded, but it is not allowed to download any more HTML or follow any more hyperlinks.
- `popularWordCount` - Number of popular words to record in the output.
  - Here: the 3 most frequent words will be recorded.
  - Tie: longer words take preference.
  - Tie: words that come first alphabetically take preference.
- `profileOutputPath` - Path to the output file where performance data should be written.
  - File already exists at that path: the new data should be appended.
  - Empty / unset: the profile data should be printed to standard output.
- `resultPath` - Path where the result JSON should be written.
  - File already exists at that path: it should be overwritten.
  - Empty / unset: the result should be printed to standard output.

## Application Architecture


## Mandatory Project Requirements


## Implemented Optional Project Requirements

