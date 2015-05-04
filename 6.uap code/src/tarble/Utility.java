package tarble;

import java.util.ArrayList;
import java.util.List;

public class Utility {
	public static <T> List<T> merge(List<T> list1, List<T> list2) {
		List<T> retList = new ArrayList<T>();
		retList.addAll(list1);
		retList.addAll(list2);
		return retList;
	}

}
