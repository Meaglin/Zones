import java.util.ArrayList;

public class Access {
	public enum Rights {
		BUILD(1, "b", "Build blocks"),
		DESTROY(2, "d", "Destroy blocks"),
		MODIFY(4, "m", "Chest access"),
		ENTER(8, "e", "Enter zone"),
		ALL(15, "*", "Anything & everything");
		
		private int flag;
		private String code;
		private String textual;
		private static ArrayList<Rights> rights;
		
		private Rights(int flag, String code, String textual) {
			this.flag = flag;
			this.code = code;
			put(this);
		}
		
		private static void put(Rights right) {
			// No nullpointer exceptions please :D
			if (rights == null)
				rights = new ArrayList<Rights>();
			
			rights.add(right);
		}

		public int getFlag() { return flag; }
		public String getCode() { return code; }
		public String getTextual() { return textual; }
		public static ArrayList<Rights> getRights() { return rights; }
		public boolean canDo(int rights) {
			return (rights & flag) != 0;
		}
	}

	private int	_rights	= 0;

	public Access(int right) {
		_rights = right;
	}
	
	public Access(String rightsString) {
		for (Rights right: Rights.getRights())
			if (rightsString.toLowerCase().contains(right.getCode()))
				_rights |= right.getFlag();
			
		// actually to return if some1 has NO rights could be usefull ;).
		// if(canNothing())
		// System.out.println("Access invoked without any access ???? POTENTIALY FATAL ERROR IN SERVER!!");
	}

	public boolean canDo(Rights right) { return right.canDo(_rights); }
	public boolean canBuild() { return canDo(Rights.BUILD); }
	public boolean canDestroy() { return canDo(Rights.DESTROY); }
	public boolean canModify() { return canDo(Rights.MODIFY); }
	public boolean canEnter() { return canDo(Rights.ENTER); }
	public boolean canAll() { return canDo(Rights.ALL); }
	public boolean canNothing() {
		return (_rights & Rights.ALL.flag) == 0;
	}


	@Override
	public String toString() {
		// Short circuit on 'all'
		if (canDo(Rights.ALL))
			return Rights.ALL.getCode();
		
		// Build list of access codes.
		String rights = "";
		for (Rights right: Rights.getRights())
			if (canDo( right ))
				rights += right.getCode();

		return rights;
	}

	public String textual() {
		
		// Short circuit on 'all'
		if (canDo(Rights.ALL))
			return Rights.ALL.getTextual();
		
		// Build list of access codes.
		String text = "";
		for (Rights right: Rights.getRights())
			if (canDo( right ))
				text += right.getCode() + ", ";

		// Remove last comma.
		text = text.substring(0, text.length() - 2);
		
		// Replace last comma with "and"
		return text.substring(0, text.lastIndexOf(',')) + " and" + text.substring( text.lastIndexOf(',')+1, text.length());
	}
}
