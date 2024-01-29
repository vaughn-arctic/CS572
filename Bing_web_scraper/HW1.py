#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Jan 22 10:42:12 2024

@author: vaughn
"""

from bs4 import BeautifulSoup
import urllib.parse
from urllib.parse import unquote
import json
import time
import requests
from random import randint
from html.parser import HTMLParser
from datetime import datetime
import webbrowser

USER_AGENT =  {'User-Agent':'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36'}
USER_AGENT2 = {'User-Agent':'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36'}
QUERIES = "/Users/vaughn/Desktop/572/HW1/given/100QueriesSet1.txt"
GOOGLE_RESULTS = "/Users/vaughn/Desktop/572/HW1/given/Google_Result1.json"
chrome_path = 'open -a /Applications/Google\ Chrome.app %s'
# Alternatives
#
# While loop that goes through results and searchs for those with len == 0
#
# Batch the restuls
#     do maybe 5 at a time
#     write those results



class SearchEngine: 
    
    def __init__(self): 
        pass
    
    @staticmethod
    def search(query, sleep=True):
        if sleep:
            wait = randint(1, 4)
            print("Random Interrupt: " + str(wait)+ ("s"), end="\n")
            now = datetime.now()
            current_time = now.strftime("%H:%M:%S")
            print("Current Time =", current_time, end="\n")
            time.sleep((wait))
            
        temp_url = '+'.join(query.split())
        
        url = "http://www.bing.com/search?q=" + temp_url +"&first=1"
        print(url,end="\n")
        webbrowser.get(chrome_path).open(url)
        time.sleep(3)
        soup = BeautifulSoup(requests.get(url, headers=USER_AGENT2).text, "html.parser")
        new_results = SearchEngine.scrape_search_result(soup)
        '''
        if len(new_results) == 0:
            print("No Results...try again (page 2")
            time.sleep(5)
            url = "http://www.bing.com/search?q=" + temp_url +"&first=11"
            soup = BeautifulSoup(requests.get(url, headers=USER_AGENT).text, "html.parser")
            new_results = SearchEngine.scrape_search_result(soup)
        '''
        return new_results
    
    @staticmethod
    def scrape_search_result(soup):
        raw_results = soup.find_all("li", {"class":"b_algo"}) 
        
        if len(raw_results) < 10:
            raw_results = soup.find_all("div", {"class":"tpcn"}) 
        results = []
        
        #check here to only get 10 results and check that URLs are not duplicate
        
        for hit in raw_results:
            
            href = hit.find("a").get("href")
            
            url = href #unquote(href) href
            print(url, end="\n")
            
            if url not in results:
                results.append(url)
                
            if len(results) == 10:
                break
            
        return results
    
def retry(yahoo_results, stats, google_results):
    for query in yahoo_results: 
    
        retry_count = 0
        while len(yahoo_results[query]) == 0 and retry_count <3: 
            print(f"{query} RETURNED NO RESULTS..>RETRYING")
            time.sleep(33)
            query = query.rstrip()[:]
            yahoo_results[query]=SearchEngine.search(query)
            retry_count += 1
        
        if retry_count == 3:
            print("3 Failed Retries" )
            
                    
        matching_url = match_url(query, google_results, yahoo_results)
        rho = calculate_rho(matching_url)
        stats[query] = {"Overlap": len(matching_url), "Percent": len(matching_url) / 10, "Rho": rho}
    return stats, yahoo_results
            

def main(): 
    queries, google_results = read_files()
    values, yahoo_results = search_task(queries, google_results)
    # values, yahoo_results = retry(yahoo_results, values, google_results)
    calculate_avg(values)
    print("Writing output files...  ")
    write_files(values, yahoo_results)
    
    
def search_task(queries, google_results): 
    #!!!!!!
    #Here is where we can just include the existing yahoo results as a parameter and 
    #only search query if len(yahoo_stat[query]) == 0
    #
    # Just run multiple times and keep updating the results list
    #
    # TODO
    # import current results_json
    # set it equal to yahoo results
    # import that as paramater in search(task)
    #
    yahoo_results = {}
    stats = {}
    for i, query in enumerate(queries): 
        """
        if (i+1)%10 == 0: 
            batch_string = f" Queries_{(i+1) - i}-{i+1}"
            
            with open(f"{batch_string}.json", "w") as f:
                json.dump(yahoo_results, f, indent=4)
                """
            
        print("Searching Query " + str(i+1)+": " + query, end="")
        query = query.rstrip()[:]
        yahoo_results[query]=SearchEngine.search(query)
        
        #!!!!!!!!!
        matching_url = match_url(query, google_results, yahoo_results)
        rho = calculate_rho(matching_url)
        stats[query] = {"Overlap": len(matching_url), "Percent": len(matching_url) / 10, "Rho": rho}
        
        x = randint(0, 5)
        print(f"Query Complete... Random Sleep {x}s\n")
       # time.sleep(x)
        
        
    return stats, yahoo_results



def match_url(query, google_result, yahoo_result):
    matching_url = []
    
    for yahoo_index, yahoo_url in enumerate(yahoo_result[query]):
        yahoo_url = manipulate_url(yahoo_url)
        
        for google_index, google_url in enumerate(google_result[query]):
            google_url = manipulate_url(google_url)
            
            if yahoo_url == google_url: 
                matching_url.append((google_index, yahoo_index))
    return matching_url



def manipulate_url(url_string):    
    """
    index = url_string.find("//")
    if index > -1:
        url_string = url_string[index + 2:]
    
    if "www." in url_string:
        url_string = url_string[4:]
    
    if url_string[-1] == "/":
        url_string = url_string[:-1]
    """    
    return url_string.lower()
    
    
    

    
def calculate_avg(stats): 
    avg_overlap, avg_percent_overlap, avg_coefficient = 0, 0, 0
    for _, value in stats.items():
        avg_overlap += value["Overlap"] / 100
        avg_percent_overlap += value["Percent"] / 100
        avg_coefficient += value["Rho"] / 100
    stats["Averages"] = {"Overlap": round(avg_overlap,1), "Percent": round(avg_percent_overlap*100,2), "Rho": round(avg_coefficient,2)}
    print(stats["Averages"])
    return

def calculate_rho(matching_url): 
    # If there is no matching, Spearman coefficient is 0
    if len(matching_url) == 0:
        coefficient = 0
    # If there is exactly 1 matching
    elif len(matching_url) == 1:
        # If Google index and Yahoo index are the same, Spearman coefficient is 1, Otherwise, 0.
        if matching_url[0][0] == matching_url[0][1]:
            coefficient = 1
        else:
            coefficient = 0
    else:
        difference = sum([(a - b) ** 2 for a, b in matching_url])
        coefficient = 1 - ((6 * difference) / (len(matching_url) * (len(matching_url) ** 2 - 1)))
        
    return coefficient

    
    
    # Driver Main
    # Put sleep after every 3 quieries
    
    # Update scrape break into multiple functions
    #       only 10 results
    #
    
    # Compare Results lists from ran search  & Google search
    
    # Calculate R
    
    
    
    
    
    
    

def read_files():
    with open(QUERIES, "r") as f:
        queries = f.readlines()
    with open(GOOGLE_RESULTS, "r") as f:
        google_results = json.load(f)
        
    return queries, google_results







def write_files(values, bing):
    with open("hw1bing.json", "w") as f:
        json.dump(bing, f, indent=4)
        
    with open("hw1bing.csv", "w") as f:
        
        f.write("Query, Overlapping Results, % Overlap, Spearman Coef.\n")
        
        for query_num, values in values.items(): 
            f.write(f"Query {query_num}, {values['Overlap']}, {values['Percent']}, {values['Rho']} \n")
            
            
            
            
            
main()
        
            
            
            
            
            
            
            
            
            
            
            
            
        