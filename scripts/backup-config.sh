#!/bin/sh

# Environment variables:
# AGENT_CONF - javaagent config location
# BACKEND_CONF - backend config location

cp $AGENT_CONF $AGENT_CONF.bak
cp $BACKEND_CONF $BACKEND_CONF.bak
