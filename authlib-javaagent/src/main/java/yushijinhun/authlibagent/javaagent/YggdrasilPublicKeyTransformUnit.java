package yushijinhun.authlibagent.javaagent;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class YggdrasilPublicKeyTransformUnit extends NamedTransformUnit {

	private byte[] yggdrasilPublickey;

	public YggdrasilPublicKeyTransformUnit(String name, byte[] publickey) {
		super(name);
		this.yggdrasilPublickey = publickey;
	}

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
}
