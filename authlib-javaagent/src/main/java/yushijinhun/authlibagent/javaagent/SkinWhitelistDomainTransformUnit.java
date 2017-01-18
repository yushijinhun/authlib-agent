package yushijinhun.authlibagent.javaagent;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class SkinWhitelistDomainTransformUnit extends NamedTransformUnit {

	private String[] skinWhitelist;

	public SkinWhitelistDomainTransformUnit(String name, String[] skinWhitelist) {
		super(name);
		this.skinWhitelist = skinWhitelist;
	}

	abstract class AbstractSkinWhitelistDomainTransformer extends ClassVisitor {

		AbstractSkinWhitelistDomainTransformer() {
			super(Opcodes.ASM5);
		}

		AbstractSkinWhitelistDomainTransformer(ClassVisitor cv) {
			super(Opcodes.ASM5, cv);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
			if ("<clinit>".equals(name)) {
				return new MethodVisitor(Opcodes.ASM5, mv) {

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
}