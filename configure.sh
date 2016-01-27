#!/bin/sh

## javaagent相关设置

# javaagent的api root
# 通过该api root应可以访问到部署了yggdrasil-backend的服务器的/yggdrasil目录
# 应以/结尾
export AGENT_API_ROOT='http://localhost:8080/yggdrasil/'

# 要加入到authlib的白名单的域名的结尾
# authlib只会从符合白名单的域名下载皮肤
# 多个域名间使用|分隔
# 例如: '.example.com', '.skinserver1.com|.skinserver2.com'
export AGENT_SKIN_DOMAINS='localhost'

# 是否将修改后的authlib类保存下来以便调试
# 开启后会在当前目录下生成名称为 <类名>_modified.class 的文件
export AGENT_DEBUG=true


## backend相关设置

# 是否允许选择游戏角色
# 这是一个mojang未实现的功能
export BACKEND_ALLOW_SELECTING_PROFILES=true

# 是否在refresh请求中包含角色列表
# mojang的验证服务不会这么做
export BACKEND_INCLUDE_PROFILES_IN_REFRESH=true

# 如果用户只有一个角色, 是否自动帮用户选择该角色
export BACKEND_AUTO_SELECTED_UNIQUE_PROFILE=true

# 是否在每次登录前清除选择的角色
export BACKEND_CLEAR_SELECTED_PROFILE_IN_LOGIN=true

# 对于hasJoinedServer请求的默认访问策略
# ALLOW=允许, DENY=阻止
export BACKEND_DEFAULT_ACCESS_POLICY=ALLOW

# accessToken过期的时间(ms)
# 默认为259200000(3天)
export BACKEND_TOKEN_EXPIRE_TIME=259200000

# 每次检查accessToken是否过期的间隔时间(ms)
# 默认为86400000(1天)
export BACKEND_TOKEN_EXPIRE_SCAN=86400000

# serverId过期的时间(ms)
# 默认为60000(1分钟)
export BACKEND_SERVERID_EXPIRE_TIME=60000

# 每次检查serverId是否过期的间隔时间(ms)
# 默认为60000(1分钟)
export BACKEND_SERVERID_EXPIRE_SCAN=60000

# 是否将错误原因发送给客户端(yggdrasil服务)
# 所谓原因即为java错误链的cause
# 由于安全原因, 推荐在生产中设为false
export BACKEND_SHOW_ERROR_CAUSE=true

# 是否将错误的stack trace发送给客户端(管理服务)
export BACKEND_SHOW_STACKTRACE=false

# 是否允许通过管理服务下载用于签名的密钥
export BACKEND_ALLOW_DOWNLOAD_KEY=true

# 是否允许通过管理服务上传签名密钥
export BACKEND_ALLOW_UPLOAD_KEY=true


## 其它设置

# 用于数字签名的RSA密钥的长度
export KEY_BITS=2048
