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
echo 'feature.autoSelectedUniqueProfile='$BACKEND_AUTO_SELECTED_UNIQUE_PROFILE >> $BACKEND_CONF
echo 'feature.clearSelectedProfileInLogin='$BACKEND_CLEAR_SELECTED_PROFILE_IN_LOGIN >> $BACKEND_CONF
echo 'feature.optionalSignature='$BACKEND_OPTIONAL_SIGNATURE >> $BACKEND_CONF
echo 'access.policy.default='$BACKEND_DEFAULT_ACCESS_POLICY >> $BACKEND_CONF
echo 'expire.token.time='$BACKEND_TOKEN_EXPIRE_TIME >> $BACKEND_CONF
echo 'expire.serverid.time='$BACKEND_SERVERID_EXPIRE_TIME >> $BACKEND_CONF
echo 'security.showErrorCause='$BACKEND_SHOW_ERROR_CAUSE >> $BACKEND_CONF
echo 'security.showStacktrace='$BACKEND_SHOW_STACKTRACE >> $BACKEND_CONF
echo 'security.allowDownloadPrivateKey='$BACKEND_ALLOW_DOWNLOAD_KEY >> $BACKEND_CONF
echo 'security.allowUploadPrivateKey='$BACKEND_ALLOW_UPLOAD_KEY >> $BACKEND_CONF
echo 'security.maxTokensPerAccounts='$BACKEND_MAX_TOKENS_PER_ACCOUNT >> $BACKEND_CONF
echo 'security.extraTokensToDelete='$BACKEND_EXTRA_TOKENS_TO_DELETE >> $BACKEND_CONF
echo 'cache.maxOnHeap='$BACKEND_CACHE_MAX_ON_HEAP >> $BACKEND_CONF
echo 'redis.host='$BACKEND_REDIS_HOST >> $BACKEND_CONF
echo 'redis.port='$BACKEND_REDIS_PORT >> $BACKEND_CONF
echo 'redis.password='$BACKEND_REDIS_PASSWORD >> $BACKEND_CONF
echo 'redis.maxIdle='$BACKEND_REDIS_MAX_IDLE >> $BACKEND_CONF
echo 'redis.maxTotal='$BACKEND_REDIS_MAX_TOTAL >> $BACKEND_CONF
echo 'redis.maxWaitMillis='$BACKEND_REDIS_MAX_WAIT_MILLIS >> $BACKEND_CONF
echo 'redis.testOnBorrow='$BACKEND_REDIS_TEST_ON_BORROW >> $BACKEND_CONF

