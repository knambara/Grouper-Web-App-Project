# BUGS TO FIX / NECESSARY THINGS TO HAVE
**Please add and remove to this list as problems are resolved/arise**

1. resolve name differences in database/variable names
2. make email list dynamic when someone leaves
3. make deleting groups dynamic
4. TESTING
5. README
6. invisibility functionality
7. extend time functionality
8. classes need to disappear from list dynamically

# BELLS AND WHISTLES

1. location services and location sorting
2. text search
3. profile images?



# Strengths/Weaknesses
Katie Lynch<br />
Classes taken: 15/16, 22, 1450, 53, 1410, 1430, 1800<br />
Strengths: algorithms, data structures, concise code
Weaknesses: web development, animation, user interaction

Jeff Demanche<br />
Classes taken: 17/18, 132, 123, 128, 173, 1950y<br />
Strengths: Web development, graphics, design.
Weaknesses: Algorithms, efficiency

Jenna Soenksen<br />
Classes taken: CS15/16, 22, 1300, 1430 (current)<br />
Strengths: UI/UX (including prototyping), CSS/HTML, commenting on code, JUnit testing
Weaknesses: maximizing efficiency, github

Kento Nambara<br />
Classes Taken: Previous Uni - Intro Seq. (15/16 equivalent), Probability & Statistics, Data Analysis;<br />
		 Brown - CS 33, 1805, 22 (Current)<br />
Strengths: Algorithms, Profiling, Data Structures, Commenting, Debugging
Weaknesses: Animation, Front-end

# Project Ideas
## Sign me up but better.
Problems to Solve:<br />
 - Without TA hours, many students find themselves struggling with studying or working on assignments by themselves. Collaborative study/work environments are mainly held during classes, recitations, and TA hours, so students who are only be able to work outside of these specified times struggle with their assignments.
 - Students who do not have study groups must rely on TA / office hours.
 - For CS classes, slots for SignMeUp often get filled up instantly.
 - There is not an efficient collaboration system for non-CS classes.

How This Will Solve These Problems<br />
 - This app would allow students to be able to check into certain locations (Sci-li, Rock, CIT, etc.) and see if any other students in that location is studying for the same class.
 - Students can either request other students for assistance, or notify others that there is a study session going on.
 - Students can remain anonymous when sending and receiving requests, until the requests are accepted.

Features
 - Implement Brown Sign-in API to make signing in simple
   - Why: To ensure only Brown students can sign in and to simplify the sign in process.
   - Challenge: Implementing a secure API.
 - Upon sign in, select the class you want to work on, then either create group or join a group.
 - Request access to identifying group location and photos/descriptions of group members.
   - Why: These features ensure that new members to the group can be “vetted” and that afterwards they will know where the study group is located.
   - Challenge: Realtime updates for group status, deciding when to terminate a group
 - Check-in to a location
   - Why: Notify others of the current places to study.
   - Challenge: Creating a comprehensive list of study locations/ allowing for user input to specify an unusual study location

## A collaborative music looping website.
Problems to Solve:

 - Music is collaborative in nature, but it can take a lot of coordinating and setup to make music with other people.
 - Looping over beats is often done by only one person, but it can be fun to get input from other people.

How This Will Solve These Problems
 - This app would allow musicians to contribute to a spontaneous composition that relies on collaboration from other users.
 - Every user would be able to either start a new loop, or contribute to an existing one, which would be randomly assigned. By limiting them to only contributing one track, the final loop would rely on many different people.

Features
 - Audio Input
   - Why: To dub over the loop with the new user’s input
   - Challenge: Implementing an audio API.  
 - Users can contribute to an existing track assigned randomly, or pass if they don’t like it.
   - This gives users the freedom to contribute on tracks they’re interested in while still constraining them to only contributing a single piece.
   - Challenge: Synchronicity (only one user working on a track at a time using queues).
 - A music track interface with controls for tempo, meter, etc.
   - The first contributor on a loop sets these values so that all the loops can be synchronized with the beat.
   - Challenge: Outputting audio (like a metronome) through JavaScript.
 - Possibly the option for creating a private loops that you can share with certain people
   - Why: to collaborate within a smaller, personal group of people/friends
   - Challenge: Keeping track of distinct groups


## “Find It” - A multi-document keyword search program.
Problems to Solve:

 - Research papers -- Students and researchers spend a lot of time ensuring they are citing the best available information. To help with this process, “Find It” will allow users to upload multiple sources (documents) and then input any number of keywords to search for across all documents. The software would then return a ranked list of the search hits in the documents. “Find It” will alleviate the need to painstakingly cross-reference different sources to find the best information. For this application, the ranking algorithm could be tied to either the number of keywords found in a given section or the strength of the source.
 - Earnings Release analysis -- Investors’ and finance professionals’ success depends on their ability to quickly react to changes in the marketplace. Earnings releases and their transcripts are one of the largest factors in volatility at the micro level. By allowing a multiple keyword search across multiple documents, finance professionals can easily understand how a company’s performance and the CEO’s comments on their performance is correlated to stock performance. For this application, the ranking algorithm would be based on the correlation between the keywords and the stock price movement.
 - FOMC Minutes analysis -- When the fed releases its expectations for interest rates in the form of meeting minutes, investors attempt to read the document and understand if the fed has changed its course. US Treasurys are the largest financial market in the world - each day around 500bn of UST are traded. Similar to the Earnings Release problem, “Find It” will allow users to quickly understand what the Fed has indicated for rates and can trade more effectively quicker.

Features:

 - Automatically format and cite source quotes
   - Why: It is often difficult to remember how to properly format citations in papers/documents, especially when there are multiple styles to remember (MLA, APA, Chicago, etc.). Your time is much better spent focusing on the content of your writing than wasting time styling citations.
   - Challenge: Collecting the right data for each of the different citation types and handling cases where the data set is incomplete.
 - Ranking:
   - Why: To help users sort through the keyword hits.
   - Challenge:
     - Papers: Efficient way to rank sources credibility or relevance.
     - Finance: Gathering the stock price data for earnings release dates.
 - Parse Documents:
   - Why: To find where the keywords are located in the documents.
   - Challenge: Implement an API that can parse PDFs.
