package utils;

public class Utils {
	
	//Catch error codes that would usually need a try/catch
	public static void invokeProcedure(Procedure procedure, boolean printStackTrace) {
		
		try {
			procedure.invoke();
		} catch (Exception e) {
			if (printStackTrace) {
				e.printStackTrace();
			}
		}
	}

}
