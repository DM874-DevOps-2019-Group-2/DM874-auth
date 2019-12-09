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


docker run -t dm874/deploy \
	--env KUBERNETES_TOKEN \
	--env DOCKER_IMAGE_SLUG=dm874/auth\
	--env DOCKER_IMAGE_TAG=$TRAVIS_COMMIT \
	--env SERVICE=auth \
	--env CONTAINER=auth-container
