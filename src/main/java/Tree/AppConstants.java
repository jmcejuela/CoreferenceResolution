package Tree;

import java.util.ArrayList;
import java.util.Arrays;

public class AppConstants {
   public static ArrayList<String>notAllowed;
  
  
  static String[] keyWordDefinite = { "protein", "gene", "factor", "molecule",
			"element", "receptor","complex","construct"};
   public  static ArrayList<String>definiteNounAllow;
   public static ArrayList<String>proteinsinArticle;
   public static ArrayList<String>notAllowedDefinite;
   static String []DefintieNotAllowed={"another", "any", "each", "only", "some", "few", "several", "some"};
   public static ArrayList<String>definite_not_allowed;
   public static void init(){
	   notAllowed= new ArrayList<String>();
	   notAllowedDefinite= new ArrayList<String>();
	   definiteNounAllow= new ArrayList<String>();
	   definite_not_allowed= new ArrayList<String>();
	   notAllowed.add("S-");
	   notAllowed.add("S");
	   notAllowed.add("CP");
	   notAllowed.add("NP-C");
	   notAllowedDefinite.add("S-");
	   notAllowedDefinite.add("PP");
	   notAllowedDefinite.add("CP");
	   notAllowedDefinite.add("NP-C");	 	
	   for(int i=0;i<keyWordDefinite.length;i++)
		   definiteNounAllow.add(keyWordDefinite[i]);   
	   for(int i=0;i<DefintieNotAllowed.length;i++)
		   definite_not_allowed.add(DefintieNotAllowed[i]);
   }
}
