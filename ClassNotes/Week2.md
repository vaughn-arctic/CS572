# Week 2
#### [Search Enginge Evaluation](https://github.com/vaughn-arctic/CS572/new/main/ClassNotes#Search-Engine-Evalutaion)

# Ranking

Finished last week showing the exponential growth of web usage/data/capabilities
Growth of data types (java, pdf, html, xml....) 
30 Trillion urls in 2012  
Zipf Law  

### Web Crawling / Web Spidering
---
Search engines have to crawl  
Links follow the links  
Hyperlinking  

1. Crawl
2. Index
3. Rank

Crawlers are recursive parameters 
> Call function on link C  
> Recursivly call F on all links C' within C..  
> ,....
> (In the html coode it is looking for ever
>         <code>href=....</code>



Programatic code that takes us to different places by fetching content

How to objectivly calculate the value of search results  
---
- Quality: how to find the best pages first  
- Efficiency: how to avoid duplication or near duplication  (*if link C' has already been returned, don't crawl it again*)
- Etiquette: behave politely by not disturbing website's performance  

How much to crawl
---
- Coverage: What percentage of the web should be covered  
- Relative Coverage: How much do competitors Have  

How often to crawL?
---
- Freshness: How much has changed?  
- How much has really changed

### Simple Crawl Operation
- initialize (begin with seed known pages [cnn.com, usc.edu, ect.])
- Loop: Fetch and parse a page
> Place page in DB  
> Extract URLs  
> Place the extracted URLs on a queue/stack  
> Fet a URL on the queue and repeat

### Complications
*In theory, theory and practice are identicial  
in practice, theory and practice are note*  

- Some pages contain spam
- Some pages contain spider traps (internal links that lead no where)
- Latency to remote servers can vary
- Robots.txt stipulations can prevent web pages from being visited
- avoiding mirrored/duplicate sites
- Session IDs can cause traps

Robots.txt  
<code> User_agent: *  
disallow: "foo.html"  
</code>

### Basic Search Strategies
- Breadth First search ( *examine all pages at level i before examining pages at level i + 1*)
- Depth First (can get stuck in down one path)

#### Fast methods for searching 1 trillion URLs  
O(n) time search listing through a list of links to check for duplicates  
Create a hash function Hash the name of the function and check whether or not its in the hash table  
Have to avoid hash collisions  

Delta encoding based upon the inital URL  
  Storing only the differences between current and next URL

Trie for URL matching  
similar to a binary search, but share the same prefix among multiple "words"  
each path from the root to a leaf corresponds to a new word  

--- 
### Normalizing URLS 
1. Convert scheme and host to lower case
2. Capitalize letters in escape sequences
3. Decode percent-encoded octoets
4. Remove the default port

# Search-Engine-Evalutaion

Precision = # Relatvant Items / # All Items  

Recal = # Relevant Items Retrieved / All Relevant Items  
*Precision is more imporatant than recall in web applications*  




