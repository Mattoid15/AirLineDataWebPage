This is the ReadMe for the AirTravelData project, written by Matthew Lingenfelter


Links:
Air Travel Data Homepage - https://acad.kutztown.edu/~mling459/AirTravelData/
Air Travel Data JavaDoc - https://acad.kutztown.edu/~mling459/csc521/site/apidocs/index.html


Cookies:
For this phase of the project, all of the meta data about what is and is not in the Oracle Database
is done in cookies stored in the end user's browser. This was done to allow the user to keep data
stored until their session ends, that way they don't have to wait for the querys every time they
make a request. Depending on how much data there is for a particular query, the webpage may take
up to five seconds to retrieve and store that data.


Use of the Session:
Also for storing meta data, I used the session to store the order that the tables were added to the
Oracle Database. I then checked if the value of the "TotalTables" cookie was 5 to see if I needed
to drop a table, in which case I would get the order of access, stored in the session, to see which
table I should drop. This was again done as a way to keep everything for a particular user until
their session ended.


Beans:
The use of Java Beans were unfortunatly not implemented, due to not being able to find a good usage
alongside using cookies and the session for this project. More is explained in the Errors section.


Design Decisions:
Everything that happens when interacting with this project, happens on the Homepage (linked above).
There is no refreshing that occurs, even when removing the data associated with the session. I also
decided to take the best result of the users query and place it above the table containing the rest
of the data, and add some text to help clarify what is being shown.


Errors:
I wanted there to be a way to let the user know that the servlet is working, and not just leave
them wondering if the button press worked. My original goal was to have text display that the page
was updating before sending the ajax request, and then clear the text once the ajax request
completed. However, since I split the request into two, one for getting the data from the Transtats
website, and one to display the data in Oracle, the first could not be done asyncronously. And
because of this I could not figure out how to update the page to say "Getting Data..." before the
request was sent.

My original intention for inplementing JavaBeans was to have a bean that would store how many
querys a user has made. It would then display back the number of querys made, updating as the user
continues to make querys. As stated above, I was not able to get this properly implemented since I
was more focused of other aspects of the project that I thought were more important, such as not
having more than 5 tables in the Oracle Database.

One possible error that I have not encountered yet could be that one user removes the data,
removing it from the Oracle Database, and another user then attempts to read that data (which is no
longer there). There are plenty of error checking in the code so that if something like this were
to happen, it would not crash or hang, but instead just inform the end user that the data could not
be retrieved.
