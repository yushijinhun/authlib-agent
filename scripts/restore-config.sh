#!/bin/sh

# Environment variables:
# AGENT_CONF - javaagent config location
# BACKEND_CONF - backend config location

cp $AGENT_CONF.bak $AGENT_CONF
cp $BACKEND_CONF.bak $BACKEND_CONF

rm $AGENT_CONF.bak
rm $BACKEND_CONF.bak
