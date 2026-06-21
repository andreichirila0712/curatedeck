#!/bin/bash

export AWS_ACCESS_KEY_ID=000000000000
export AWS_SECRET_ACCESS_KEY=000000000000

awslocal s3 mb s3://test-bucket
awslocal sqs create-queue --queue-name my-queue