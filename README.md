Introduction -
Android Application to display twitter posts with specified hashtag.
Tweets are searched based on the input hashtag.
Once fetched, tweets are updated real time automatically after a certain interval of time, which can be changed from the application code.
Currently it is set at 4 seconds.
Calls are not made to any server except Twitter REST API calls.
Works fine in any device orientation.

Uses Twitter Outh1 for user login to make API calls.

Shows a basic list of tweets, which are fetched 20 at a time (lazy loading).The amount 20 can be changed from the application code.

Limitations - 

Limitation on requests to Search REST API imposed by Twitter according to:
  At this time, users represented by access tokens can make 180 requests/queries per 15 minutes. Using application-only auth, an application   can make 450 queries/requests per 15 minutes on its own behalf without a user context.
  
Limitation imposed by Twitter on the duration that can be fetched using the API,i.e tweets older than 7 days cannot be fetched using Twitter REST API.
