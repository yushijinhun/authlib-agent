package yushijinhun.authlibagent.javaagent;

import java.util.Objects;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class AuthlibTransformer extends Transformer {

	private String apiYggdrasilAuthenticate;
	private String apiYggdrasilRefresh;
	private String apiYggdrasilValidate;
	private String apiYgggdrasilInvalidate;
	private String apiYggdarsilSignout;
	private String apiFillGameProfile;
	private String apiJoinServer;
	private String apiHasJoinServer;
	private String apiProfilesLookup;
	private String[] skinWhitelist;
	private byte[] yggdrasilPublickey;

	public AuthlibTransformer(String apiYggdrasilAuthenticate, String apiYggdrasilRefresh, String apiYggdrasilValidate, String apiYgggdrasilInvalidate, String apiYggdarsilSignout, String apiFillGameProfile, String apiJoinServer, String apiHasJoinServer, String apiProfilesLookup, String[] skinWhitelist, byte[] yggdrasilPublickey) {
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

		setup();
	}

	private void setup() {
		abstract class NamedTransformUnit implements TransformUnit {

			final String name;

			NamedTransformUnit(String name) {
				this.name = name;
			}

			@Override
			public String toString() {
				return name;
			};
		}
		class LdcTransformUnit extends NamedTransformUnit {

			abstract class LdcClassVisitor extends ClassVisitor {

				LdcClassVisitor() {
					super(Opcodes.ASM4);
				}

				LdcClassVisitor(ClassVisitor cv) {
					super(Opcodes.ASM4, cv);
				}

				@Override
				public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
					MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
					if (method.equals(name)) {
						return new MethodVisitor(Opcodes.ASM4, mv) {

							@Override
							public void visitLdcInsn(Object cst) {
								if (Objects.equals(ldc, cst)) {
									checked(mv);
								} else {
									super.visitLdcInsn(cst);
								}
							}
						};
					} else {
						return mv;
					}
				}

				abstract void checked(MethodVisitor mv);

			}

			Object method;
			Object ldc;
			Object replacement;

			LdcTransformUnit(String name, Object method, Object ldc, Object replacement) {
				super(name);
				this.method = method;
				this.ldc = ldc;
				this.replacement = replacement;
			}

			@Override
			public ClassVisitor check(final TransformCheckCallback callback) {
				if (replacement != null) {
					return new LdcClassVisitor() {

						@Override
						void checked(MethodVisitor mv) {
							callback.allowTransform();
						}
					};
				}
				return null;
			}

			@Override
			public ClassVisitor transform(ClassWriter writer) {
				return new LdcClassVisitor(writer) {

					@Override
					void checked(MethodVisitor mv) {
						mv.visitLdcInsn(replacement);
					}
				};
			}

		}

		// authlib
		addTransformUnit("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService", new NamedTransformUnit("yggdrasil_publickey_transformer") {

			abstract class AbstractPublickeyTransformer extends ClassVisitor {

				AbstractPublickeyTransformer() {
					super(Opcodes.ASM4);
				}

				AbstractPublickeyTransformer(ClassVisitor cv) {
					super(Opcodes.ASM4, cv);
				}

				@Override
				public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
					MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
					if ("<init>".equals(name)) {
						return new MethodVisitor(Opcodes.ASM4, mv) {

							int status = 0;

							@Override
							public void visitLdcInsn(Object cst) {
								if (status == 0 && cst instanceof Type && ((Type) cst).getInternalName().equals("com/mojang/authlib/yggdrasil/YggdrasilMinecraftSessionService")) {
									status++;
								} else if (status == 1 && "/yggdrasil_session_pubkey.der".equals(cst)) {
									status++;
								} else {
									super.visitLdcInsn(cst);
								}
							}

							@Override
							public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
								if (status == 2 && opcode == Opcodes.INVOKEVIRTUAL && "java/lang/Class".equals(owner) && "getResourceAsStream".equals(name) && "(Ljava/lang/String;)Ljava/io/InputStream;".equals(desc)) {
									status++;
								} else if (status == 3 && opcode == Opcodes.INVOKESTATIC && "org/apache/commons/io/IOUtils".equals(owner) && "toByteArray".equals(name) && "(Ljava/io/InputStream;)[B".equals(desc)) {
									status++;
									if (status == 4) {
										checked(this);
									}
								} else {
									super.visitMethodInsn(opcode, owner, name, desc, itf);
								}
							}

						};
					}
					return mv;
				}

				abstract void checked(MethodVisitor mv);

			}

			@Override
			public ClassVisitor check(final TransformCheckCallback callback) {
				if (yggdrasilPublickey != null) {
					return new AbstractPublickeyTransformer() {

						@Override
						void checked(MethodVisitor mv) {
							callback.allowTransform();
						}
					};
				}
				return null;
			}

			@Override
			public ClassVisitor transform(ClassWriter writer) {
				return new AbstractPublickeyTransformer(writer) {

					@Override
					void checked(MethodVisitor mv) {
						mv.visitIntInsn(Opcodes.SIPUSH, yggdrasilPublickey.length);
						mv.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_BYTE);
						for (int i = 0; i < yggdrasilPublickey.length; i++) {
							mv.visitInsn(Opcodes.DUP);
							mv.visitIntInsn(Opcodes.SIPUSH, i);
							mv.visitIntInsn(Opcodes.BIPUSH, yggdrasilPublickey[i]);
							mv.visitInsn(Opcodes.BASTORE);
						}
					}
				};
			}

		});
		addTransformUnit("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService", new LdcTransformUnit("fill_game_profile_transformer", "fillGameProfile", "https://sessionserver.mojang.com/session/minecraft/profile/", apiFillGameProfile));
		addTransformUnit("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService", new LdcTransformUnit("fill_profile_properties_transformer", "fillProfileProperties", "https://sessionserver.mojang.com/session/minecraft/profile/", apiFillGameProfile));
		addTransformUnit("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService", new LdcTransformUnit("join_server_transformer", "<clinit>", "https://sessionserver.mojang.com/session/minecraft/join", apiJoinServer));
		addTransformUnit("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService", new LdcTransformUnit("has_joined_server_transformer", "<clinit>", "https://sessionserver.mojang.com/session/minecraft/hasJoined", apiHasJoinServer));
		addTransformUnit("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService", new NamedTransformUnit("skin_whitelist_domain_transformer") {

			abstract class AbstractSkinWhitelistDomainTransformer extends ClassVisitor {

				AbstractSkinWhitelistDomainTransformer() {
					super(Opcodes.ASM4);
				}

				AbstractSkinWhitelistDomainTransformer(ClassVisitor cv) {
					super(Opcodes.ASM4, cv);
				}

				@Override
				public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
					MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
					if ("<clinit>".equals(name)) {
						return new MethodVisitor(Opcodes.ASM4, mv) {

							int status = 0;

							@Override
							public void visitInsn(int opcode) {
								if (status == 0 && opcode == Opcodes.ICONST_2) {
									status++;
								} else if ((status == 2 || status == 6) && opcode == Opcodes.DUP) {
									status++;
								} else if (status == 3 && opcode == Opcodes.ICONST_0) {
									status++;
								} else if ((status == 5 || status == 9) && opcode == Opcodes.AASTORE) {
									status++;
									if (status == 10) {
										checked(mv);
									}
								} else if (status == 7 && opcode == Opcodes.ICONST_1) {
									status++;
								} else {
									super.visitInsn(opcode);
								}
							}

							@Override
							public void visitTypeInsn(int opcode, String type) {
								if (status == 1 && opcode == Opcodes.ANEWARRAY && "java/lang/String".equals(type)) {
									status++;
								} else {
									super.visitTypeInsn(opcode, type);
								}
							}

							@Override
							public void visitLdcInsn(Object cst) {
								if (status == 4 && ".minecraft.net".equals(cst)) {
									status++;
								} else if (status == 8 && ".mojang.com".equals(cst)) {
									status++;
								} else {
									super.visitLdcInsn(cst);
								}
							}

						};
					} else {
						return mv;
					}
				}

				abstract void checked(MethodVisitor mv);

			}

			@Override
			public ClassVisitor check(final TransformCheckCallback callback) {
				if (skinWhitelist != null) {
					return new AbstractSkinWhitelistDomainTransformer() {

						@Override
						void checked(MethodVisitor mv) {
							callback.allowTransform();
						}

					};
				} else {
					return null;
				}
			}

			@Override
			public ClassVisitor transform(ClassWriter writer) {
				return new AbstractSkinWhitelistDomainTransformer(writer) {

					@Override
					void checked(MethodVisitor mv) {
						mv.visitIntInsn(Opcodes.SIPUSH, skinWhitelist.length + 2);
						mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/String");
						mv.visitInsn(Opcodes.DUP);
						mv.visitInsn(Opcodes.ICONST_0);
						mv.visitLdcInsn(".minecraft.net");
						mv.visitInsn(Opcodes.AASTORE);
						mv.visitInsn(Opcodes.DUP);
						mv.visitInsn(Opcodes.ICONST_1);
						mv.visitLdcInsn(".mojang.com");
						mv.visitInsn(Opcodes.AASTORE);
						for (int i = 0; i < skinWhitelist.length; i++) {
							mv.visitInsn(Opcodes.DUP);
							mv.visitIntInsn(Opcodes.SIPUSH, i + 2);
							mv.visitLdcInsn(skinWhitelist[i]);
							mv.visitInsn(Opcodes.AASTORE);
						}
					}
				};
			}

		});
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
