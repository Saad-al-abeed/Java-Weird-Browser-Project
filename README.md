# Weird Browser

A custom web browser project built in Java that fetches web pages and parses their HTML DOM asynchronously.

## Prerequisites

- **Java Development Kit (JDK) 26** or higher
- **Apache Maven** installed

## How to Build and Run

1. Open your terminal and navigate to the root directory of the project (where the `pom.xml` is located).

2. Compile the project and download dependencies using Maven:
   ```bash
   mvn clean compile
   ```

3. Run the application using the Maven Exec plugin:
   ```bash
   mvn exec:java -Dexec.mainClass="org.custombrowser.Main"
   ```

## Usage

Once the program starts, it will prompt you to enter a URL.
You can enter a domain like `example.com` or a full URL like `https://example.com`.
The application will asynchronously fetch the webpage, print the HTTP status code, and process the HTML.