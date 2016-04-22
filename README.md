[![Build Status](https://travis-ci.org/mmisw/orr-ont.svg?branch=master)](https://travis-ci.org/mmisw/orr-ont)
[![Coverage Status](https://coveralls.io/repos/github/mmisw/orr-ont/badge.svg?branch=master)](https://coveralls.io/github/mmisw/orr-ont?branch=master)


# What's orr-ont?

`orr-ont` is a complete new implementation of the `Ont` service.
See [wiki](https://github.com/mmisw/orr-ont/wiki).


# Deployment

Only basic requirement is to have a [Docker engine](https://www.docker.com/products/docker-engine)
on the target system. 
The user performing the deployment should have the relevant Docker privileges.
Check with your sysadmin.

The docker images comprising the ORR system are:

    docker pull mongo
    docker pull franzinc/agraph
    docker pull mmisw/orr-ont
    docker pull mmisw/httpd

> NOTE: specific tagging is still TBD.

  
### Preparations

- Designate a directory on your host machine as a base location for all data 
  (ontology files, MongoDB data, etc.), and capture that location
  in the `ORR_ONT_BASE_DIR` environment variable, e.g.:

        export ORR_ONT_BASE_DIR=/opt/orr-ont-base-directory
    
- Prepare your local `orr-ont` configuration file:
  - Make a local copy (say, with name `orront.conf`) of the template from 
    [here](https://raw.githubusercontent.com/mmisw/orr-ont/master/template.orront.conf)
    
			curl -o orront.conf https://raw.githubusercontent.com/mmisw/orr-ont/master/template.orront.conf
    
  - edit `./orront.conf` if needed.
    

### Run the containers

ORR's [`docker-run`](https://raw.githubusercontent.com/mmisw/orr-ont/master/bin/docker-run) 
bash script makes launching the ORR containers very straightforward.

	curl -o docker-run https://raw.githubusercontent.com/mmisw/orr-ont/master/bin/docker-run
	chmod +x docker-run
	./docker-run mongo agraph orr-ont httpd

That's it!

Now open http://localhost/ont in your browser.


> On a Mac, open `http://<docker-machine-ip>/ont`.
 

### Making your ORR instance visible to the world

Currently this consists of exposing port 80 (or whatever the host port defined 
under the `httpd` option in `docker-run`). 
Check with your sysadmin. 

> This part is still WiP particularly regarding HTTPS (port 443; certificates, etc.)


# `orr-ont` build 

On a clone of this repository:

	sbt test package
	docker build -t mmisw/orr-ont --no-cache .
	docker push mmisw/orr-ont
