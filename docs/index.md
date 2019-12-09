Welcome to the Parabond Pricing application, powered by Google Kubernetes Engine! This application demonstrates the power of Kubernetes to easily scale up an application to meet greater performance demands. The main function runs a pricing algorithm on a batch of portfolios. This requires querying MongoDB for the porfolio instrument information, querying the database for the bond instruments that make up each portfolio, and running the computations to price them. With Kubernetes, we can scale the number of worker nodes to increase the application performance according to the workload. 

We deploy the Parabond cluster to Google Kubernetes Engine running in Google Cloud. We create a user REST server as the user interface, running in the IBM cloud. The deployment process is made simple by containerizing our application nodes with Docker. Each Docker container encapsulates the necessary dependencies for each module in our application. By creating these distributed components combined with the Docker runtime running underneath the Kubernetes platform, scaling up or down node instances is as easy as a single command. 

Check out the [API specification here](https://jonathansavas.github.io/marist-mscs621-2019-savas/apidoc.html "Swagger API Doc")

And check out a more in-depth description of the application technologies by clicking 'View on GitHub' above!
