public class Access {
	public enum Rights {
		BUILD, DESTROY, MODIFY, ENTER
	}

	private int	_rights	= 0;

	public Access(String right) {
		if (right.equalsIgnoreCase("*"))
			_rights = 15;

		if (right.toLowerCase().contains("b"))
			_rights |= 1;
		if (right.toLowerCase().contains("d"))
			_rights |= 2;
		if (right.toLowerCase().contains("m"))
			_rights |= 4;
		if (right.toLowerCase().contains("e"))
			_rights |= 8;
		// actually to return if some1 has NO rights could be usefull ;).
		// if(canNothing())
		// System.out.println("Access invoked without any access ???? POTENTIALY FATAL ERROR IN SERVER!!");
	}

	public boolean canBuild() {
		return ((_rights & 1) == 1);
	}

	public boolean canDestroy() {
		return ((_rights & 2) == 1);
	}

	public boolean canModify() {
		return ((_rights & 4) == 1);
	}

	public boolean canEnter() {
		return ((_rights & 8) == 1);
	}

	public boolean canAll() {
		return (canBuild() && canModify() && canDestroy() && canEnter());
	}

	public boolean canNothing() {
		return (!canBuild() && !canModify() && !canDestroy() && !canEnter());
	}

	public boolean canDo(Rights r) {
		switch (r) {
			case BUILD:
				return canBuild();
			case DESTROY:
				return canDestroy();
			case MODIFY:
				return canModify();
			case ENTER:
				return canEnter();
				// prevent problems, just check for everything :D.
			default:
				return canAll();
		}
	}

	@Override
	public String toString() {
		String rights = "";

		if (canBuild() && canModify() && canDestroy() && canEnter())
			return "*";

		if (canBuild())
			rights += "b";
		if (canDestroy())
			rights += "d";
		if (canModify())
			rights += "m";
		if (canEnter())
			rights += "e";

		return rights;
	}

	public String textual() {
		String text = "";

		if (canBuild())
			text += "Build blocks,";
		if (canDestroy())
			text += "Destroy blocks,";
		if (canModify())
			text += "Chest Access,";
		if (canEnter())
			text += "Enter zone,";

		text = text.substring(0, text.length() - 1);

		String Proper = "";
		String[] split = text.split(",");

		if (split.length == 1)
			return text;

		for (int i = 0; i < split.length; i++) {
			if (i == split.length - 1) {
				Proper = Proper.substring(0, -2);
				Proper += " and " + split[i];
			} else
				Proper += split[i] + ", ";
		}
		return Proper;
	}
}
