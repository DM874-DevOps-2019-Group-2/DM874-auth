#!/bin/bash

if [ -z "$KUBERNETES_TOKEN" ]
then
	>&2 echo "KUBERNETES_TOKEN not set"
	exit 22
fi

if [ -z "$DOCKER_SLUG" ]
then
	>&2 echo "DOCKER_SLUG not set"
	exit 22
fi

if [ -z "$TRAVIS_COMMIT" ]
then
	>&2 echo "TRAVIS_COMMIT not set"
	exit 22
fi

if [ -z "$SERVICE" ]
then
	>&2 echo "SERVICE not set"
	exit 22
fi

if [ -z "$CONTAINER" ]
	>&2 echo "CONTAINER not set"
	exit 22
fi


docker run -t dm874/deploy \
	-e KUBERNETES_TOKEN=$KUBERNETES_TOKEN \
	-e DOCKER_IMAGE_SLUG=$DOCKER_SLUG \
	-e DOCKER_IMAGE_TAG=$TRAVIS_COMMIT \
	-e SERVICE=$SERVICE \
	-e CONTAINER=$CONTAINER
