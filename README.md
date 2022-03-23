# Sleep Shift Solver (Java, Maven or Gradle)

Assign people to shifts and beds to produce a better schedule for volunteers who need to sleep.


## Setup

1. Clone the repository.
2. Make sure you have the Java Development Kit installed - [OpenJDK 18 Download](https://jdk.java.net/18/).
3. Make sure you have Maven installed - [Maven Download](https://maven.apache.org/download.cgi).
4. You can develop in VSCode at the root of the repo or in Eclipse/IntelliJ by importing a Maven project (`pom.xml`).

## Running
Start the application with Maven:  
`mvn exec:java`  

Export the application to a `.jar` file:  
`mvn clean install`  
The jar will be in the `target` folder. Make sure to use the `withdependencies` one.


## Configuration
There are a few places where code can be commented out or back in depending on desired behavior.
- Static shift bed population: Sometimes it is useful to reverse the order of the search.

## Usage Example
Launch the application and select the "example" folder.

## More information

Visit https://www.optaplanner.org/[www.optaplanner.org] for documentation on the constraint solving library.
