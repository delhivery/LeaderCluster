Java Leader Cluster
===================

##Introduction
Leader Cluster is a simple clustering algorithm and is as described in the
book "Clustering Algorithms" by John A. Hartigan (pg. 74, §3.2), published
by Wiley. This project is inspired by a similar implementation in R's Leader
Cluster package.

In Java Leader Cluster, we have modified the original leader cluster to create
clusters of fixed size in a single pass over the data points.

##Input
It requires two basic inputs:-
- Data points comprising of their coordinates and weights
- Radius of the cluster
Optionally you can also provide your own distance calculator, by default,
it uses Haversine distance calculator.

##Algorithm
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

##How to use
There are two ways to use it:-
 - You can directly use it as a tool for clustering spatial points by using
 the spatialClustering package
 - You can implement the interfaces given in algorithm package and integrate
 Leader Cluster Algorithm into your project