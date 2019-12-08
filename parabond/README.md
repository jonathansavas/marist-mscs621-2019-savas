# parabond-project

This is an adaptation of a project I completed for a class I took with Ron Coleman, github.com/roncoleman125. The original project uses
an Actor model as the communication method between dispatcher and worker nodes. I have adapted the nodes to communicate via gRPC and
I have restructured the project with the goal of exploring the benefits of moving this project to Kubernetes. 

The original parabond project's goal was to teach methods of building a distributed application. The application prices a certain
number (up to 100,000) of financial bond portfolios stored in MongoDB and returns timing information about the computation. On each node,
the computational work is parallelized by partitioning the node's own work to run concurrently, which is easily done in Scala. The 
data is stored in two collections within the MongoDB parabond database: Portfolios and Bonds. Portfolios contains 100,000 documents, each 
containing an ID and a list of bond IDs. Bonds contains 5,000 documents, each containing an ID, coupon, freq, tenor, and maturity. The
worker nodes price their partition of the portfolios list by querying the database for the portfolio information, querying the database
for the bond information that makes up the portfolio, and runs the pricing algorithm to assign a price to the portfolio. The application is mostly concerned with the timing information about the process: the total time for the work to be completed in parallel (tN) and an estimate of the serial time (t1). 

The Actor model for the original project is easy to understand and simple to write code on top of for the specific work of the 
application. Recruiting worker nodes to scale out the application took a bit of work, as the number of workers and their IP addresses
had to be known beforehand in order to partition the work successfully. In addition, the application code had to be manually installed
on each of the worker nodes. Understanding and deploying applications to Kubernetes is certainly more challenging, but scaling out
worker nodes in Kubernetes appears to be much simpler. Although the application code had to be edited in certain places in order to
account for the highly dynamic nature of the Kubernetes environment, the ease of adding and removing workers is a great benefit. 

My local development environment on my Windows machine consists of VSCode and a Linux VM. VSCode has great support for Docker and Kubernetes, and also offers the ability to develop remotely via ssh (https://code.visualstudio.com/docs/remote/ssh). kind (https://kind.sigs.k8s.io/) is a CNCF certified conformant Kubernetes installer (https://landscape.cncf.io/selected=kind). kind requires a Linux environment; it can be run using WSL2 for Windows but this currently requires being on the Windows Insider fast ring. With the VSCode remote development extension, I have set up an Ubuntu 18.04 VM on my Windows machine, running VSCode Insiders locally with the remote development extension to ssh into my VM. This is a viable development environment for testing Kubernetes clusters locally.
I deploy to my Google Cloud account using the Google Cloud SDK CLI. 
