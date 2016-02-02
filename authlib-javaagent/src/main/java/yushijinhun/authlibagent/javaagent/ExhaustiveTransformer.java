package yushijinhun.authlibagent.javaagent;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Collection;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ExhaustiveTransformer extends AuthlibTransformer {

	private String[] nonTransformablePackages = new String[] { "java.", "javax.", "com.sun.", "com.oracle.", "jdk.", "sun.", "org.apache.", "com.google.", "oracle.", "com.oracle.", "com.paulscode.", "io.netty.", "org.lwjgl.", "net.java.", "org.w3c.", "javassist" };

	public ExhaustiveTransformer(String apiYggdrasilAuthenticate, String apiYggdrasilRefresh, String apiYggdrasilValidate, String apiYgggdrasilInvalidate, String apiYggdarsilSignout, String apiFillGameProfile, String apiJoinServer, String apiHasJoinServer, String apiProfilesLookup, String[] skinWhitelist, byte[] yggdrasilPublickey) {
		super(apiYggdrasilAuthenticate, apiYggdrasilRefresh, apiYggdrasilValidate, apiYgggdrasilInvalidate, apiYggdarsilSignout, apiFillGameProfile, apiJoinServer, apiHasJoinServer, apiProfilesLookup, skinWhitelist, yggdrasilPublickey);
	}

	@Override
	protected void setup() {
		addTransformUnit("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService", new YggdrasilPublicKeyTransformUnit("yggdrasil_publickey_transformer", yggdrasilPublickey));
		addTransformUnit("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService", new SkinWhitelistDomainTransformUnit("skin_whitelist_domain_transformer", skinWhitelist));
	}

	@Override
	public byte[] transform(ClassLoader loader, final String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		if (className != null)
			try {
				byte[] bytecode = classfileBuffer;
				boolean modified = false;
				Collection<TransformUnit> classunits = units.get(className);
				if (classunits != null) {
					bytecode = transformClass(classunits, classfileBuffer);
					modified = true;
				}

				if (canTransform(className)) {
					ClassReader classreader = new ClassReader(bytecode);
					ClassWriter classwriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
					final TransformCheckCallbackImpl callback = new TransformCheckCallbackImpl();
					classreader.accept(new ClassVisitor(Opcodes.ASM4, classwriter) {

						@Override
						public MethodVisitor visitMethod(int access, final String name, String desc, String signature, String[] exceptions) {
							MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
							return new MethodVisitor(Opcodes.ASM4, mv) {

								@Override
								public void visitLdcInsn(Object cst) {
									if (cst instanceof String) {
										String transformed = transformString((String) cst);
										if (transformed != null) {
											super.visitLdcInsn(transformed);
											callback.allowTransform();
											logger.info("transform " + className + ":" + name + " " + cst + " -> " + transformed);
											return;
										}
									}
									super.visitLdcInsn(cst);
								}

							};
						}

					}, 0);

					if (callback.isAllowed || modified) {
						byte[] output = classwriter.toByteArray();
						debugSaveModifiedClass(output, className);
						return classwriter.toByteArray();
					}
				}
			} catch (Throwable e) {
				logger.severe("failed to transform " + className);
				e.printStackTrace();
			}

		return null;
	}

	private boolean canTransform(String classInternalName) {
		String name = classInternalName.replace('/', '.');
		for (String nonTransformablePackage : nonTransformablePackages) {
			if (name.startsWith(nonTransformablePackage)) {
				return false;
			}
		}
		return true;
	}

	private String transformString(String origin) {
		String str = origin;
		str = str.replaceAll("https://sessionserver.mojang.com/session/minecraft/profile/", apiFillGameProfile);
		str = str.replaceAll("https://sessionserver.mojang.com/session/minecraft/join", apiJoinServer);
		str = str.replaceAll("https://sessionserver.mojang.com/session/minecraft/hasJoined", apiHasJoinServer);
		str = str.replaceAll("https://api.mojang.com/profiles/", apiProfilesLookup);
		str = str.replaceAll("https://authserver.mojang.com/authenticate", apiYggdrasilAuthenticate);
		str = str.replaceAll("https://authserver.mojang.com/refresh", apiYggdrasilRefresh);
		str = str.replaceAll("https://authserver.mojang.com/validate", apiYggdrasilValidate);
		str = str.replaceAll("https://authserver.mojang.com/invalidate", apiYgggdrasilInvalidate);
		str = str.replaceAll("https://authserver.mojang.com/signout", apiYggdarsilSignout);
		return origin.equals(str) ? null : str;
	}

}
