package yushijinhun.authlibagent.javaagent;

abstract public class NamedTransformUnit implements TransformUnit {

	private final String name;

	public NamedTransformUnit(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	};
}