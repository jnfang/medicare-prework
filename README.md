## Nava take home assignment 
JN Fang

### Set up
Ensure that you have Java 8 installed: https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html

Install the following Scala dependencies on in OSX:
 ```$xslt
brew install scala@2.12
brew install sbt
```

### Run the project
In the home directory
```$xslt
sbt run
```
Then ping localhost:9000 in any browser to trigger the file reading and POST calls
The html page will show the JSON body that was sent for each request