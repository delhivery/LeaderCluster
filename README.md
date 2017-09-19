Java Leader Cluster v1.1
===================

## Introduction  
Leader Cluster is a simple clustering algorithm and is as described in the
book "Clustering Algorithms" by John A. Hartigan (pg. 74, §3.2), published
by Wiley. This project is inspired by a similar implementation in R's [Leader
Cluster](https://cran.r-project.org/web/packages/leaderCluster/index.html) package.

In Java Leader Cluster, we have modified the original leader cluster to create
clusters of fixed size in a single pass over the data points.

## What's New  
- Allows clustering based on road distances, either using 
[Google Distance API](https://developers.google.com/maps/documentation/distance-matrix/intro)
or 
[OSRM HTTP API](https://github.com/Project-OSRM/osrm-backend/blob/2ed6b181c8e80b7d17991ffe29726ecb130785f6/docs/http.md)
- Just specify the URL, basic Auth for OSRM (optional) and/or Google Key in config/CONFIG.ini

## Input  
It requires two basic inputs:-
- Data points comprising of their coordinates and weights
- Radius of the cluster in meters

Optionally you can also provide your own distance calculator, by default,
it uses Haversine distance calculator.

## Algorithm  
The steps of the algorithm are:-
- It first sorts the data points in decreasing order of their weights.
- The first data point forms its own cluster
- For each of the remaining points, it first checks if they can be inserted
into any of the existing clusters
- This involves checking if the point's distance from the centroid of the
cluster is less than the cluster radius and
- The resulting weighted coordinates of the cluster on inserting this point,
do not result in exceeding the cluster radius for the existing members of the
cluster
- Before each iteration, all the existing clusters are sorted in decreasing
order of their weights to ensure that we create clusters of greater weights

## How to build  
Requirements:-
- Java 8 (Ubuntu 16.04): `sudo apt-get install openjdk-8-jdk`
- Apache Maven: `sudo apt-get install maven`
- Set the environment variable `JAVA_HOME`

After, that, clone the project to a folder and to build the project, use commands:

    cd LeaderCluster
    mvn clean package

## How to use  
There are three ways to use it:-
 - You can directly use it as a tool for clustering spatial points by using
 the spatialClustering package
 - You can implement the interfaces given in algorithm package and integrate
 Leader Cluster Algorithm into your project
 - You can use it as a standalone runnable jar to cluster points given in a input csv file.

Using runnable jar  
 After mvn install, a runnable jar is created in the target folder. You can use it as shown below:
 
    java -jar target/JavaLeaderCluster-1.1.jar /path/to/input.csv <radius-of-cluster-in-meters>

## Advanced Usage:
- You can specify the distance calculator - either one of haversine, osrm or google as:

``` 
java -jar target/JavaLeaderCluster-1.1.jar /path/to/input.csv <radius-of-cluster-in-meters> <distance-calculator-name>
```

## Usage  
For a sample use case, please look at [LeaderClusterTest.java](https://github.com/delhivery/LeaderCluster/blob/master/src/test/java/com/delhivery/clustering/spatialClustering/LeaderClusterTest.java)

## Using with another java project  
Add this project as a submodule or place the jar file in the libs folder, and then include this in your project's pom.xml
```
<plugin>
     <groupId>org.apache.maven.plugins</groupId>
     <artifactId>maven-install-plugin</artifactId>
     <version>2.5.2</version>
     <executions>
         <execution>
             <id>install-external</id>
             <phase>clean</phase>
             <configuration>
                 <file>${basedir}/path/to/JavaLeaderCluster-1.1.jar</file>
                 <repositoryLayout>default</repositoryLayout>
                 <groupId>com.delhivery</groupId>
                 <artifactId>JavaLeaderCluster</artifactId>
                 <version>1.1</version>
                 <packaging>jar</packaging>
                 <generatePom>true</generatePom>
             </configuration>
             <goals>
                 <goal>install-file</goal>
             </goals>
         </execution>
     </executions>
</plugin>
```
