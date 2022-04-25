# David-O-Meter

## What is this?
         This is a program/webpage that displays the ratio of positive to neutral to negative tweets that mention the word "David".  
         The David-O-Meter measures the overall sentiment towards Davids around the world  :)

## How does it work?
         The java program first sends a request to the Twitter API to return **recent tweets that mention the word "David"** from twitter users not named David.  
         Then the returned **JSON data is parsed** and the tweets are formatted into a format recognizable by Azure Cognitive Services. The new JSON containing the tweets' text content is sent to the Azure Cognitive Services API for **sentiment analysis**.  
         The returned data is then parsed again, and any sentiment with a **.5+** confidence score is added to its' aggregate count. For example, a tweet with positive of .6, negative of .15 and negative of .25 would increase the positive sentiment count by 1  
         Twitter limits me to **300 API request per 15 minutes** so this process is run every 3 seconds to have it repeat no more than 300 times in 15 minutes. Every 15 minutes, **the `sentimentValues.json` file**—which contains the total values of each sentiment—is updated, commited, and pushed to the repo  
         When a client visits the webpage, the initial HTML/CSS will have each sentiment in 1 of 3 bars in a flex container, all with equal lengths and content. The javascript will then parse the values from `sentimentValues.json` and **update each sentiment's flex-grow property** (to create the ratio in length) and its content/innerHTML to display the percentage for that sentiment.

## Why did I do this?
         I wanted to learn about APIs, how to use them and how to use the data extracted from them in client-facing software/sites. So, instead of buying an expensive course to slowly learn it, I decided to to think of a simple non-controversial project to work on and learn accross the way with the help of documentation (Twitter API documentation sucks though) and Google (I actually use Bing).

## Am I finished?
Short answer: **No**  
Long answer: While the project and site are functional, here's some features and stuff I wish to add soon:
- Ability to see sentiment values for the last hour and last 24 hours
- Ability to see the latest positive, neutral and negative tweets analyzed
- Ability to download program software as jar and deploy it to analyze and display sentiment for whatever key word/phrase you choose
