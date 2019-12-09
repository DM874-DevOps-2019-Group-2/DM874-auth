#!/bin/bash

if [ -z "$KUBERNETES_TOKEN" ]
then
	>&2 echo "KUBERNETES_TOKEN not set"
	exit 22
fi

if [ -z "$TRAVIS_COMMIT" ]
then
	>&2 echo "TRAVIS_COMMIT not set"
	exit 22
fi


docker run \ 
	-e KUBERNETES_TOKEN \
	-e DOCKER_IMAGE_SLUG=dm874/auth-migrations \
	-e DOCKER_IMAGE_TAG=$TRAVIS_COMMIT \
	-e SERVICE=auth \
	-e CONTAINER=migrations-container \
	-t dm874/deploy \
