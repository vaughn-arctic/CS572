# Week 3

### Last class

Precision = # Relatvant Items / # All Items  

Recal = # Relevant Items Retrieved / All Relevant Items  
*Precision is more imporatant than recall in web applications*  

Harmonic Mean = 2PR / p + R  
Arithmatic mean = P+R / 2  
Geometric Mean = <code>sqrt(pr)</code>

If P != R, then the 3 means will always be different  
All 3 means will be somewhere in the range (P-R)  

Harmonic Mean is also called the F Score  
<code> 1/ 1/2(1/p + 1/R) = 2PR / P+R</code>

Beta (B symbol) is a parameter that conrtols relative improtance of precision and recall  
make it closer to 0 or 1.... 
Fb = (b^2 + 1)PR / R + b^2P

In calculating a problem  
Recall = #present / # total
Precision = # correct / # total  
Monotonic increasing = means only go up  
Average precisoin @ relative documents... Sum the precision when correct document is found / # relevant documents  
Average precision across queries == same thing... sum of precision @ correct document  
#### (Mean Average Precision) = sum of average precision across queires / # quiers  
 #### Negative aspects  
 - if relevant query is never found average = 0
 - each query counts equally
 - How to reward more relevant documents?

Need for human measured relevance  
Heavily skewed by ownership/collection/authorship  

#### Discounted Cumulative gain  
highly relevant docs appearing lower in the search results should be penalized   
The graded relevance value is reduced logrithmically based on position of result  
![DCG](/Users/vaughn/Desktop/Screenshot 2024-01-28 at 1.20.11 PM.png)  
rel(i) is the graded relevance at position i   
