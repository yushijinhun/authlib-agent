#!/bin/sh

# Environment variables:
# AGENT_CONF - javaagent config location
# BACKEND_CONF - backend config location

# javaagent
echo '# auto-generated javaagent config' > $AGENT_CONF
echo '# '`date` >> $AGENT_CONF
echo 'transform.yggdrasil.authenticate='$AGENT_API_ROOT'authenticate' >> $AGENT_CONF
echo 'transform.yggdrasil.refresh='$AGENT_API_ROOT'refresh' >> $AGENT_CONF
echo 'transform.yggdrasil.validate='$AGENT_API_ROOT'validate' >> $AGENT_CONF
echo 'transform.yggdrasil.invalidate='$AGENT_API_ROOT'invalidate' >> $AGENT_CONF
echo 'transform.yggdrasil.signout='$AGENT_API_ROOT'signout' >> $AGENT_CONF
echo 'transform.session.fillgameprofile='$AGENT_API_ROOT'profiles/minecraft/' >> $AGENT_CONF
echo 'transform.session.joinserver='$AGENT_API_ROOT'joinserver' >> $AGENT_CONF
echo 'transform.session.hasjoinserver='$AGENT_API_ROOT'hasjoinserver' >> $AGENT_CONF
echo 'transform.api.profiles='$AGENT_API_ROOT'profilerepo/' >> $AGENT_CONF
echo 'transform.skin.whitelistdomains='$AGENT_SKIN_DOMAINS >> $AGENT_CONF
echo 'debug='$AGENT_DEBUG >> $AGENT_CONF

# backend
echo '# auto-generated backend config' > $BACKEND_CONF
echo '# '`date` >> $BACKEND_CONF
echo 'feature.allowSelectingProfile='$BACKEND_ALLOW_SELECTING_PROFILES >> $BACKEND_CONF
echo 'feature.includeProfilesInRefresh='$BACKEND_INCLUDE_PROFILES_IN_REFRESH >> $BACKEND_CONF
echo 'access.policy.default='$BACKEND_DEFAULT_ACCESS_POLICY >> $BACKEND_CONF
echo 'expire.token.time='$BACKEND_TOKEN_EXPIRE_TIME >> $BACKEND_CONF
echo 'expire.serverid.time='$BACKEND_SERVERID_EXPIRE_TIME >> $BACKEND_CONF
echo 'expire.token.scantime='$BACKEND_TOKEN_EXPIRE_SCAN >> $BACKEND_CONF
echo 'expire.serverid.scantime='$BACKEND_SERVERID_EXPIRE_SCAN >> $BACKEND_CONF
echo 'security.showErrorCause='$BACKEND_SHOW_ERROR_CAUSE >> $BACKEND_CONF
echo 'security.showStacktrace='$BACKEND_SHOW_STACKTRACE >> $BACKEND_CONF
echo 'security.allowDownloadPrivateKey='$BACKEND_ALLOW_DOWNLOAD_KEY >> $BACKEND_CONF
echo 'security.allowUploadPrivateKey='$BACKEND_ALLOW_UPLOAD_KEY >> $BACKEND_CONF
