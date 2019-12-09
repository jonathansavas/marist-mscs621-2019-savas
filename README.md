# marist-mscs621-2019-savas

Flask Server Endpoints:</br>
&nbsp;&nbsp;&nbsp;&nbsp;Required Header: 'Authorization: Basic \<Base64-encoded-password\>'</br>
&nbsp;&nbsp;&nbsp;&nbsp;/price/batch/{num_portfolios [1,100]}</br>
&nbsp;&nbsp;&nbsp;&nbsp;/query/bond/{bond_id [1,5000]}</br>
&nbsp;&nbsp;&nbsp;&nbsp;/query/portfolio/{portfolio_id [1,100000]}</br>

Application Development and Deployment Diagram: </br></br>
![alt text](https://github.com/jonathansavas/marist-mscs621-2019-savas/blob/master/app-dev-deployment.png "App Dev-Deploy")</br></br></br></br>

Kubernetes Cluster Architecture: </br></br>
![alt text](https://github.com/jonathansavas/marist-mscs621-2019-savas/blob/master/kubernetes-cluster.png "Kubernetes Cluster")</br></br></br></br>

This application demonstrates the ability to utilize Kubernetes to scale out work. The application prices a certain number of financial bond portfolios stored in MongoDB and returns timing information about the computation. The dispatcher nodes divides this partition to give work to each of the worker nodes. On each worker node, the computational work is parallelized by partitioning the node's own work to run concurrently, which is easily done in Scala. The data is stored in two collections within the MongoDB parabond database: Portfolios and Bonds. Portfolios contains 100,000 documents, each containing an ID and a list of bond IDs. Bonds contains 5,000 documents, each containing an ID, coupon, freq, tenor, and maturity. The worker nodes price their partition of the portfolios list by querying the database for the portfolio information, querying the database for the bond information that makes up the portfolio, and runs the pricing algorithm to assign a price to the portfolio. The application is mostly concerned with the timing information about the process: the total time for the work to be completed in parallel (tN) and an estimate of the serial time (t1). The worker nodes report back to the dispatcher node in order to calculate this information. The number of worker nodes to scale out the work can be easily increased or decreased in Kubernetes. 

The REST server that clients will interface with is a Python Flask application. This server is meant to be deployed in a cloud provider different from the Kubernetes cluster. The Flask REST server communicates with the Kubernetes cluster via REST API as well. The cluster REST server is a Java Spring Boot REST server. Internal to the Kubernetes cluster, the REST Controller, the dispatcher node, and the worker nodes all communicate via gRPC. Due to the way load balancing is handled within Kubernetes, consistent load balancing requries a service mesh to be deployed into the cluster (https://kubernetes.io/blog/2018/11/07/grpc-load-balancing-on-kubernetes-without-tears/). The linked article suggests Linkerd, but for this project I chose to use Istio Service Mesh to facilitate balancing gRPC requests evenly (https://istio.io/docs/setup/getting-started/). In order for the dispatcher node to divide the worker evenly among worker nodes, it must know the number of active worker nodes at the time of each request. Because we can scale up or down the number of worker nodes at any given time, the dispatcher must query the Kubernetes cluster for this information, which can be done using the Java Kubernetes Client (https://github.com/kubernetes-client/java). In order to communicate with the MongoDB, the nodes use the Mongo Java Driver (https://mongodb.github.io/mongo-java-driver/). 

The Java containers in the Parabond application are easily built without configuring a local Docker runtime using the Jib Maven plugin (https://github.com/GoogleContainerTools/jib). This triggers building of Docker images during the Maven build which are automatically pushed to the configured remote repository. The images for the application are already configured to be pulled from my docker hub account for deployment into Kubernetes. Users can also pull the source code and edit the Jib configuration in the pom.xml file to use their own remote repository. Upon creating a Kubernetes cluster, the application can be deployed with a series of kubectl commands: 

$ kubectl create secret generic parabond-client --from-literal=parabond-password='password' </br>
$ kubectl apply -f mongo.yaml </br>
$ kubectl apply -f paradispatcher.yaml </br>
$ kubectl apply -f paraworker.yaml </br>
$ kubectl apply -f controller.yaml </br>
$ kubectl describe service controller (this will show the exposed IP:port to access the cluster) </br>
