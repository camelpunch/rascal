#!/bin/bash

set -ex

boot prod
chmod -R +w target/*
cf push
