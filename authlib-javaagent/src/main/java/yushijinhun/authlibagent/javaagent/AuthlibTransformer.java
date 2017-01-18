package yushijinhun.authlibagent.javaagent;

public class AuthlibTransformer extends Transformer {

	protected String apiYggdrasilAuthenticate;
	protected String apiYggdrasilRefresh;
	protected String apiYggdrasilValidate;
	protected String apiYgggdrasilInvalidate;
	protected String apiYggdarsilSignout;
	protected String apiFillGameProfile;
	protected String apiJoinServer;
	protected String apiHasJoinServer;
	protected String apiProfilesLookup;
	protected String[] skinWhitelist;
	protected byte[] yggdrasilPublickey;
	protected String apiUsername2Profile;

	public AuthlibTransformer(String apiYggdrasilAuthenticate, String apiYggdrasilRefresh, String apiYggdrasilValidate, String apiYgggdrasilInvalidate, String apiYggdarsilSignout, String apiFillGameProfile, String apiJoinServer, String apiHasJoinServer, String apiProfilesLookup, String[] skinWhitelist, byte[] yggdrasilPublickey, String apiUsername2Profile) {
		this.apiYggdrasilAuthenticate = apiYggdrasilAuthenticate;
		this.apiYggdrasilRefresh = apiYggdrasilRefresh;
		this.apiYggdrasilValidate = apiYggdrasilValidate;
		this.apiYgggdrasilInvalidate = apiYgggdrasilInvalidate;
		this.apiYggdarsilSignout = apiYggdarsilSignout;
		this.apiFillGameProfile = apiFillGameProfile;
		this.apiJoinServer = apiJoinServer;
		this.apiHasJoinServer = apiHasJoinServer;
		this.apiProfilesLookup = apiProfilesLookup;
		this.skinWhitelist = skinWhitelist;
		this.yggdrasilPublickey = yggdrasilPublickey;
		this.apiUsername2Profile=apiUsername2Profile;

		setup();
	}

	protected void setup() {
		// authlib
		addTransformUnit("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService", new YggdrasilPublicKeyTransformUnit("yggdrasil_publickey_transformer", yggdrasilPublickey));
		addTransformUnit("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService", new LdcTransformUnit("fill_game_profile_transformer", "fillGameProfile", "https://sessionserver.mojang.com/session/minecraft/profile/", apiFillGameProfile));
		addTransformUnit("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService", new LdcTransformUnit("fill_profile_properties_transformer", "fillProfileProperties", "https://sessionserver.mojang.com/session/minecraft/profile/", apiFillGameProfile));
		addTransformUnit("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService", new LdcTransformUnit("join_server_transformer", "<clinit>", "https://sessionserver.mojang.com/session/minecraft/join", apiJoinServer));
		addTransformUnit("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService", new LdcTransformUnit("has_joined_server_transformer", "<clinit>", "https://sessionserver.mojang.com/session/minecraft/hasJoined", apiHasJoinServer));
		addTransformUnit("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService", new SkinWhitelistDomainTransformUnit("skin_whitelist_domain_transformer", skinWhitelist));
		addTransformUnit("com.mojang.authlib.yggdrasil.YggdrasilGameProfileRepository", new LdcTransformUnit("profiles_lookup_transformer", "findProfilesByNames", "https://api.mojang.com/profiles/", apiProfilesLookup));
		addTransformUnit("com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication", new LdcTransformUnit("yggdrasil_authenticate_transformer", "<clinit>", "https://authserver.mojang.com/authenticate", apiYggdrasilAuthenticate));
		addTransformUnit("com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication", new LdcTransformUnit("yggdrasil_refresh_transformer", "<clinit>", "https://authserver.mojang.com/refresh", apiYggdrasilRefresh));
		addTransformUnit("com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication", new LdcTransformUnit("yggdrasil_validate_transformer", "<clinit>", "https://authserver.mojang.com/validate", apiYggdrasilValidate));
		addTransformUnit("com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication", new LdcTransformUnit("yggdrasil_invalidate_transformer", "<clinit>", "https://authserver.mojang.com/invalidate", apiYgggdrasilInvalidate));
		addTransformUnit("com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication", new LdcTransformUnit("yggdrasil_signout_transformer", "<clinit>", "https://authserver.mojang.com/signout", apiYggdarsilSignout));

		// bungeecord
		addTransformUnit("net.md_5.bungee.connection.InitialHandler", new LdcTransformUnit("bungeecord_has_joined_server_transformer", "handle", "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=", apiHasJoinServer + "?username="));
	}
}
