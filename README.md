Multi tiered application in Scala with sbt-native-packager, Docker & Kubernetes
===============================================================================
The example application is composed of a frontend module which is a Play application created from 
`play-scala` activator template, and a backend module (acting as a micro-service decoy) created
from `minimal-akka-scala-seed` template, running an Akka HTTP webserver on port 9500.

The backend provides a single "service": a `GET` request to `/double?num=x` where x is an integer 
will return a `text/plain` response with x number multiplied by 2. Frontend module provides the same
route, but delegates the actual calculation to the backend module. PRs to change it into anything more
sensible are welcome!

Building & running locally
--------------------------

Run `sbt run` in `backend` directory. Enter `http://localhost:9500/double?num=1` in your browser. 
You should see `2` as a response.

Then run `sbt run` in `frontend` directory. Enter `http://localhost:9000/double?num=1` in your 
browser. You sould see an error message. This is because frontend module tries to access host 
named `backend` and obviously cannot find it. You can get around that by adding that host name 
to your local `/etc/hosts` (or equivalend) file as an alias for `localhost`. When you do that, 
you should see `2` response from frontend module as well. 

When you stop backend module (by hitting Ctrl-C in `sbt` console), reloading 
`http://localhost:9000/double?num=1` should faild with "Connection refused" error.

Remember to remove `backend` alias from `/etc/hosts` (or equivalent) after the experiment!

By the way, a PR to make this a multi-module sbt project would be great!

Building & running using Docker
-------------------------------

Run `docker:publishLocal` in both `backend` and `frontend` directories. After that, run 
`docker images` to verify that `eu.gcr.io/gke-evaluation/backend:latest` and 
`eu.gcr.io/gke-evaluation/frontend:latest` are available in your local docker registry.

Now run `docker run --rm --name backend eu.gcr.io/gke-evaluation/backend` to start the backend
module, and then in a separate shell window run 
`docker run --rm --link backend:backed -p 9000:9000 eu.gcr.io/gke-evaluation/frontend` to run 
frontend module.

Now, verify that `http://localhost:9000/double?num=1` also responds with `2` as expected.

You can use those docker images to run this "application" using Docker Compose or even Docker 
Swarm  (but make sure to use cross-host networking added to Experimendal Docker distribution 
recently)

Note that you'll need to modify the docker image names to push those images to a Docker registry. 
The current name are valid only for my particular GCE project, and will work for you only locally.
You can modify the names by editing `packageName in Docker` setting in `build.sbt` in both 
modules. After you modify the names to reflect the particular remote registry that you wish to 
use, you sholud be able to use `docker:publish` command in `sbt` to compile, package and push your
image in one go.

Running using Kubernetes
------------------------

See http://kubernetes.io/gettingstarted/ for different ways of setting up a Kubernetes cluster for
yourself. I tried a few, and [GKE](https://cloud.google.com/container-engine/) is the easiest and 
most robust of all, but it requires a Google Cloud Engine with billing enabled, which means the 
experiment will cost you a dollar or two (but remember to clean up created resources afterwards!)

As a preparation step you need to push the docker images to a registry that your k8s worker nodes 
can access. In case of GKE, or GCE, [GCR](https://cloud.google.com/container-registry/) is 
definetely the easies, with Vagrant and other IAAS providers, Docker hub or quay.io will do. You 
can of course run your own registry too, but that's a bit of work. Irregardles of particular 
registry you use, you need to edit `backend.yml` and `frontend.yml` and change `image` parameter
to match your actual image names.

After you install `kubectl` locally and ensure you can access your cluster (run 
`kubectl get nodes` you have worker nodes ready to accept tasks) you can deploy the application by 
runing `kubectl create -f backend.yml` followed by `kubectl create -f frontend.yml`. This will 
create a replication controller for backend module with 1 initial replica, backend service (which 
is private to the cluster), replication controller for the frontend module and a frontend service
connected to a public load balancer (if your cloud provider supports that). In the latter case
you will be propmted to create a firewall rule to allow ingress HTTP traffic to reach your k8s
worker nodes. `kube-proxy` will ensure that requests to `fronted` public IP will be routed to
appropriate pod (or pods if multiple frontend replicas are launched). You can adjust number of
replicas of the modules by running `kubectl scale rc/frontend --replicas=3` etc.

You can discover the public IP of your frontend service by running `kubectl get services`. You 
will notice two IPs for this particular service, one in `10.0.0.0/24` internal network of the 
cluster and another in a publicly routable block. Now, entering 
`http://<forontend public IP>/double?num=1` in the browser should yield the familiar `2` response (note 
that port 80 is used this time) if it doesn't double check your firewall rules.

Further ideas
-------------
- Add information about the host name particular backend and frontend instances to see load balancing in action.
- Add Cassandra or Redis database to the application. Kubernetes has documentation & deployment manifests for them.

Conclusion
----------

You are are invited to discuss this example in this [thread](https://groups.google.com/forum/?fromgroups#!topic/scalania/-eIiYvEKH1A)
on Warsaw Scala Enthusiasts group (English & Polish). I'll be on vacation from July 25 to August 10. 
I hope to find some PRs to this project when I'm back ;)
